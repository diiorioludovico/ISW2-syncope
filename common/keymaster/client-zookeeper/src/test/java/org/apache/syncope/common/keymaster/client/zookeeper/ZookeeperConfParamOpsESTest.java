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
import static org.mockito.Mockito.*;

import java.io.Serializable;
import org.junit.AssumptionViolatedException;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.LinkedList;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps;
import org.apache.zookeeper.data.Stat;


public class ZookeeperConfParamOpsESTest {

  /**
     * Legacy string matcher: preserves matching of null String arguments.
     * Inherited methods take precedence over static imports in the test class.
     */
    private static String anyString() {
        return ArgumentMatchers.nullable(String.class);
    }

    /**
     * Preserve the generated default-answer policy: an unstubbed non-void
     * invocation invalidates the generated assumption, rather than returning null.
     * Void methods need no default return value.
     */
    private static final class ViolatedAssumptionAnswer
            implements Answer<Object>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public Object answer(final InvocationOnMock invocation) {
            if (invocation.getMethod().getReturnType() == Void.TYPE) {
                return null;
            }
            throw new AssumptionViolatedException(
                    "Unstubbed mock invocation invalidates the generated test assumption: "
                    + invocation.getMethod().toGenericString());
        }
    }

    /**
     * Keep the generated check on the originating class of the caught exception.
     * This is not merely an assertThrows(RuntimeException.class) replacement.
     */
    private static void verifyException(final String expectedOrigin, final Throwable exception) {
        if (exception instanceof AssumptionViolatedException) {
            throw (AssumptionViolatedException) exception;
        }
        assertNotNull("Expected a non-null exception", exception);
        StackTraceElement[] trace = exception.getStackTrace();
        assertTrue("The exception must have a stack trace", trace.length > 0);
        assertEquals("Unexpected origin of the caught exception", expectedOrigin,
                trace[0].getClassName());
    }

  @Test(timeout = 4000)
  public void test0()  throws Throwable  {
      Stat stat0 = mock(Stat.class, new ViolatedAssumptionAnswer());
      ExistsBuilder existsBuilder0 = mock(ExistsBuilder.class, new ViolatedAssumptionAnswer());
      doReturn(stat0).when(existsBuilder0).forPath(anyString());
      CuratorFramework curatorFramework0 = mock(CuratorFramework.class, new ViolatedAssumptionAnswer());
      doReturn(existsBuilder0).when(curatorFramework0).checkExists();
      doReturn((SetDataBuilder) null).when(curatorFramework0).setData();
      ZookeeperConfParamOps zookeeperConfParamOps0 = new ZookeeperConfParamOps(curatorFramework0);
      JsonMapper jsonMapper0 = ZookeeperConfParamOps.MAPPER;
      // Undeclared exception!
      try { 
        zookeeperConfParamOps0.set("K'", "Node {} was not found", jsonMapper0);
        fail("Expecting exception: RuntimeException");
      
      } catch(RuntimeException e) {
         //
         // java.lang.NullPointerException
         //
         verifyException("org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps", e);
      }
  }

  @Test(timeout = 4000)
  public void test1()  throws Throwable  {
      ExistsBuilder existsBuilder0 = mock(ExistsBuilder.class, new ViolatedAssumptionAnswer());
      doReturn((Object) null).when(existsBuilder0).forPath(anyString());
      CuratorFramework curatorFramework0 = mock(CuratorFramework.class, new ViolatedAssumptionAnswer());
      doReturn(existsBuilder0).when(curatorFramework0).checkExists();
      doReturn((CreateBuilder) null).when(curatorFramework0).create();
      ZookeeperConfParamOps zookeeperConfParamOps0 = new ZookeeperConfParamOps(curatorFramework0);
      // Undeclared exception!
      try { 
        zookeeperConfParamOps0.set("uOq_O*y%n49GkaQ1!fY", "Could not resolve type id '%s' into a subtype of %s", (Object) "Could not resolve type id '%s' into a subtype of %s");
        fail("Expecting exception: RuntimeException");
      
      } catch(RuntimeException e) {
         //
         // java.lang.NullPointerException
         //
         verifyException("org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps", e);
      }
  }

  @Test(timeout = 4000)
  public void test2()  throws Throwable  {
      Stat stat0 = mock(Stat.class, new ViolatedAssumptionAnswer());
      ExistsBuilder existsBuilder0 = mock(ExistsBuilder.class, new ViolatedAssumptionAnswer());
      doReturn(stat0).when(existsBuilder0).forPath(anyString());
      LinkedList<String> linkedList0 = new LinkedList<String>();
      GetChildrenBuilder getChildrenBuilder0 = mock(GetChildrenBuilder.class, new ViolatedAssumptionAnswer());
      doReturn(linkedList0).when(getChildrenBuilder0).forPath(anyString());
      linkedList0.addFirst((String) null);
      CuratorFramework curatorFramework0 = mock(CuratorFramework.class, new ViolatedAssumptionAnswer());
      doReturn(existsBuilder0).when(curatorFramework0).checkExists();
      doReturn(getChildrenBuilder0).when(curatorFramework0).getChildren();
      doReturn((GetDataBuilder) null).when(curatorFramework0).getData();
      ZookeeperConfParamOps zookeeperConfParamOps0 = new ZookeeperConfParamOps(curatorFramework0);
      String[] stringArray0 = new String[8];
      // Undeclared exception!
      try { 
        zookeeperConfParamOps0.list(stringArray0[4]);
        fail("Expecting exception: RuntimeException");
      
      } catch(RuntimeException e) {
         //
         // java.lang.NullPointerException
         //
         verifyException("org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps", e);
      }
  }

  @Test(timeout = 4000)
  public void test3()  throws Throwable  {
      ExistsBuilder existsBuilder0 = mock(ExistsBuilder.class, new ViolatedAssumptionAnswer());
      doReturn((Object) null).when(existsBuilder0).forPath(anyString());
      CuratorFramework curatorFramework0 = mock(CuratorFramework.class, new ViolatedAssumptionAnswer());
      doReturn(existsBuilder0).when(curatorFramework0).checkExists();
      doReturn((CreateBuilder) null).when(curatorFramework0).create();
      ZookeeperConfParamOps zookeeperConfParamOps0 = new ZookeeperConfParamOps(curatorFramework0);
      String[] stringArray0 = new String[8];
      // Undeclared exception!
      try { 
        zookeeperConfParamOps0.list(stringArray0[4]);
        fail("Expecting exception: RuntimeException");
      
      } catch(RuntimeException e) {
         //
         // java.lang.NullPointerException
         //
         verifyException("org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps", e);
      }
  }

  @Test(timeout = 4000)
  public void test4()  throws Throwable  {
      Stat stat0 = mock(Stat.class, new ViolatedAssumptionAnswer());
      ExistsBuilder existsBuilder0 = mock(ExistsBuilder.class, new ViolatedAssumptionAnswer());
      doReturn(stat0).when(existsBuilder0).forPath(anyString());
      LinkedList<String> linkedList0 = new LinkedList<String>();
      GetChildrenBuilder getChildrenBuilder0 = mock(GetChildrenBuilder.class, new ViolatedAssumptionAnswer());
      doReturn((Object) null).when(getChildrenBuilder0).forPath(anyString());
      CuratorFramework curatorFramework0 = mock(CuratorFramework.class, new ViolatedAssumptionAnswer());
      doReturn(existsBuilder0).when(curatorFramework0).checkExists();
      doReturn(getChildrenBuilder0).when(curatorFramework0).getChildren();
      ZookeeperConfParamOps zookeeperConfParamOps0 = new ZookeeperConfParamOps(curatorFramework0);
      // Undeclared exception!
      try { 
        zookeeperConfParamOps0.list("c-3:<)W}");
        fail("Expecting exception: RuntimeException");
      
      } catch(RuntimeException e) {
         //
         // java.lang.NullPointerException
         //
         verifyException("org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps", e);
      }
  }

  @Test(timeout = 4000)
  public void test5()  throws Throwable  {
      ZookeeperConfParamOps zookeeperConfParamOps0 = new ZookeeperConfParamOps((CuratorFramework) null);
      JsonMapper.Builder jsonMapper_Builder0 = JsonMapper.builder();
      Class<JsonMapper.Builder> class0 = JsonMapper.Builder.class;
      // Undeclared exception!
      try { 
        zookeeperConfParamOps0.get("PROPAGATE_TRANSIENT_MARKER", "", jsonMapper_Builder0, class0);
        fail("Expecting exception: RuntimeException");
      
      } catch(RuntimeException e) {
         //
         // java.lang.NullPointerException
         //
         verifyException("org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps", e);
      }
  }

  @Test(timeout = 4000)
  public void test6()  throws Throwable  {
      DeleteBuilder deleteBuilder0 = mock(DeleteBuilder.class, new ViolatedAssumptionAnswer());
      doNothing().when(deleteBuilder0).forPath(anyString());
      CuratorFramework curatorFramework0 = mock(CuratorFramework.class, new ViolatedAssumptionAnswer());
      doReturn(deleteBuilder0).when(curatorFramework0).delete();
      ZookeeperConfParamOps zookeeperConfParamOps0 = new ZookeeperConfParamOps(curatorFramework0);
      String[] stringArray0 = new String[8];
      zookeeperConfParamOps0.set("org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps", stringArray0[4], (Object) null);
      assertEquals(8, stringArray0.length);
  }
}
