package org.apache.syncope.common.keymaster.client.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.curator.framework.api.ProtectACLCreateModeStatPathAndBytesable;
import org.apache.syncope.common.keymaster.client.api.KeymasterException;
import org.apache.zookeeper.data.Stat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.beans.Transient;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/*
    TEST CLASS for ZookeeperConfParamOps
*/

@ExtendWith(MockitoExtension.class)
class ZookeeperConfParamOpsTest {
    private static ZookeeperConfParamOps confParamOps;
    private final String DOMAIN = "domain";

    @Mock
    private CuratorFramework client;

    @Mock
    private Stat stat;

    @Mock
    private ProtectACLCreateModeStatPathAndBytesable<String> createPathBuilder;

    @Mock
    private ExistsBuilder existsBuilder;

    @Mock
    private CreateBuilder createBuilder;

    @Mock
    private GetChildrenBuilder childrenBuilder;

    @Mock
    private GetDataBuilder getDataBuilder;

    @Mock
    private DeleteBuilder deleteBuilder;

    @Mock
    private SetDataBuilder setDataBuilder;


    @BeforeEach
    public void configuration() {
        // inizializzazione dell'oggetto ZookeeperConfParamOps
        confParamOps = new ZookeeperConfParamOps(client);
    }

    /* _____________________________________
       |                                    |
       |    test per buildConfPathTest()    |
       |____________________________________|   */
    
    @Test
    public void buildConfPathTest() {
        String result = ZookeeperConfParamOps.buildConfPath(DOMAIN);
        assertEquals("/conf/domain", result);
    }    

    @Test
    public void buildConfPathMultipleParametersTest() {
        String test = "test";
        String result = ZookeeperConfParamOps.buildConfPath(DOMAIN, test);
        assertEquals("/conf/domain/test", result);
    }    

    @Test
    public void buildConfPathEmptyStringTest() {
        String domain = "";
        String result = ZookeeperConfParamOps.buildConfPath(domain);
        assertEquals("/conf/", result);
    } 
    
    @Test
    public void buildConfPathNullStringTest() {
        String domain = null;
        String result = ZookeeperConfParamOps.buildConfPath(domain);
        assertEquals("/conf/null", result);
    } 

    /* _____________________________________
       |                                    |
       |         test per list()            |
       |____________________________________|   */

    @Test
    public void listDontExistTest() throws Exception {
        String path = ZookeeperConfParamOps.buildConfPath(DOMAIN);        
        
        // serve una mock su client per la funzione client.checkExists() in modo che restituisca null
        when(client.checkExists()).thenReturn(existsBuilder);
        when(existsBuilder.forPath(path)).thenReturn(null);

        // viene creato il nuovo dominio
        when(client.create()).thenReturn(createBuilder);
        when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createPathBuilder);
        when(createPathBuilder .forPath(path)).thenReturn(null);
        
        // non trova figli, quindi serve una mock per client.getChildren() che restituisce una lista di stringhe vuota
        when(client.getChildren()).thenReturn(childrenBuilder);
        when(childrenBuilder.forPath(path)).thenReturn(List.of());

        Map<String, Object> result = confParamOps.list(DOMAIN);
        
