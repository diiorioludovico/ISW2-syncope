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

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;import org.apache.curator.framework.api.DeleteBuilder;import org.apache.curator.framework.api.ExistsBuilder;import org.apache.curator.framework.api.GetChildrenBuilder;import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.ProtectACLCreateModeStage;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.syncope.common.keymaster.client.api.KeymasterException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ZookeeperConfParamOps_3LLMTest {

private CuratorFramework client;
private ZookeeperConfParamOps_3 confParamOps;
private JsonMapper mapper;

@Before
public void setUp() {
    client = mock(CuratorFramework.class);
    confParamOps = new ZookeeperConfParamOps_3(client);
    mapper = ZookeeperConfParamOps_3.MAPPER;
}

@Test
public void testList_DomainExists() throws Exception {
    String domain = "Master";
    String confPath = "/conf/" + domain;
    String childPath = confPath + "/param1";

    ExistsBuilder existsBuilder = mock(ExistsBuilder.class);
    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(confPath)).thenReturn(new Stat());

    GetChildrenBuilder childrenBuilder = mock(GetChildrenBuilder.class);
    when(client.getChildren()).thenReturn(childrenBuilder);
    when(childrenBuilder.forPath(confPath)).thenReturn(Arrays.asList("param1"));

    GetDataBuilder getDataBuilder = mock(GetDataBuilder.class);
    when(client.getData()).thenReturn(getDataBuilder);
    when(getDataBuilder.forPath(childPath)).thenReturn(mapper.writeValueAsBytes("value1"));

    Map<String, Object> result = confParamOps.list(domain);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("value1", result.get("param1"));
}

@Test
public void testList_DomainNotExists() throws Exception {
    String domain = "Master";
    String confPath = "/conf/" + domain;

    ExistsBuilder existsBuilder = mock(ExistsBuilder.class);
    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(confPath)).thenReturn(null);

    CreateBuilder createBuilder = mock(CreateBuilder.class);
    when(client.create()).thenReturn(createBuilder);
    ProtectACLCreateModeStage aclStage = mock(ProtectACLCreateModeStage.class);
    when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(aclStage);
    when(aclStage.forPath(confPath)).thenReturn(confPath);

    GetChildrenBuilder childrenBuilder = mock(GetChildrenBuilder.class);
    when(client.getChildren()).thenReturn(childrenBuilder);
    when(childrenBuilder.forPath(confPath)).thenReturn(Arrays.asList());

    Map<String, Object> result = confParamOps.list(domain);
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(createBuilder).creatingParentContainersIfNeeded();
}

@Test(expected = KeymasterException.class)
public void testList_Exception() throws Exception {
    String domain = "Master";
    ExistsBuilder existsBuilder = mock(ExistsBuilder.class);
    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(anyString())).thenThrow(new RuntimeException("ZooKeeper error"));

    confParamOps.list(domain);
}

@Test
public void testGet_NodeExists() throws Exception {
    String domain = "Master";
    String key = "param1";
    String path = "/conf/" + domain + "/" + key;

    GetDataBuilder getDataBuilder = mock(GetDataBuilder.class);
    when(client.getData()).thenReturn(getDataBuilder);
    when(getDataBuilder.forPath(path)).thenReturn(mapper.writeValueAsBytes("testValue"));

    String result = confParamOps.get(domain, key, "default", String.class);
    assertEquals("testValue", result);
}

@Test
public void testGet_NoNodeException() throws Exception {
    String domain = "Master";
    String key = "param1";
    String path = "/conf/" + domain + "/" + key;

    GetDataBuilder getDataBuilder = mock(GetDataBuilder.class);
    when(client.getData()).thenReturn(getDataBuilder);
    when(getDataBuilder.forPath(path)).thenThrow(new KeeperException.NoNodeException(path));

    String result = confParamOps.get(domain, key, "default", String.class);
    assertEquals("default", result);
}

