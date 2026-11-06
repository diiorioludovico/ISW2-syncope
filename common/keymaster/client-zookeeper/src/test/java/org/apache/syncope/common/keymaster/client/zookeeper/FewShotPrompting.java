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
import java.util.Map;

class FewShotPrompting {

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
}