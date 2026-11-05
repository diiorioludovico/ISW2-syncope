package org.apache.syncope.common.keymaster.client.zookeeper;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.syncope.common.keymaster.client.api.KeymasterException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ZookeeperConfParamOpsLLM2Test {

    private TestingServer server;

    private CuratorFramework client;

    private ZookeeperConfParamOps ops;

    private static final String DOMAIN = "Master";

    @Before
    public void setUp() throws Exception {
        server = new TestingServer();

        client = CuratorFrameworkFactory.newClient(
                server.getConnectString(),
                new ExponentialBackoffRetry(100, 3));
        client.start();
        assertTrue(client.blockUntilConnected(5, TimeUnit.SECONDS));

        ops = new ZookeeperConfParamOps(client);
    }

    @After
    public void tearDown() throws Exception {
        client.close();
        server.close();
    }

    /* --------------------------------------
     * buildConfPath()
     * -------------------------------------- */

    @Test
    public void buildConfPathWithoutPartsTest() {
        assertEquals("/conf/", ZookeeperConfParamOps.buildConfPath());
    }

    @Test
    public void buildConfPathWithSinglePartTest() {
        assertEquals("/conf/Master", ZookeeperConfParamOps.buildConfPath(DOMAIN));
    }

    @Test
    public void buildConfPathWithMultiplePartsTest() {
        assertEquals(
                "/conf/Master/password.cipher.algorithm",
                ZookeeperConfParamOps.buildConfPath(DOMAIN, "password.cipher.algorithm"));
    }

    /* --------------------------------------
     * list()
     * -------------------------------------- */

    @Test
    public void listExistingDomainTest() throws Exception {
        client.create().creatingParentsIfNeeded().forPath("/conf/" + DOMAIN);

        ops.set(DOMAIN, "password.cipher.algorithm", "S-SHA-1");
        ops.set(DOMAIN, "token.length", 256);

        Map<String, Object> result = ops.list(DOMAIN);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("S-SHA-1", result.get("password.cipher.algorithm"));
        assertEquals(256, result.get("token.length"));
    }

    @Test
    public void listNonExistingDomainCreatesItTest() {
        Map<String, Object> result = ops.list(DOMAIN);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        try {
            assertNotNull(client.checkExists().forPath("/conf/" + DOMAIN));
        } catch (Exception e) {
            fail("Unexpected exception while checking the created domain: " + e.getMessage());
        }
    }

    @Test
    public void listEmptyDomainTest() throws Exception {
        client.create().creatingParentsIfNeeded().forPath("/conf/" + DOMAIN);

        Map<String, Object> result = ops.list(DOMAIN);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void listReturnsSortedKeysTest() {
        ops.set(DOMAIN, "token.length", 256);
        ops.set(DOMAIN, "password.cipher.algorithm", "S-SHA-1");
        ops.set(DOMAIN, "admin.password", "admin");

        Map<String, Object> result = ops.list(DOMAIN);

        assertNotNull(result);
        assertEquals(3, result.size());

        String previousKey = null;
        for (String key : result.keySet()) {
            if (previousKey != null) {
                assertTrue(previousKey.compareTo(key) <= 0);
            }
            previousKey = key;
        }
    }

    /* --------------------------------------
     * get()
     * -------------------------------------- */

    @Test
    public void getExistingStringKeyTest() {
        String key = "password.cipher.algorithm";
        String expected = "S-SHA-1";

        ops.set(DOMAIN, key, expected);

        String result = ops.get(DOMAIN, key, "SHA-256", String.class);

        assertEquals(expected, result);
    }

    @Test
    public void getExistingIntegerKeyTest() {
        String key = "token.length";
        Integer expected = 256;

        ops.set(DOMAIN, key, expected);

        Integer result = ops.get(DOMAIN, key, 128, Integer.class);

        assertEquals(expected, result);
    }

    @Test
    public void getExistingBooleanKeyTest() {
        String key = "notification.enabled";
        Boolean expected = Boolean.TRUE;

        ops.set(DOMAIN, key, expected);

        Boolean result = ops.get(DOMAIN, key, Boolean.FALSE, Boolean.class);

        assertEquals(expected, result);
    }

    @Test
    public void getNonExistingKeyReturnsDefaultValueTest() {
        String result = ops.get(DOMAIN, "missing.key", "default", String.class);

        assertEquals("default", result);
    }

    @Test
    public void getNonExistingKeyWithNullDefaultValueTest() {
        String result = ops.get(DOMAIN, "missing.key", null, String.class);

        assertNull(result);
    }

    @Test
    public void getNonExistingDomainReturnsDefaultValueTest() {
        String result = ops.get("MissingDomain", "missing.key", "default", String.class);

        assertEquals("default", result);
    }

    @Test
    public void getWithInvalidStoredValueTest() throws Exception {
        client.create().creatingParentsIfNeeded().forPath(ZookeeperConfParamOps.buildConfPath(DOMAIN, "invalid.value"));
        client.setData().forPath("/conf/" + DOMAIN + "/invalid.value", "not-json".getBytes());

        try {
            ops.get(DOMAIN, "invalid.value", 0, Integer.class);
            fail("Expected KeymasterException");
        } catch (KeymasterException e) {
            assertNotNull(e.getMessage());
        }
    }

    /* --------------------------------------
     * set()
     * -------------------------------------- */

    @Test
    public void setNewKeyTest() {
        String key = "password.cipher.algorithm";
        String value = "S-SHA-1";

        ops.set(DOMAIN, key, value);

        try {
            assertNotNull(client.checkExists().forPath("/conf/" + DOMAIN + "/" + key));
        } catch (Exception e) {
            fail("Unexpected exception while checking the created key: " + e.getMessage());
        }

        assertEquals(value, ops.get(DOMAIN, key, null, String.class));
    }

    @Test
    public void setExistingKeyOverwritesValueTest() {
        String key = "password.cipher.algorithm";

        ops.set(DOMAIN, key, "S-SHA-1");
        assertEquals("S-SHA-1", ops.get(DOMAIN, key, null, String.class));

        ops.set(DOMAIN, key, "SHA-256");
        assertEquals("SHA-256", ops.get(DOMAIN, key, null, String.class));
    }

    @Test
    public void setIntegerValueTest() {
        String key = "token.length";
        Integer value = 256;

        ops.set(DOMAIN, key, value);

        assertEquals(value, ops.get(DOMAIN, key, null, Integer.class));
    }

    @Test
    public void setBooleanValueTest() {
        String key = "notification.enabled";
        Boolean value = Boolean.TRUE;

        ops.set(DOMAIN, key, value);

        assertEquals(value, ops.get(DOMAIN, key, null, Boolean.class));
    }

    @Test
    public void setNullValueRemovesKeyTest() {
        String key = "password.cipher.algorithm";

        ops.set(DOMAIN, key, "S-SHA-1");
        assertNotNull(ops.get(DOMAIN, key, null, String.class));

        ops.set(DOMAIN, key, null);

        assertNull(client.checkExists() != null ? ops.get(DOMAIN, key, null, String.class) : null);
        assertEquals("default", ops.get(DOMAIN, key, "default", String.class));
    }

    /* --------------------------------------
     * remove()
     * -------------------------------------- */

    @Test
    public void removeExistingKeyTest() {
        String key = "password.cipher.algorithm";

        ops.set(DOMAIN, key, "S-SHA-1");
        assertEquals("S-SHA-1", ops.get(DOMAIN, key, null, String.class));

        ops.remove(DOMAIN, key);

        assertNull(client.checkExists() != null ? ops.get(DOMAIN, key, null, String.class) : null);
        assertEquals("default", ops.get(DOMAIN, key, "default", String.class));
    }

    @Test
    public void removeNonExistingKeyTest() {
        try {
            ops.remove(DOMAIN, "missing.key");
            fail("Expected KeymasterException");
        } catch (KeymasterException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void removeKeyFromNonExistingDomainTest() {
        try {
            ops.remove("MissingDomain", "missing.key");
            fail("Expected KeymasterException");
        } catch (KeymasterException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void removeWithNullDomainTest() {
        try {
            ops.remove(null, "password.cipher.algorithm");
            fail("Expected KeymasterException");
        } catch (KeymasterException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void removeWithNullKeyTest() {
        try {
            ops.remove(DOMAIN, null);
            fail("Expected KeymasterException");
        } catch (KeymasterException e) {
            assertNotNull(e.getMessage());
        }
    }

    /* --------------------------------------
     * Integration among operations
     * -------------------------------------- */

    @Test
    public void setGetListAndRemoveWorkflowTest() {
        ops.set(DOMAIN, "password.cipher.algorithm", "S-SHA-1");
        ops.set(DOMAIN, "token.length", 256);

        Map<String, Object> listed = ops.list(DOMAIN);
        assertEquals(2, listed.size());
        assertEquals("S-SHA-1", listed.get("password.cipher.algorithm"));
        assertEquals(256, listed.get("token.length"));

        ops.remove(DOMAIN, "token.length");

        listed = ops.list(DOMAIN);
        assertEquals(1, listed.size());
        assertEquals("S-SHA-1", listed.get("password.cipher.algorithm"));
    }

    @Test
    public void multipleDomainsAreIndependentTest() {
        ops.set(DOMAIN, "password.cipher.algorithm", "S-SHA-1");
        ops.set("OtherDomain", "password.cipher.algorithm", "SHA-256");

        assertEquals("S-SHA-1", ops.get(DOMAIN, "password.cipher.algorithm", null, String.class));
        assertEquals("SHA-256", ops.get("OtherDomain", "password.cipher.algorithm", null, String.class));

        ops.remove(DOMAIN, "password.cipher.algorithm");

        assertEquals("SHA-256", ops.get("OtherDomain", "password.cipher.algorithm", null, String.class));
        assertEquals("default", ops.get(DOMAIN, "password.cipher.algorithm", "default", String.class));
    }
}