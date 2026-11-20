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

public class ZookeeperConfParamOps_3ESTest {

  @Test(timeout = 4000)
  public void test0() {
      ZookeeperConfParamOps_3 zookeeperConfParamOps_3_0 = new ZookeeperConfParamOps_3((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_3_0.set("S`-kI||21[s3\"~d", "1U@-b2s):^R ]W", (Integer) null);
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test1() {
      ZookeeperConfParamOps_3 zookeeperConfParamOps_3_0 = new ZookeeperConfParamOps_3((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_3_0.list("(%-IRxtD");
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test2() {
      try { 
        ZookeeperConfParamOps_3.buildConfPath((String[]) null);
        fail("Expecting exception: NullPointerException");
      } catch(NullPointerException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test3() {
      ZookeeperConfParamOps_3 zookeeperConfParamOps_3_0 = new ZookeeperConfParamOps_3((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_3_0.set("", "", zookeeperConfParamOps_3_0);
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test4() {
      ZookeeperConfParamOps_3 zookeeperConfParamOps_3_0 = new ZookeeperConfParamOps_3((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_3_0.remove((String) null, "m!l6-Lu");
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test5() {
      ZookeeperConfParamOps_3 zookeeperConfParamOps_3_0 = new ZookeeperConfParamOps_3((CuratorFramework) null);
      Class<JsonMapper> class0 = JsonMapper.class;
      try { 
        zookeeperConfParamOps_3_0.get("|SN'a4+v)!3 ", "|SN'a4+v)!3 ", (JsonMapper) null, class0);
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test6() {
      String[] stringArray0 = new String[0];
      String string0 = ZookeeperConfParamOps_3.buildConfPath(stringArray0);
      assertEquals("/conf/", string0);
  }
}