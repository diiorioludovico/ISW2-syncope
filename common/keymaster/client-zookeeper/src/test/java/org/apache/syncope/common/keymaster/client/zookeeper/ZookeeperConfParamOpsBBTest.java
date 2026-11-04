package org.apache.syncope.common.keymaster.client.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.test.TestingServer;
import org.apache.curator.retry.ExponentialBackoffRetry;

import org.apache.syncope.common.keymaster.client.api.KeymasterException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


import java.util.concurrent.TimeUnit;
import java.beans.Transient;
import java.util.Map;

class ZookeeperConfParamOpsBBTest {

    private TestingServer server;
    private CuratorFramework client;
    private ZookeeperConfParamOps ops;

    private final String DOMAIN = "Master";

    @BeforeEach
    public void setUp() throws Exception {
        server = new TestingServer();
        client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(100, 3));

        client.start();
        client.blockUntilConnected(5, TimeUnit.SECONDS);

        // creo il dominio Master vuoto
        client.create().creatingParentsIfNeeded().forPath("/conf/Master");

        ops = new ZookeeperConfParamOps(client);
    }

    @AfterEach
    public void tearDown() throws Exception {
        client.close();
        server.close();
    }

    /* _____________________________________
       |                                    |
       |         test per list()            |
       |____________________________________| */

    @Test
    public void listValidDomainTest() throws Exception {
        // verifico esistenza del dominio
        assertNotNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(DOMAIN)));

        // definiamo le chiavi e aggiungiamole al dominio Master
        Map<String, Object> expected = Map.of(
            "password.cipher.algorithm", "S-SHA-1",
            "token.lenght", 256
        );

        expected.forEach((key, value) -> ops.set(DOMAIN, key, value));

        // chiamiamo list()
        Map<String, Object> result = ops.list(DOMAIN);

        // controlliamo se il risultato ottenuto è quello aspettato
        assertEquals(expected, result);
    }

    @Test
    public void listEmptyDomainTest() throws Exception {
        // verifico esistenza del dominio
        assertNotNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(DOMAIN)));

        // chiamiamo list()
        Map<String, Object> result = ops.list(DOMAIN);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    public void listNoExistingDomainTest() throws Exception {
        // defininaiamo dominio che non esiste
        String domain = "DomainA";

        // chiamiamo list()
        Map<String, Object> result = ops.list(domain);

        // verifichiamo che ha creato il dominio
        assertNotNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(domain)));

        assertTrue(result.isEmpty());
    }

    @Test
    public void listZookeeperErrorTest() throws Exception {
        // interrompiamo il server 
        server.stop();

        // verifichiamo che sia lanciata l'eccezione
        assertThrows(KeymasterException.class, () -> ops.list(DOMAIN));
    }

    /* _____________________________________
       |                                    |
       |          test per get()            |
       |____________________________________| */

    @Test
    public void getExistingKeyTest() throws Exception {
        // verifico esistenza del dominio
        assertNotNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(DOMAIN)));

        String key = "password.cipher.algorithm";
        String expected = "S-SHA-1";

        // inseriamo valore su /conf/Master/password.cipher.algorithm
        ops.set(DOMAIN, key, expected);

        String result = ops.get(DOMAIN, key, "SHA-256", String.class);

        assertEquals(expected, result);
    }

    @Test
    public void getNoExistingKeytest() throws Exception {
        // verifichiamo che il dominio esiste
        assertNotNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(DOMAIN)));

        String key = "not.existing.key";
        Integer expected = 30;

        Integer result = ops.get(DOMAIN, key, expected, Integer.class);

        assertEquals(expected, result);
    }

    @Test
    public void getNoExistingPathTest() throws Exception {
        String domain = "DomainA";
        String key = "not.existing.key";
        Boolean expected = false;

        Boolean result = ops.get(domain, key, expected, Boolean.class);

        assertEquals(expected, result);
    }

    @Test
    public void getNoReferenceMatchingTest() throws Exception {
        String key = "password.cipher.algorithm";
        String value = "S-SHA-1";

        // inseriamo valore su /conf/Master/password.cipher.algorithm
        ops.set(DOMAIN, key, value);

        // verifichiamo che venga generata l'eccezione
        assertThrows(KeymasterException.class, () -> ops.get(DOMAIN, key, null, Integer.class));
    }

    @Test
    public void getZookeeperErrorTest() throws Exception {
        String key = "password.cipher.algorithm";

        // interrompiamo il server 
        server.stop();

        // verifichiamo che sia lanciata l'eccezione
        assertThrows(KeymasterException.class, () -> ops.get(DOMAIN, key, null, String.class));
    }

    /* _____________________________________
       |                                    |
       |          test per set()            |
       |____________________________________| */

    @Test
    public void setExistingPathTest() throws Exception {
        String key = "password.cipher.algorithm";
        String value = "S-SHA-1";

        ops.set(DOMAIN, key, value);

        // verifichiamo che il dominio esiste
        assertNotNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(DOMAIN, key)));
        assertEquals(value, ops.get(DOMAIN, key, null, String.class));

        //sovrascriviamo il valore su /conf/Master/password.cipher.algorithm
        String expected= "SHA-256";

        ops.set(DOMAIN, key, expected);

        // verifichiamo che lo ha modificato correttamente
        String result = ops.get(DOMAIN, key, null, String.class);

        assertEquals(expected, result);
    }

    @Test
    public void setNotExistingKeyTest() throws Exception {
        String key = "not.existing.key";
        Integer expected = 25;

        ops.set(DOMAIN, key, expected);

        // verifichiamo che il dominio esiste
        assertNotNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(DOMAIN, key)));

        Integer result = ops.get(DOMAIN, key, null, Integer.class);

        assertEquals(expected, result);
    }

    @Test
    public void setNotExistingPathTest() throws Exception {
        String domain = "DomainA";
        String key = "not.existing.key";
        Integer expected = 50;

        ops.set(domain, key, expected);

        // verifichiamo che il dominio esiste
        assertNotNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(domain, key)));

        Integer result = ops.get(domain, key, null, Integer.class);

        assertEquals(expected, result);
    }

    @Test
    public void setExistingPathNullValueTest() throws Exception {
        String key = "password.cipher.algorithm";
        String value = "S-SHA-1";

        ops.set(DOMAIN, key, value);

        //verifichiamo che il dominio esiste e che c'è il valore dentro
        // verifichiamo che il dominio esiste
        assertNotNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(DOMAIN, key)));
        assertEquals(value, ops.get(DOMAIN, key, null, String.class));

        ops.set(DOMAIN, key, null);

        assertNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(DOMAIN, key)));
    } 

    @Test
    public void setNoExistingPathNullValueTest() throws Exception {
        String domain = "DomainA";
        String key = "password.cipher.algorithm";

        // verifichiamo che effettivamente non c'è il dominio
        assertNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(DOMAIN, key)));
        assertThrows(KeymasterException.class, () -> ops.set(domain, key, null));
    }

    @Test
    public void setZookeeperErrorTest() throws Exception {
        String key = "password.cipher.algorithm";

        // interrompiamo il server 
        server.stop();

        // verifichiamo che sia lanciata l'eccezione
        assertThrows(KeymasterException.class, () -> ops.set(DOMAIN, key, "value-test"));
    }

    /* _____________________________________
       |                                    |
       |        test per remove()           |
       |____________________________________| */

    @Test
    public void removeExistingPathTest() throws Exception {
        String key = "password.cipher.algorithm";
        String value = "SHA-256";

        ops.set(DOMAIN, key, value);

        // verifichiamo che effettivamente c'è il dominio
        assertNotNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(DOMAIN, key)));
        assertEquals(value, ops.get(DOMAIN, key, null, String.class));

        ops.remove(DOMAIN, key);
    
        assertNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(DOMAIN, key)));
    }

    @Test
    public void removeNoExistingKeyTest() throws Exception {
        String key = "password.cipher.algorithm";

        // verifichiamo che effettivamente non c'è il dominio
        assertNull(client.checkExists().forPath(ZookeeperConfParamOps.buildConfPath(DOMAIN, key)));

        assertThrows(KeymasterException.class, () -> ops.remove(DOMAIN, key));
    }

    @Test
    public void removeZookeeperErrorTest() throws Exception {
        String key = "password.cipher.algorithm";

        // interrompiamo il server 
        server.stop();

        // verifichiamo che sia lanciata l'eccezione
        assertThrows(KeymasterException.class, () -> ops.remove(DOMAIN, key));
    }

}