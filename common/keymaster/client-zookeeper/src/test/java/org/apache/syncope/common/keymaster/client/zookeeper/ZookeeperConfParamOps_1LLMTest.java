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
package org.apache.syncope.common.keymaster.client.zookeeper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ZookeeperConfParamOps_1LLMTest {

private static final JsonMapper MAPPER = JsonMapper.builder().findAndAddModules().build();

private static final String DOMAIN = "Master";

private static final String KEY = "test.param";

@Mock
private CuratorFramework client;

@Mock
private ExistsBuilder existsBuilder;

@Mock
private CreateBuilder createBuilder;

@Mock
private ProtectACLCreateModeStatPathAndBytesable<String> createParentBuilder;

@Mock
private GetChildrenBuilder getChildrenBuilder;

@Mock
private GetDataBuilder getDataBuilder;

@Mock
private SetDataBuilder setDataBuilder;

@Mock
private DeleteBuilder deleteBuilder;

private ZookeeperConfParamOps_1 confParamOps;

@Before
public void setUp() {
    confParamOps = new ZookeeperConfParamOps_1(client);
}

// -------------------------------------------------------------------------
// buildConfPath tests
// -------------------------------------------------------------------------

@Test
public void testBuildConfPathSinglePart() {
    assertEquals("/conf/Master", ZookeeperConfParamOps_1.buildConfPath("Master"));
}

@Test
public void testBuildConfPathMultipleParts() {
    assertEquals("/conf/Master/my.key", ZookeeperConfParamOps_1.buildConfPath("Master", "my.key"));
}

@Test
public void testBuildConfPathNoParts() {
    assertEquals("/conf/", ZookeeperConfParamOps_1.buildConfPath());
}

// -------------------------------------------------------------------------
// list(String domain) tests
// -------------------------------------------------------------------------

@Test
public void testListWhenDomainNodeDoesNotExistCreatesDomainAndReturnsEmptyMap() throws Exception {
    String domainPath = "/conf/" + DOMAIN;

    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(domainPath)).thenReturn(null);

    when(client.create()).thenReturn(createBuilder);
    when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createParentBuilder);
    when(createParentBuilder.forPath(domainPath)).thenReturn(domainPath);

    when(client.getChildren()).thenReturn(getChildrenBuilder);
    when(getChildrenBuilder.forPath(domainPath)).thenReturn(Collections.emptyList());

    Map<String, Object> result = confParamOps.list(DOMAIN);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    assertTrue(result instanceof TreeMap);
    verify(createParentBuilder).forPath(domainPath);
}

@Test
public void testListWhenDomainNodeExistsReturnsSortedPopulatedMap() throws Exception {
    String domainPath = "/conf/" + DOMAIN;
    List<String> children = Arrays.asList("z.key", "a.key", "m.key");

    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(domainPath)).thenReturn(new Stat());

    when(client.getChildren()).thenReturn(getChildrenBuilder);
    when(getChildrenBuilder.forPath(domainPath)).thenReturn(children);

    when(client.getData()).thenReturn(getDataBuilder);
    when(getDataBuilder.forPath(domainPath + "/z.key")).thenReturn(MAPPER.writeValueAsBytes("stringVal"));
    when(getDataBuilder.forPath(domainPath + "/a.key")).thenReturn(MAPPER.writeValueAsBytes(42));
    when(getDataBuilder.forPath(domainPath + "/m.key")).thenReturn(MAPPER.writeValueAsBytes(true));

    Map<String, Object> result = confParamOps.list(DOMAIN);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(Arrays.asList("a.key", "m.key", "z.key"), Arrays.asList(result.keySet().toArray(new String[0])));
    assertEquals(42, result.get("a.key"));
    assertEquals(true, result.get("m.key"));
    assertEquals("stringVal", result.get("z.key"));
    verify(client, never()).create();
}

@Test(expected = KeymasterException.class)
public void testListThrowsKeymasterExceptionOnCheckExistsFailure() throws Exception {
    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath("/conf/" + DOMAIN)).thenThrow(new RuntimeException("ZK connection lost"));

    confParamOps.list(DOMAIN);
}

