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

import org.junit.Test;
import static org.junit.Assert.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.curator.framework.CuratorFramework;

public class ZookeeperConfParamOps_1ESTest {

  @Test(timeout = 4000)
  public void test0() {
      ZookeeperConfParamOps_1 zookeeperConfParamOps_1_0 = new ZookeeperConfParamOps_1((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_1_0.list("com.google.common.cache.ForwardingLoadingCache");
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test1() {
      try { 
        ZookeeperConfParamOps_1.buildConfPath((String[]) null);
        fail("Expecting exception: NullPointerException");
      } catch(NullPointerException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test2() {
      ZookeeperConfParamOps_1 zookeeperConfParamOps_1_0 = new ZookeeperConfParamOps_1((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_1_0.remove("/", "/");
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test3() {
      ZookeeperConfParamOps_1 zookeeperConfParamOps_1_0 = new ZookeeperConfParamOps_1((CuratorFramework) null);
      Integer integer0 = 2395;
      try { 
        zookeeperConfParamOps_1_0.set("result", "/conf/", integer0);
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test4() {
      String[] stringArray0 = new String[5];
      String string0 = ZookeeperConfParamOps_1.buildConfPath(stringArray0);
      assertEquals("/conf/null/null/null/null/null", string0);
  }

  @Test(timeout = 4000)
  public void test5() {
      ZookeeperConfParamOps_1 zookeeperConfParamOps_1_0 = new ZookeeperConfParamOps_1((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_1_0.set("Node {} was not found", (String) null, (Integer) null);
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test6() {
      ZookeeperConfParamOps_1 zookeeperConfParamOps_1_0 = new ZookeeperConfParamOps_1((CuratorFramework) null);
      JsonMapper jsonMapper0 = ZookeeperConfParamOps_1.MAPPER;
      Class<JsonMapper> class0 = JsonMapper.class;
      try { 
        zookeeperConfParamOps_1_0.get("}rjl2}W2Z)LElc_E22a", (String) null, jsonMapper0, class0);
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }
}