@Test(expected = KeymasterException.class)
public void testGet_OtherException() throws Exception {
    String domain = "Master";
    String key = "param1";
    String path = "/conf/" + domain + "/" + key;

    GetDataBuilder getDataBuilder = mock(GetDataBuilder.class);
    when(client.getData()).thenReturn(getDataBuilder);
    when(getDataBuilder.forPath(path)).thenThrow(new RuntimeException("ZK error"));

    confParamOps.get(domain, key, "default", String.class);
}

@Test
public void testSet_NullValue() throws Exception {
    String domain = "Master";
    String key = "param1";
    String path = "/conf/" + domain + "/" + key;

    DeleteBuilder deleteBuilder = mock(DeleteBuilder.class);
    when(client.delete()).thenReturn(deleteBuilder);
    when(deleteBuilder.forPath(path)).thenReturn(null);

    confParamOps.set(domain, key, null);
    verify(deleteBuilder).forPath(path);
}

@Test
public void testSet_NodeNotExists() throws Exception {
    String domain = "Master";
    String key = "param1";
    String path = "/conf/" + domain + "/" + key;

    ExistsBuilder existsBuilder = mock(ExistsBuilder.class);
    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(path)).thenReturn(null);

    CreateBuilder createBuilder = mock(CreateBuilder.class);
    when(client.create()).thenReturn(createBuilder);
    ProtectACLCreateModeStage aclStage = mock(ProtectACLCreateModeStage.class);
    when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(aclStage);
    when(aclStage.forPath(path)).thenReturn(path);

    SetDataBuilder setDataBuilder = mock(SetDataBuilder.class);
    when(client.setData()).thenReturn(setDataBuilder);
    when(setDataBuilder.forPath(eq(path), ArgumentMatchers.any(byte[].class))).thenReturn(null);

    confParamOps.set(domain, key, "newValue");
    verify(createBuilder).creatingParentContainersIfNeeded();
    verify(setDataBuilder).forPath(eq(path), ArgumentMatchers.any(byte[].class));
}

@Test
public void testSet_NodeExists() throws Exception {
    String domain = "Master";
    String key = "param1";
    String path = "/conf/" + domain + "/" + key;

    ExistsBuilder existsBuilder = mock(ExistsBuilder.class);
    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(path)).thenReturn(new Stat());

    SetDataBuilder setDataBuilder = mock(SetDataBuilder.class);
    when(client.setData()).thenReturn(setDataBuilder);
    when(setDataBuilder.forPath(eq(path), ArgumentMatchers.any(byte[].class))).thenReturn(null);

    confParamOps.set(domain, key, "newValue");
    verify(client, never()).create();
    verify(setDataBuilder).forPath(eq(path), ArgumentMatchers.any(byte[].class));
}

@Test(expected = KeymasterException.class)
public void testSet_Exception() throws Exception {
    String domain = "Master";
    String key = "param1";
    ExistsBuilder existsBuilder = mock(ExistsBuilder.class);
    when(client.checkExists()).thenReturn(existsBuilder);
    when(existsBuilder.forPath(anyString())).thenThrow(new RuntimeException("ZK error"));

    confParamOps.set(domain, key, "newValue");
}

@Test
public void testRemove_Success() throws Exception {
    String domain = "Master";
    String key = "param1";
    String path = "/conf/" + domain + "/" + key;

    DeleteBuilder deleteBuilder = mock(DeleteBuilder.class);
    when(client.delete()).thenReturn(deleteBuilder);
    when(deleteBuilder.forPath(path)).thenReturn(null);

    confParamOps.remove(domain, key);
    verify(deleteBuilder).forPath(path);
}

@Test(expected = KeymasterException.class)
public void testRemove_Exception() throws Exception {
    String domain = "Master";
    String key = "param1";
    String path = "/conf/" + domain + "/" + key;

    DeleteBuilder deleteBuilder = mock(DeleteBuilder.class);
    when(client.delete()).thenReturn(deleteBuilder);
    when(deleteBuilder.forPath(path)).thenThrow(new RuntimeException("ZK error"));

    confParamOps.remove(domain, key);
}
}