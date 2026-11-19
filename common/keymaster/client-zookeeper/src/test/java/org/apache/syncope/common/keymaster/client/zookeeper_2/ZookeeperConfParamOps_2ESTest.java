package org.apache.syncope.common.keymaster.client.zookeeper;

import org.junit.Test;
import static org.junit.Assert.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.curator.framework.CuratorFramework;

public class ZookeeperConfParamOps_2ESTest {

  @Test(timeout = 4000)
  public void test00() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      Class<String> class0 = String.class;
      try { 
        zookeeperConfParamOps_2_0.get("no default constructor found", "no default constructor found", "no default constructor found", class0);
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test01() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_2_0.set("no default constructor found", "no default constructor found", (Object) null);
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test02() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_2_0.list("org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps_2");
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test03() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      String[] stringArray0 = new String[4];
      ZookeeperConfParamOps_2.buildConfPath(stringArray0);
      try { 
        zookeeperConfParamOps_2_0.remove("Node of type `%s` has no indexed values", "Cannot construct EnumMap; generic (key) type not available");
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test04() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_2_0.set("/conf", "=Ua8(Mb:.F97| xen9", (JsonMapper.Builder) null);
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test05() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_2_0.set("no default constructor found", "no default constructor found", (Object) null);
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test06() {
      try { 
        ZookeeperConfParamOps_2.buildConfPath((String[]) null);
        fail("Expecting exception: NullPointerException");
      } catch(NullPointerException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test07() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_2_0.remove("", "");
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test08() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_2_0.remove("! AK4c-_^3H#", "! AK4c-_^3H#");
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test09() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_2_0.set("", "", "+s*ilNLa~i");
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test10() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_2_0.list("");
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test11() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_2_0.list("{`< a$4h:'.a");
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test12() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      String[] stringArray1 = new String[5];
      stringArray1[0] = "/conf/";
      stringArray1[1] = "/conf/";
      Integer integer0 = 134;
      Class<Integer> class0 = Integer.class;
      try { 
        zookeeperConfParamOps_2_0.get((String) null, "", integer0, class0);
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test13() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      Integer integer0 = 1037;
      Class<Integer> class0 = Integer.class;
      try { 
        zookeeperConfParamOps_2_0.get("y0V[-iWI", "", integer0, class0);
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test14() {
      try { 
        ZookeeperConfParamOps_2.buildConfPath((String[]) null);
        fail("Expecting exception: NullPointerException");
      } catch(NullPointerException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test15() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      try { 
        zookeeperConfParamOps_2_0.list("l");
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }

  @Test(timeout = 4000)
  public void test16() {
      ZookeeperConfParamOps_2 zookeeperConfParamOps_2_0 = new ZookeeperConfParamOps_2((CuratorFramework) null);
      String[] stringArray0 = new String[6];
      ZookeeperConfParamOps_2.buildConfPath(stringArray0);
      Integer integer0 = 32;
      try { 
        zookeeperConfParamOps_2_0.set("comfasterxml.jackson.core.format.InputAcessor$Std", "comfasterxml.jackson.core.format.InputAcessor$Std", integer0);
        fail("Expecting exception: RuntimeException");
      } catch(RuntimeException e) {
         assertNotNull(e);
      }
  }
}