@Test(expected = KeymasterException.class)
public void testListThrowsKeymasterExceptionOnGetChildrenFailure() throws Exception {
    String domainPath = "/conf/" + DOMAIN;
    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(domainPath)).thenReturn(new Stat());

    when(client.getChildren()).thenReturn(getChildrenBuilder);
    when(getChildrenBuilder.forPath(domainPath)).thenThrow(new KeeperException.SystemErrorException());

    confParamOps.list(DOMAIN);
}

@Test(expected = KeymasterException.class)
public void testListThrowsKeymasterExceptionOnInvalidJsonData() throws Exception {
    String domainPath = "/conf/" + DOMAIN;
    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(domainPath)).thenReturn(new Stat());

    when(client.getChildren()).thenReturn(getChildrenBuilder);
    when(getChildrenBuilder.forPath(domainPath)).thenReturn(Collections.singletonList(KEY));

    when(client.getData()).thenReturn(getDataBuilder);
    when(getDataBuilder.forPath(domainPath + "/" + KEY)).thenReturn("invalid-json{".getBytes());

    confParamOps.list(DOMAIN);
}

// -------------------------------------------------------------------------
// get(String domain, String key, T defaultValue, Class<T> reference) tests
// -------------------------------------------------------------------------

@Test
public void testGetReturnsDeserializedStringValue() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    when(client.getData()).thenReturn(getDataBuilder);
    when(getDataBuilder.forPath(fullPath)).thenReturn(MAPPER.writeValueAsBytes("actualValue"));

    String result = confParamOps.get(DOMAIN, KEY, "defaultValue", String.class);

    assertEquals("actualValue", result);
}

@Test
public void testGetReturnsDeserializedIntegerValue() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    when(client.getData()).thenReturn(getDataBuilder);
    when(getDataBuilder.forPath(fullPath)).thenReturn(MAPPER.writeValueAsBytes(100));

    Integer result = confParamOps.get(DOMAIN, KEY, 0, Integer.class);

    assertEquals(Integer.valueOf(100), result);
}

@Test
public void testGetReturnsDefaultValueWhenNoNodeExceptionOccurs() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    when(client.getData()).thenReturn(getDataBuilder);
    when(getDataBuilder.forPath(fullPath)).thenThrow(new KeeperException.NoNodeException(fullPath));

    String result = confParamOps.get(DOMAIN, KEY, "fallback", String.class);

    assertEquals("fallback", result);
}

@Test
public void testGetReturnsNullWhenNoNodeExceptionOccursAndDefaultIsNull() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    when(client.getData()).thenReturn(getDataBuilder);
    when(getDataBuilder.forPath(fullPath)).thenThrow(new KeeperException.NoNodeException(fullPath));

    String result = confParamOps.get(DOMAIN, KEY, null, String.class);

    assertNull(result);
}

@Test
public void testGetReturnsDefaultValueWhenJsonValueIsNull() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    when(client.getData()).thenReturn(getDataBuilder);
    when(getDataBuilder.forPath(fullPath)).thenReturn(MAPPER.writeValueAsBytes(null));

    String result = confParamOps.get(DOMAIN, KEY, "defaultOnNull", String.class);

    assertEquals("defaultOnNull", result);
}

@Test(expected = KeymasterException.class)
public void testGetThrowsKeymasterExceptionOnOtherKeeperException() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    when(client.getData()).thenReturn(getDataBuilder);
    when(getDataBuilder.forPath(fullPath)).thenThrow(new KeeperException.ConnectionLossException());

    confParamOps.get(DOMAIN, KEY, "default", String.class);
}

@Test(expected = KeymasterException.class)
public void testGetThrowsKeymasterExceptionOnDeserializationError() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    when(client.getData()).thenReturn(getDataBuilder);
    when(getDataBuilder.forPath(fullPath)).thenReturn(MAPPER.writeValueAsBytes("notAnInteger"));

    confParamOps.get(DOMAIN, KEY, 10, Integer.class);
}

// -------------------------------------------------------------------------
// set(String domain, String key, T value) tests
// -------------------------------------------------------------------------

