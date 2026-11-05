/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.common.keymaster.client.zookeeper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.ProtectACLCreateModeStatPathAndBytesable;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.syncope.common.keymaster.client.api.KeymasterException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

public class ZookeeperConfParamOpsLLM3Test {

    private static final String DOMAIN = "testDomain";

    private static final String KEY = "testKey";

    private CuratorFramework client;

    private ExistsBuilder existsBuilder;

    private CreateBuilder createBuilder;

    private ProtectACLCreateModeStatPathAndBytesable<String> createContainersBuilder;

    private GetChildrenBuilder getChildrenBuilder;

    private GetDataBuilder getDataBuilder;

    private SetDataBuilder setDataBuilder;

    private DeleteBuilder deleteBuilder;

    private ZookeeperConfParamOps ops;

    @Before
    public void setUp() {
        client = mock(CuratorFramework.class);

        existsBuilder = mock(ExistsBuilder.class);
        createBuilder = mock(CreateBuilder.class);
        createContainersBuilder = mock(ProtectACLCreateModeStatPathAndBytesable.class);
        getChildrenBuilder = mock(GetChildrenBuilder.class);
        getDataBuilder = mock(GetDataBuilder.class);
        setDataBuilder = mock(SetDataBuilder.class);
        deleteBuilder = mock(DeleteBuilder.class);

        when(client.checkExists()).thenReturn(existsBuilder);
        when(client.create()).thenReturn(createBuilder);
        when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createContainersBuilder);
        when(client.getChildren()).thenReturn(getChildrenBuilder);
        when(client.getData()).thenReturn(getDataBuilder);
        when(client.setData()).thenReturn(setDataBuilder);
        when(client.delete()).thenReturn(deleteBuilder);