        assertTrue(result.isEmpty());        
    }

    @Test
    public void listExistTest() throws Exception {
        String path = ZookeeperConfParamOps.buildConfPath(DOMAIN);    

        // serve una mock su client per la funzione client.checkExists() in modo che restituisca qualcosa
        when(client.checkExists()).thenReturn(existsBuilder);
        when(existsBuilder.forPath(path)).thenReturn(stat);

        // serve una mock per client.getChildren() che restituisce qualcosa
        when(client.getChildren()).thenReturn(childrenBuilder);
        when(childrenBuilder.forPath(path)).thenReturn(List.of("maxRetries", "expireTime"));
        
        // viene popolata la mappa
        when(client.getData()).thenReturn(getDataBuilder);
        when(getDataBuilder.forPath(path + "/maxRetries")).thenReturn("3".getBytes());
        when(getDataBuilder.forPath(path + "/expireTime")).thenReturn("60".getBytes());
        
        // viene restituita la mappa piena
        Map<String, Object> result = confParamOps.list(DOMAIN);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(3, result.get("maxRetries"));
        assertEquals(60, result.get("expireTime"));
    }

    @Test
    public void listInvalidZookeeperDomainTest() throws Exception{
        String domain = "zookeeper";
        String path = ZookeeperConfParamOps.buildConfPath(domain); 

        // serve una mock su client per la funzione client.checkExists() in modo che restituisca null
        when(client.checkExists()).thenReturn(existsBuilder);
        when(existsBuilder.forPath(path)).thenReturn(null);

        // tenta di creare il nuovo dominio, ma genera eccezione
        when(client.create()).thenReturn(createBuilder);
        when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createPathBuilder );
        when(createPathBuilder .forPath(path)).thenThrow(new Exception("ZooKeeper rejected reserved domain"));

        // verifichiamo che l'eccezione sia stata generata
        assertThrows(KeymasterException.class, () -> confParamOps.list(domain));
    }

    @Test
    public void listEmptyDomainTest() throws Exception {
        String domain = "";
        String path = ZookeeperConfParamOps.buildConfPath(domain); 
        
        // serve una mock su client per la funzione client.checkExists() in modo che restituisca null
        when(client.checkExists()).thenReturn(existsBuilder);
        when(existsBuilder.forPath(path)).thenReturn(null);

        // tenta di creare il nuovo dominio, ma genera eccezione
        when(client.create()).thenReturn(createBuilder);
        when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createPathBuilder);
        when(createPathBuilder.forPath(path)).thenThrow(new Exception ("Invalid path")); 

        //IllegalArgumentException rappresenta l’errore che Curator solleva per un path ZooKeeper malformato

        assertThrows(KeymasterException.class, () -> confParamOps.list(domain));
    }

    @Test
    public void listNullDomainTest() throws Exception {
        String domain = null;
        String path = ZookeeperConfParamOps.buildConfPath(domain); // "/conf/null"

        // serve una mock su client per la funzione client.checkExists() in modo che restituisca null
        when(client.checkExists()).thenReturn(existsBuilder);
        when(existsBuilder.forPath(path)).thenReturn(null);

        // viene creato il dominio "/conf/null"
        when(client.create()).thenReturn(createBuilder);
        when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createPathBuilder);
        when(createPathBuilder.forPath(path)).thenReturn(path);

        // si recuperano le configurazioni sotto tale dominio, ma non viene restituito nulla
        when(client.getChildren()).thenReturn(childrenBuilder);
        when(childrenBuilder.forPath(path)).thenReturn(List.of());

        Map<String, Object> result = confParamOps.list(domain);

        assertTrue(result.isEmpty());
    }

    /* _____________________________________
       |                                    |
       |          test per get()            |
       |____________________________________|   */


    @Test
    public void getExistingNodeTest() throws Exception {
        String key = "host.address";
        String defaultValue = "defaultValue";

        String path = ZookeeperConfParamOps.buildConfPath(DOMAIN, key);

        // leggiamo il contenuto del json di test
        byte[] jsonBytes = "\"host.address\"".getBytes();

        // simuliamo l'interazione con Zookeeper
        when(client.getData()).thenReturn(getDataBuilder);
        when(getDataBuilder.forPath(path)).thenReturn(jsonBytes);

        String result = confParamOps.get(DOMAIN, key, "defaultValue", String.class);

        assertEquals(key, result);
    }

    @Test
    public void getNoExistingNodeTest() throws Exception {
        String key = "key.integer";
        Integer defaultValue = 20;

        String path = ZookeeperConfParamOps.buildConfPath(DOMAIN, key);

        when(client.getData()).thenReturn(getDataBuilder);
        when(getDataBuilder.forPath(path)).thenThrow(new org.apache.zookeeper.KeeperException.NoNodeException());

        Integer result = confParamOps.get(DOMAIN, key, defaultValue, Integer.class);

        assertEquals(defaultValue, result);
    }

    @Test
    public void getInvalidPathTest() throws Exception {
        String key = "";
        Boolean defaultValue = true;

        String path = ZookeeperConfParamOps.buildConfPath(DOMAIN, key);
        when(client.getData()).thenReturn(getDataBuilder);
        when(getDataBuilder.forPath(path)).thenThrow(new Exception());

        assertThrows(KeymasterException.class, () -> confParamOps.get(DOMAIN, key, defaultValue, Boolean.class));
    }


    /* _____________________________________
       |                                    |
       |          test per set()            |
       |____________________________________|   */

    @Test
    public void setNullValueTest() throws Exception {
        String key = "key";
        String path = ZookeeperConfParamOps.buildConfPath(DOMAIN, key);

        when(client.delete()).thenReturn(deleteBuilder);
        doNothing().when(deleteBuilder).forPath(path);

        confParamOps.set(DOMAIN, key, null);

        verify(client).delete();
        verify(client.delete()).forPath(path);
    }

    @Test
    public void setNotExistingKeyTest() throws Exception {
        String domain = "notExistingDomain";
        String key = "not.exixsting.key";
        String path = ZookeeperConfParamOps.buildConfPath(domain, key);
        Integer value = 30;

        // mock sul controllo che verifica l'esistenza di /conf/notExistingDomain/not.existing.key e ritorna null, poiché non esiste
        when(client.checkExists()).thenReturn(existsBuilder);
        when(existsBuilder.forPath(path)).thenReturn(null);

        // mock sulla creazione del dominio
        when(client.create()).thenReturn(createBuilder);
        when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createPathBuilder);
        when(createPathBuilder.forPath(path)).thenReturn(null);

        // mock sull'operazione di scrittura di value dentro /conf/notExistingDomain/not.existing.key
        when(client.setData()).thenReturn(setDataBuilder);
        when(setDataBuilder.forPath(eq(path), any(byte[].class))).thenReturn(null);

        confParamOps.set(domain, key, value);

        verify(client).checkExists();
        verify(client.checkExists()).forPath(path);

        verify(client).create();
        verify(client.create()).creatingParentContainersIfNeeded();
        verify(client.create().creatingParentContainersIfNeeded()).forPath(path);

        verify(client).setData();
        verify(client.setData()).forPath(eq(path), any(byte[].class));
    }

    @Test 
    public void setInvalidPathTest() throws Exception {
        String domain = "";
        String key = "";
        Boolean value = true;

        String path = ZookeeperConfParamOps.buildConfPath(domain, key);

        when(client.checkExists()).thenReturn(existsBuilder);
        when(existsBuilder.forPath(path)).thenThrow(new Exception("comunicazione con Zookeeper interrotta"));

        assertThrows(KeymasterException.class, () -> confParamOps.set(domain, key, value));

        verify(client).checkExists();
        verify(client.checkExists()).forPath(path);
    }

    /* _____________________________________
       |                                    |
       |         test per remove()          |
       |____________________________________|   */

    @Test
    public void removeKeymasterExceptionTest() throws Exception {
        String key = "key";

        String path = ZookeeperConfParamOps.buildConfPath(DOMAIN, key);

        when(client.delete()).thenReturn(deleteBuilder);
        when(deleteBuilder.forPath(path)).thenThrow(new Exception("comunicazione con Zookeeper interrotta"));

        assertThrows(KeymasterException.class, () -> confParamOps.remove(DOMAIN, key));

        verify(client).delete();
        verify(client.delete()).forPath(path);
    }
}