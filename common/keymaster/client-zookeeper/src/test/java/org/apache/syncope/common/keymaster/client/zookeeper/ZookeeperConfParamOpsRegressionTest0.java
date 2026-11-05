import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ZookeeperConfParamOpsRegressionTest0 {

    public static boolean debug = false;

    public void assertBooleanArrayEquals(boolean[] expectedArray, boolean[] actualArray) {
        if (expectedArray.length != actualArray.length) {
            throw new AssertionError("Array lengths differ: " + expectedArray.length + " != " + actualArray.length);
        }
        for (int i = 0; i < expectedArray.length; i++) {
            if (expectedArray[i] != actualArray[i]) {
                throw new AssertionError("Arrays differ at index " + i + ": " + expectedArray[i] + " != " + actualArray[i]);
            }
        }
    }

    @Test
    public void test01() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test01");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        // The following exception was thrown during execution in test generation
        try {
            java.util.Map<java.lang.String, java.lang.Object> strMap3 = zookeeperConfParamOps1.list("hi!");
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
    }

    @Test
    public void test02() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test02");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.remove("", "");
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.delete()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
    }

    @Test
    public void test03() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test03");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        org.apache.curator.framework.CuratorFramework curatorFramework4 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps5 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework4);
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.set("", "", zookeeperConfParamOps5);
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
    }

    @Test
    public void test04() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test04");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.remove("hi!", "");
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.delete()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
    }

    @Test
    public void test05() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test05");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        // The following exception was thrown during execution in test generation
        try {
            java.util.Map<java.lang.String, java.lang.Object> strMap3 = zookeeperConfParamOps1.list("");
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
    }

    @Test
    public void test06() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test06");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.remove("hi!", "hi!");
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.delete()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
    }

    @Test
    public void test07() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test07");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        java.lang.Object obj4 = new java.lang.Object();
        java.lang.Class<?> wildcardClass5 = obj4.getClass();
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.set("hi!", "hi!", (java.lang.reflect.Type) wildcardClass5);
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNotNull(wildcardClass5);
    }

    @Test
    public void test08() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test08");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        java.lang.Object obj4 = new java.lang.Object();
        java.lang.Class<?> wildcardClass5 = obj4.getClass();
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.set("", "hi!", (java.lang.reflect.GenericDeclaration) wildcardClass5);
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNotNull(wildcardClass5);
    }

    @Test
    public void test09() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test09");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        java.lang.Object obj4 = new java.lang.Object();
        java.lang.Class<?> wildcardClass5 = obj4.getClass();
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.set("", "hi!", (java.io.Serializable) wildcardClass5);
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNotNull(wildcardClass5);
    }

    @Test
    public void test10() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test10");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.set("hi!", "", (java.lang.constant.Constable) 10);
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
    }

    @Test
    public void test11() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test11");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        org.apache.curator.framework.CuratorFramework curatorFramework4 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps5 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework4);
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.set("", "", (org.apache.syncope.common.keymaster.client.api.ConfParamOps) zookeeperConfParamOps5);
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
    }

    @Test
    public void test12() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test12");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.remove("", "hi!");
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.delete()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
    }

    @Test
    public void test13() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test13");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        org.apache.curator.framework.CuratorFramework curatorFramework4 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps5 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework4);
        java.lang.Class<?> wildcardClass6 = zookeeperConfParamOps5.getClass();
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.set("hi!", "hi!", (java.lang.reflect.GenericDeclaration) wildcardClass6);
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNotNull(wildcardClass6);
    }

    @Test
    public void test14() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test14");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        java.lang.Object obj4 = new java.lang.Object();
        java.lang.Class<?> wildcardClass5 = obj4.getClass();
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.set("", "", (java.lang.reflect.GenericDeclaration) wildcardClass5);
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNotNull(wildcardClass5);
    }

    @Test
    public void test15() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test15");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        org.apache.curator.framework.CuratorFramework curatorFramework4 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps5 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework4);
        java.lang.Class<?> wildcardClass6 = zookeeperConfParamOps5.getClass();
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.set("hi!", "hi!", (java.lang.reflect.Type) wildcardClass6);
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNotNull(wildcardClass6);
    }

    @Test
    public void test16() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test16");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.set("hi!", "", (java.lang.Comparable<java.lang.String>) "hi!");
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
    }

    @Test
    public void test17() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test17");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.set("", "hi!", (java.lang.constant.ConstantDesc) 100.0f);
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
    }

    @Test
    public void test18() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ZookeeperConfParamOpsRegressionTest0.test18");
        org.apache.curator.framework.CuratorFramework curatorFramework0 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps1 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework0);
        org.apache.curator.framework.CuratorFramework curatorFramework4 = null;
        org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps zookeeperConfParamOps5 = new org.apache.syncope.common.keymaster.client.zookeeper.ZookeeperConfParamOps(curatorFramework4);
        // The following exception was thrown during execution in test generation
        try {
            zookeeperConfParamOps1.set("", "hi!", (org.apache.syncope.common.keymaster.client.api.ConfParamOps) zookeeperConfParamOps5);
            org.junit.Assert.fail("Expected exception of type org.apache.syncope.common.keymaster.client.api.KeymasterException; message: java.lang.NullPointerException: Cannot invoke \"org.apache.curator.framework.CuratorFramework.checkExists()\" because \"this.client\" is null");
        } catch (org.apache.syncope.common.keymaster.client.api.KeymasterException e) {
            // Expected exception.
        }
    }
}