        ops = new ZookeeperConfParamOps(client);
    }

    /**
     * Expert 1 - typical case: list() returns all children and their JSON-decoded values.
     */
    @Test
    public void listReturnsAllDomainParameters() throws Exception {
        when(existsBuilder.forPath("/conf/" + DOMAIN)).thenReturn(mockStat());
        when(getChildrenBuilder.forPath("/conf/" + DOMAIN)).thenReturn(List.of("key1", "key2"));
        when(getDataBuilder.forPath("/conf/" + DOMAIN + "/key1")).thenReturn("\"value1\"".getBytes());
        when(getDataBuilder.forPath("/conf/" + DOMAIN + "/key2")).thenReturn("42".getBytes());

        Map<String, Object> result = ops.list(DOMAIN);

        Map<String, Object> expected = new HashMap<>();
        expected.put("key1", "value1");
        expected.put("key2", 42);

        assertEquals(expected, result);
    }

    /**
     * Expert 2 - edge case: when the domain node does not exist, list() creates it
     * and returns an empty map.
     */
    @Test
    public void listCreatesMissingDomainAndReturnsEmptyMap() throws Exception {
        when(existsBuilder.forPath("/conf/" + DOMAIN)).thenReturn(null);
        when(createContainersBuilder.forPath("/conf/" + DOMAIN)).thenReturn("/conf/" + DOMAIN);
        when(getChildrenBuilder.forPath("/conf/" + DOMAIN)).thenReturn(List.of());

        Map<String, Object> result = ops.list(DOMAIN);

        assertEquals(Map.of(), result);
        verify(createContainersBuilder).forPath("/conf/" + DOMAIN);
    }

    /**
     * Expert 3 - error case: any Curator/ZooKeeper/Jackson failure is wrapped
     * in KeymasterException.
     */
    @Test
    public void listWrapsFailuresInKeymasterException() throws Exception {
        when(existsBuilder.forPath("/conf/" + DOMAIN))
                .thenThrow(new IllegalStateException("ZooKeeper unavailable"));

        try {
            ops.list(DOMAIN);
            fail("Expected KeymasterException");
        } catch (KeymasterException e) {
            assertEquals(IllegalStateException.class, e.getCause().getClass());
        }
    }

    /**
     * Expert 1 - typical case: get() deserializes the stored JSON value.
     */
    @Test
    public void getReturnsStoredValue() throws Exception {
        when(getDataBuilder.forPath("/conf/" + DOMAIN + "/" + KEY))
                .thenReturn("\"storedValue\"".getBytes());

        String value = ops.get(DOMAIN, KEY, "defaultValue", String.class);

        assertEquals("storedValue", value);
    }

    /**
     * Expert 2 - edge case: get() returns the default value when the ZooKeeper
     * node does not exist.
     */
    @Test
    public void getReturnsDefaultValueWhenNodeIsMissing() throws Exception {
        KeeperException.NoNodeException noNode =
                new KeeperException.NoNodeException("/conf/" + DOMAIN + "/" + KEY);
        when(getDataBuilder.forPath("/conf/" + DOMAIN + "/" + KEY)).thenThrow(noNode);

        String value = ops.get(DOMAIN, KEY, "defaultValue", String.class);

        assertEquals("defaultValue", value);
    }

    /**
     * Expert 3 - edge case: get() returns null when the node is missing and
     * the default value is null.
     */
    @Test
    public void getReturnsNullWhenNodeIsMissingAndDefaultIsNull() throws Exception {
        KeeperException.NoNodeException noNode =
                new KeeperException.NoNodeException("/conf/" + DOMAIN + "/" + KEY);
        when(getDataBuilder.forPath("/conf/" + DOMAIN + "/" + KEY)).thenThrow(noNode);

        String value = ops.get(DOMAIN, KEY, null, String.class);

        assertNull(value);
    }

    /**
     * Expert 3 - error case: get() wraps unexpected deserialization or Curator failures.
     */
    @Test
    public void getWrapsUnexpectedFailuresInKeymasterException() throws Exception {
        when(getDataBuilder.forPath("/conf/" + DOMAIN + "/" + KEY))
                .thenThrow(new IllegalStateException("Connection lost"));

        try {
            ops.get(DOMAIN, KEY, "defaultValue", String.class);
            fail("Expected KeymasterException");
        } catch (KeymasterException e) {
            assertEquals(IllegalStateException.class, e.getCause().getClass());
        }
    }

    /**
     * Expert 1 - typical case: set() creates a missing node and stores the JSON
     * representation.
     */
    @Test
    public void setCreatesMissingNodeAndStoresValue() throws Exception {
        when(existsBuilder.forPath("/conf/" + DOMAIN + "/" + KEY)).thenReturn(null);
        when(createContainersBuilder.forPath("/conf/" + DOMAIN + "/" + KEY))
                .thenReturn("/conf/" + DOMAIN + "/" + KEY);

        ops.set(DOMAIN, KEY, "newValue");

        verify(createContainersBuilder).forPath("/conf/" + DOMAIN + "/" + KEY);
        verify(setDataBuilder).forPath("/conf/" + DOMAIN + "/" + KEY, "\"newValue\"".getBytes());
    }

    /**
     * Expert 2 - typical case: set() updates an existing node without creating it again.
     */
    @Test
    public void setUpdatesExistingNode() throws Exception {
        when(existsBuilder.forPath("/conf/" + DOMAIN + "/" + KEY)).thenReturn(mockStat());

        ops.set(DOMAIN, KEY, "updatedValue");

        verify(createContainersBuilder, never()).forPath(anyString());
        verify(setDataBuilder).forPath("/conf/" + DOMAIN + "/" + KEY, "\"updatedValue\"".getBytes());
    }

    /**
     * Expert 2 - edge case: setting null delegates to remove().
     */
    @Test
    public void setNullValueRemovesKey() throws Exception {
        ops.set(DOMAIN, KEY, null);

        verify(deleteBuilder).forPath("/conf/" + DOMAIN + "/" + KEY);
        verify(setDataBuilder, never()).forPath(anyString(), any(byte[].class));
    }

    /**
     * Expert 3 - error case: set() wraps failures while creating or updating a node.
     */
    @Test
    public void setWrapsFailuresInKeymasterException() throws Exception {
        when(existsBuilder.forPath("/conf/" + DOMAIN + "/" + KEY)).thenReturn(mockStat());
        when(setDataBuilder.forPath(eq("/conf/" + DOMAIN + "/" + KEY), any(byte[].class)))
                .thenThrow(new IllegalStateException("Cannot update node"));

        try {
            ops.set(DOMAIN, KEY, "value");
            fail("Expected KeymasterException");
        } catch (KeymasterException e) {
            assertEquals(IllegalStateException.class, e.getCause().getClass());
        }
    }

    /**
     * Expert 1 - typical case: remove() deletes the requested ZooKeeper node.
     */
    @Test
    public void removeDeletesRequestedNode() throws Exception {
        ops.remove(DOMAIN, KEY);

        verify(deleteBuilder, times(1)).forPath("/conf/" + DOMAIN + "/" + KEY);
    }

    /**
     * Expert 2 - edge case: remove() propagates a missing-node failure as
     * KeymasterException. The implementation does not treat NoNode as a successful no-op.
     */
    @Test
    public void removeWrapsMissingNodeFailureInKeymasterException() throws Exception {
        KeeperException.NoNodeException noNode =
                new KeeperException.NoNodeException("/conf/" + DOMAIN + "/" + KEY);
        when(deleteBuilder.forPath("/conf/" + DOMAIN + "/" + KEY)).thenThrow(noNode);

        try {
            ops.remove(DOMAIN, KEY);
            fail("Expected KeymasterException");
        } catch (KeymasterException e) {
            assertSame(noNode, e.getCause());
        }
    }

    /**
     * Expert 3 - error case: remove() wraps any other delete failure.
     */
    @Test
    public void removeWrapsUnexpectedFailuresInKeymasterException() throws Exception {
        doThrow(new IllegalStateException("Connection lost"))
                .when(deleteBuilder).forPath("/conf/" + DOMAIN + "/" + KEY);

        try {
            ops.remove(DOMAIN, KEY);
            fail("Expected KeymasterException");
        } catch (KeymasterException e) {
            assertEquals(IllegalStateException.class, e.getCause().getClass());
        }
    }

    private static Stat mockStat() {
        return new Stat();
    }
}