@Test
public void testSetCreatesNodeAndWritesDataWhenNodeDoesNotExist() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    String valueToSet = "newParamValue";
    byte[] expectedBytes = MAPPER.writeValueAsBytes(valueToSet);

    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(fullPath)).thenReturn(null);

    when(client.create()).thenReturn(createBuilder);
    when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createParentBuilder);
    when(createParentBuilder.forPath(fullPath)).thenReturn(fullPath);

    when(client.setData()).thenReturn(setDataBuilder);
    when(setDataBuilder.forPath(eq(fullPath), eq(expectedBytes))).thenReturn(new Stat());

    confParamOps.set(DOMAIN, KEY, valueToSet);

    verify(createParentBuilder).forPath(fullPath);
    verify(setDataBuilder).forPath(eq(fullPath), eq(expectedBytes));
}

@Test
public void testSetUpdatesDataWithoutCreatingNodeWhenNodeAlreadyExists() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    Integer valueToSet = 999;
    byte[] expectedBytes = MAPPER.writeValueAsBytes(valueToSet);

    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(fullPath)).thenReturn(new Stat());

    when(client.setData()).thenReturn(setDataBuilder);
    when(setDataBuilder.forPath(eq(fullPath), eq(expectedBytes))).thenReturn(new Stat());

    confParamOps.set(DOMAIN, KEY, valueToSet);

    verify(client, never()).create();
    verify(setDataBuilder).forPath(eq(fullPath), eq(expectedBytes));
}

@Test
public void testSetWithNullValueDelegatesToRemove() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    when(client.delete()).thenReturn(deleteBuilder);

    confParamOps.set(DOMAIN, KEY, null);

    verify(deleteBuilder).forPath(fullPath);
    verify(client, never()).checkExists();
    verify(client, never()).setData();
}

@Test(expected = KeymasterException.class)
public void testSetThrowsKeymasterExceptionWhenCheckExistsFails() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(fullPath)).thenThrow(new KeeperException.SessionExpiredException());

    confParamOps.set(DOMAIN, KEY, "someValue");
}

@Test(expected = KeymasterException.class)
public void testSetThrowsKeymasterExceptionWhenCreateFails() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(fullPath)).thenReturn(null);

    when(client.create()).thenReturn(createBuilder);
    when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createParentBuilder);
    when(createParentBuilder.forPath(fullPath)).thenThrow(new KeeperException.NodeExistsException(fullPath));

    confParamOps.set(DOMAIN, KEY, "someValue");
}

@Test(expected = KeymasterException.class)
public void testSetThrowsKeymasterExceptionWhenSetDataFails() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(fullPath)).thenReturn(new Stat());

    when(client.setData()).thenReturn(setDataBuilder);
    when(setDataBuilder.forPath(eq(fullPath), any(byte[].class)))
            .thenThrow(new KeeperException.BadVersionException(fullPath));

    confParamOps.set(DOMAIN, KEY, "someValue");
}

@Test
public void testSetThrowsKeymasterExceptionWhenSerializationFails() {
    Object unserializable = new Object(); // Jackson fails on empty bean with no properties by default

    try {
        confParamOps.set(DOMAIN, KEY, unserializable);
        fail("Expected KeymasterException to be thrown");
    } catch (KeymasterException e) {
        assertNotNull(e.getCause());
    }
}

// -------------------------------------------------------------------------
// remove(String domain, String key) tests
// -------------------------------------------------------------------------

@Test
public void testRemoveDeletesNodeSuccessfully() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    when(client.delete()).thenReturn(deleteBuilder);

    confParamOps.remove(DOMAIN, KEY);

    verify(deleteBuilder).forPath(fullPath);
}

@Test(expected = KeymasterException.class)
public void testRemoveThrowsKeymasterExceptionWhenDeleteFails() throws Exception {
    String fullPath = "/conf/" + DOMAIN + "/" + KEY;
    when(client.delete()).thenReturn(deleteBuilder);
    when(deleteBuilder.forPath(anyString())).thenThrow(new KeeperException.NoNodeException(fullPath));

    confParamOps.remove(DOMAIN, KEY);
}
}