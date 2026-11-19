/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.common.keymaster.client.zookeeper_2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.ProtectableCreateModableWithPath;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.syncope.common.keymaster.client.api.KeymasterException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ZookeeperConfParamOps_2LLMTest {

    @Mock
    private CuratorFramework client;

    @Mock
    private ExistsBuilder existsBuilder;

    @Mock
    private ProtectableCreateModableWithPath createBuilder;

    @Mock
    private GetChildrenBuilder getChildrenBuilder;

    @Mock
    private GetDataBuilder getDataBuilder;

    @Mock
    private SetDataBuilder setDataBuilder;

    @Mock
    private DeleteBuilder deleteBuilder;

    @Mock
    private Stat stat;

    private ZookeeperConfParamOps_2 confParamOps;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        confParamOps = new ZookeeperConfParamOps_2(client);

        when(client.checkExists()).thenReturn(existsBuilder);
        when(client.create()).thenReturn((CreateBuilder) createBuilder);
        when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createBuilder);
        when(client.getChildren()).thenReturn(getChildrenBuilder);
        when(client.getData()).thenReturn(getDataBuilder);
        when(client.setData()).thenReturn(setDataBuilder);
        when(client.delete()).thenReturn(deleteBuilder);
    }

    @Test
    public void testBuildConfPath() {
        assertEquals("/conf/testDomain/testKey", ZookeeperConfParamOps_2.buildConfPath("testDomain", "testKey"));
        assertEquals("/conf", ZookeeperConfParamOps_2.buildConfPath());
    }

    @Test
    public void testList_existingDomain() throws Exception {
        when(existsBuilder.forPath("/conf/domain1")).thenReturn(stat);
        when(getChildrenBuilder.forPath("/conf/domain1")).thenReturn(Collections.singletonList("param1"));
        when(getDataBuilder.forPath("/conf/domain1/param1")).thenReturn("\"value1\"".getBytes());

        Map<String, Object> result = confParamOps.list("domain1");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("value1", result.get("param1"));
    }

    @Test
    public void testList_nonExistingDomain() throws Exception {
        when(existsBuilder.forPath("/conf/domain1")).thenReturn(null);
        when(createBuilder.forPath("/conf/domain1")).thenReturn("/conf/domain1");
        when(getChildrenBuilder.forPath("/conf/domain1")).thenReturn(Collections.emptyList());

        Map<String, Object> result = confParamOps.list("domain1");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test(expected = KeymasterException.class)
    public void testList_exception() throws Exception {
        when(existsBuilder.forPath(anyString())).thenThrow(new RuntimeException("ZK Error"));
        confParamOps.list("domain1");
    }

    @Test
    public void testGet_found() throws Exception {
        when(getDataBuilder.forPath("/conf/domain1/key1")).thenReturn("\"val1\"".getBytes());

        String value = confParamOps.get("domain1", "key1", "default", String.class);
        assertEquals("val1", value);
    }

    @Test
    public void testGet_noNode() throws Exception {
        when(getDataBuilder.forPath("/conf/domain1/key1"))
                .thenThrow(new KeeperException.NoNodeException("/conf/domain1/key1"));

        String value = confParamOps.get("domain1", "key1", "default", String.class);
        assertEquals("default", value);
    }

    @Test(expected = KeymasterException.class)
    public void testGet_exception() throws Exception {
        when(getDataBuilder.forPath(anyString())).thenThrow(new RuntimeException("ZK Error"));
        confParamOps.get("domain1", "key1", "default", String.class);
    }

    @Test
    public void testSet_nodeExists() throws Exception {
        when(existsBuilder.forPath("/conf/domain1/key1")).thenReturn(stat);
        when(setDataBuilder.forPath(anyString(), any(byte[].class))).thenReturn(stat);

        confParamOps.set("domain1", "key1", "val1");
        verify(setDataBuilder).forPath(anyString(), any(byte[].class));
    }

    @Test
    public void testSet_nodeNotExists() throws Exception {
        when(existsBuilder.forPath("/conf/domain1/key1")).thenReturn(null);
        when(createBuilder.forPath("/conf/domain1/key1")).thenReturn("/conf/domain1/key1");
        when(setDataBuilder.forPath(anyString(), any(byte[].class))).thenReturn(stat);

        confParamOps.set("domain1", "key1", "val1");
        verify(createBuilder).forPath("/conf/domain1/key1");
    }

    @Test
    public void testSet_nullValue() throws Exception {
        when(deleteBuilder.forPath("/conf/domain1/key1")).thenReturn(null);

        confParamOps.set("domain1", "key1", null);
        verify(deleteBuilder).forPath("/conf/domain1/key1");
    }

    @Test(expected = KeymasterException.class)
    public void testSet_exception() throws Exception {
        when(existsBuilder.forPath(anyString())).thenThrow(new RuntimeException("ZK Error"));
        confParamOps.set("domain1", "key1", "val1");
    }

    @Test
    public void testRemove() throws Exception {
        when(deleteBuilder.forPath("/conf/domain1/key1")).thenReturn(null);

        confParamOps.remove("domain1", "key1");
        verify(deleteBuilder).forPath("/conf/domain1/key1");
    }

    @Test(expected = KeymasterException.class)
    public void testRemove_exception() throws Exception {
        doThrow(new RuntimeException("ZK Error")).when(deleteBuilder).forPath(anyString());
        confParamOps.remove("domain1", "key1");
    }
}