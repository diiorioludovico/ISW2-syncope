package org.apache.syncope.common.keymaster.client.zookeeper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.syncope.common.keymaster.client.api.KeymasterException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Mockito.*;

/**
 * Tests the constructor, path construction, listing, retrieval, storage,
 * removal, and exception handling of ZookeeperConfParamOps.
 *
 * Source under test: [1]
 *
 * Dependencies: JUnit 4 and Mockito.
 * No running ZooKeeper server is required.
 */
public class ZookeeperConfParamOpsLLM1Test {

    private static final String DOMAIN = "test-domain";
    private static final String KEY = "test-key";
    private static final String DOMAIN_PATH = "/conf/test-domain";
    private static final String KEY_PATH = "/conf/test-domain/test-key";

    private CuratorFramework client;
    private ZookeeperConfParamOps subject;

    @Before
    public void setUp() {
        client = mock(CuratorFramework.class, RETURNS_DEEP_STUBS);
        subject = new ZookeeperConfParamOps(client);
    }

    private static byte[] json(final String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    private static KeymasterException expectKeymasterException(final Runnable action) {
        try {
            action.run();
        } catch (KeymasterException e) {
            return e;
        }
        throw new AssertionError("Expected KeymasterException");
    }

    private static void assertCause(
            final Throwable expected,
            final KeymasterException actual) {

        assertSame("The original exception must be preserved", expected, actual.getCause());
    }

    // Constructor and path construction

    @Test
    public void constructorRetainsProvidedClient() {
        assertSame(client, subject.client);
    }

    @Test
    public void buildConfPathWithNoPartsReturnsRootWithTrailingSlash() {
        assertEquals("/conf/", ZookeeperConfParamOps.buildConfPath());
    }

    @Test
    public void buildConfPathWithDomain() {
        assertEquals(DOMAIN_PATH, ZookeeperConfParamOps.buildConfPath(DOMAIN));
    }

    @Test
    public void buildConfPathWithDomainAndKey() {
        assertEquals(KEY_PATH, ZookeeperConfParamOps.buildConfPath(DOMAIN, KEY));
    }

    @Test
    public void buildConfPathPreservesAllPartsAndTheirOrder() {
        assertEquals(
                "/conf/domain/section/key",
                ZookeeperConfParamOps.buildConfPath("domain", "section", "key"));
    }

    @Test
    public void buildConfPathDoesNotNormalizeEmptyPartsOrSlashes() {
        assertEquals("/conf/", ZookeeperConfParamOps.buildConfPath(""));
        assertEquals("/conf//key", ZookeeperConfParamOps.buildConfPath("", "key"));
        assertEquals("/conf/domain/", ZookeeperConfParamOps.buildConfPath("domain", ""));
        assertEquals(
                "/conf//domain//key/",
                ZookeeperConfParamOps.buildConfPath("/domain/", "key/"));
    }

    // list()

    @Test
    public void listExistingEmptyDomainReturnsEmptyMapWithoutCreatingNode() throws Exception {
        when(client.checkExists().forPath(DOMAIN_PATH)).thenReturn(new Stat());
        when(client.getChildren().forPath(DOMAIN_PATH)).thenReturn(Collections.emptyList());

        Map<?, ?> result = subject.list(DOMAIN);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(client, never()).create();
    }

    @Test
    public void listMissingDomainCreatesParentContainersAndReturnsEmptyMap() throws Exception {
        when(client.checkExists().forPath(DOMAIN_PATH)).thenReturn(null);
        when(client.getChildren().forPath(DOMAIN_PATH)).thenReturn(Collections.emptyList());

        Map<?, ?> result = subject.list(DOMAIN);

        assertTrue(result.isEmpty());
        verify(client.create().creatingParentContainersIfNeeded()).forPath(DOMAIN_PATH);
        verify(client.getChildren()).forPath(DOMAIN_PATH);
    }

    @Test
    public void listDeserializesValuesAndSortsKeys() throws Exception {
        when(client.checkExists().forPath(DOMAIN_PATH)).thenReturn(new Stat());
        when(client.getChildren().forPath(DOMAIN_PATH))
                .thenReturn(Arrays.asList("zeta", "alpha", "middle"));

        when(client.getData().forPath(DOMAIN_PATH + "/zeta")).thenReturn(json("\"last\""));
        when(client.getData().forPath(DOMAIN_PATH + "/alpha")).thenReturn(json("42"));
        when(client.getData().forPath(DOMAIN_PATH + "/middle")).thenReturn(json("true"));

        Map<?, ?> result = subject.list(DOMAIN);

        assertEquals(3, result.size());
        assertEquals(42, result.get("alpha"));
        assertEquals(Boolean.TRUE, result.get("middle"));
        assertEquals("last", result.get("zeta"));
        assertEquals(
                Arrays.asList("alpha", "middle", "zeta"),
                new ArrayList<>(result.keySet()));
    }

    @Test
    public void listDeserializesNestedObjectsArraysAndNull() throws Exception {
        when(client.checkExists().forPath(DOMAIN_PATH)).thenReturn(new Stat());
        when(client.getChildren().forPath(DOMAIN_PATH))
                .thenReturn(Arrays.asList("object", "array", "null"));

        when(client.getData().forPath(DOMAIN_PATH + "/object"))
                .thenReturn(json("{\"enabled\":true,\"count\":2}"));
        when(client.getData().forPath(DOMAIN_PATH + "/array"))
                .thenReturn(json("[1,\"two\",false]"));
        when(client.getData().forPath(DOMAIN_PATH + "/null"))
                .thenReturn(json("null"));

        Map<?, ?> result = subject.list(DOMAIN);

        assertTrue(result.get("object") instanceof Map);
        Map<?, ?> object = (Map<?, ?>) result.get("object");
        assertEquals(Boolean.TRUE, object.get("enabled"));
        assertEquals(2, object.get("count"));
        assertEquals(Arrays.asList(1, "two", false), result.get("array"));
        assertTrue(result.containsKey("null"));
        assertNull(result.get("null"));
    }

    @Test
    public void listWrapsExistenceCheckFailure() throws Exception {
        Exception failure = new Exception("existence check failed");
        when(client.checkExists().forPath(DOMAIN_PATH)).thenThrow(failure);

        assertCause(failure, expectKeymasterException(() -> subject.list(DOMAIN)));
    }

    @Test
    public void listWrapsDomainCreationFailure() throws Exception {
        Exception failure = new Exception("creation failed");
        when(client.checkExists().forPath(DOMAIN_PATH)).thenReturn(null);
        when(client.create().creatingParentContainersIfNeeded().forPath(DOMAIN_PATH))
                .thenThrow(failure);

        assertCause(failure, expectKeymasterException(() -> subject.list(DOMAIN)));
        verify(client, never()).getChildren();
    }

    @Test
    public void listWrapsChildrenLookupFailure() throws Exception {
        Exception failure = new Exception("children lookup failed");
        when(client.checkExists().forPath(DOMAIN_PATH)).thenReturn(new Stat());
        when(client.getChildren().forPath(DOMAIN_PATH)).thenThrow(failure);

        assertCause(failure, expectKeymasterException(() -> subject.list(DOMAIN)));
    }

    @Test
    public void listWrapsChildDataReadFailureIncludingMissingNode() throws Exception {
        KeeperException.NoNodeException failure = new KeeperException.NoNodeException();
        when(client.checkExists().forPath(DOMAIN_PATH)).thenReturn(new Stat());
        when(client.getChildren().forPath(DOMAIN_PATH))
                .thenReturn(Collections.singletonList(KEY));
        when(client.getData().forPath(KEY_PATH)).thenThrow(failure);

        assertCause(failure, expectKeymasterException(() -> subject.list(DOMAIN)));
    }

    @Test
    public void listWrapsInvalidJson() throws Exception {
        when(client.checkExists().forPath(DOMAIN_PATH)).thenReturn(new Stat());
        when(client.getChildren().forPath(DOMAIN_PATH))
                .thenReturn(Collections.singletonList(KEY));
        when(client.getData().forPath(KEY_PATH)).thenReturn(json("{invalid-json"));

        KeymasterException error = expectKeymasterException(() -> subject.list(DOMAIN));

        assertTrue(error.getCause() instanceof com.fasterxml.jackson.core.JsonProcessingException);
    }

    // get()

    @Test
    public void getReturnsStoredStringInsteadOfDefault() throws Exception {
        when(client.getData().forPath(KEY_PATH)).thenReturn(json("\"stored value\""));

        assertEquals("stored value", subject.get(DOMAIN, KEY, "fallback", String.class));
        verify(client.getData()).forPath(KEY_PATH);
    }

    @Test
    public void getDeserializesInteger() throws Exception {
        when(client.getData().forPath(KEY_PATH)).thenReturn(json("123"));

        assertEquals(
                Integer.valueOf(123),
                subject.get(DOMAIN, KEY, Integer.valueOf(-1), Integer.class));
    }

    @Test
    public void getPreservesFalseZeroAndEmptyStringInsteadOfUsingDefault() throws Exception {
        when(client.getData().forPath(KEY_PATH))
                .thenReturn(json("false"), json("0"), json("\"\""));

        assertEquals(Boolean.FALSE, subject.get(DOMAIN, KEY, Boolean.TRUE, Boolean.class));
        assertEquals(Integer.valueOf(0), subject.get(DOMAIN, KEY, 99, Integer.class));
        assertEquals("", subject.get(DOMAIN, KEY, "fallback", String.class));
    }

    @Test
    public void getDeserializesObjectUsingRequestedType() throws Exception {
        when(client.getData().forPath(KEY_PATH))
                .thenReturn(json("{\"name\":\"worker\",\"count\":4}"));

        Map<?, ?> result = subject.get(DOMAIN, KEY, null, Map.class);

        assertNotNull(result);
        assertEquals("worker", result.get("name"));
        assertEquals(4, result.get("count"));
    }

    @Test
    public void getMissingNodeReturnsExactDefaultInstance() throws Exception {
        when(client.getData().forPath(KEY_PATH))
                .thenThrow(new KeeperException.NoNodeException());
        String fallback = new String("fallback");

        assertSame(fallback, subject.get(DOMAIN, KEY, fallback, String.class));
        verify(client, never()).create();
    }

    @Test
    public void getMissingNodeWithNullDefaultReturnsNull() throws Exception {
        when(client.getData().forPath(KEY_PATH))
                .thenThrow(new KeeperException.NoNodeException());

        assertNull(subject.get(DOMAIN, KEY, null, String.class));
    }

    @Test
    public void getJsonNullReturnsExactDefaultInstance() throws Exception {
        when(client.getData().forPath(KEY_PATH)).thenReturn(json("null"));
        String fallback = new String("fallback");

        assertSame(fallback, subject.get(DOMAIN, KEY, fallback, String.class));
    }

    @Test
    public void getJsonNullWithNullDefaultReturnsNull() throws Exception {
        when(client.getData().forPath(KEY_PATH)).thenReturn(json("null"));

        assertNull(subject.get(DOMAIN, KEY, null, String.class));
    }

    @Test
    public void getWrapsNonMissingNodeReadFailure() throws Exception {
        KeeperException.ConnectionLossException failure =
                new KeeperException.ConnectionLossException();
        when(client.getData().forPath(KEY_PATH)).thenThrow(failure);

        assertCause(
                failure,
                expectKeymasterException(() -> subject.get(DOMAIN, KEY, "fallback", String.class)));
    }

    @Test
    public void getWrapsMalformedJsonRatherThanReturningDefault() throws Exception {
        when(client.getData().forPath(KEY_PATH)).thenReturn(json("{invalid-json"));

        KeymasterException error = expectKeymasterException(
                () -> subject.get(DOMAIN, KEY, "fallback", String.class));

        assertTrue(error.getCause() instanceof com.fasterxml.jackson.core.JsonProcessingException);
    }

    @Test
    public void getWrapsIncompatibleJsonType() throws Exception {
        when(client.getData().forPath(KEY_PATH)).thenReturn(json("{\"value\":1}"));

        KeymasterException error = expectKeymasterException(
                () -> subject.get(DOMAIN, KEY, 0, Integer.class));

        assertTrue(error.getCause() instanceof com.fasterxml.jackson.databind.JsonMappingException);
    }

    // set()

    @Test
    public void setExistingNodeWritesJsonWithoutCreatingNode() throws Exception {
        when(client.checkExists().forPath(KEY_PATH)).thenReturn(new Stat());

        subject.set(DOMAIN, KEY, "stored value");

        verify(client.setData()).forPath(eq(KEY_PATH), aryEq(json("\"stored value\"")));
        verify(client, never()).create();
    }

    @Test
    public void setMissingNodeCreatesParentContainersBeforeWriting() throws Exception {
        when(client.checkExists().forPath(KEY_PATH)).thenReturn(null);

        subject.set(DOMAIN, KEY, 42);

        org.mockito.InOrder order = inOrder(
                client.create().creatingParentContainersIfNeeded(),
                client.setData());

        order.verify(client.create().creatingParentContainersIfNeeded()).forPath(KEY_PATH);
        order.verify(client.setData()).forPath(eq(KEY_PATH), aryEq(json("42")));
    }

    @Test
    public void setSerializesStructuredValue() throws Exception {
        when(client.checkExists().forPath(KEY_PATH)).thenReturn(new Stat());

        Map<String, Object> value = new LinkedHashMap<>();
        value.put("name", "worker");
        value.put("enabled", true);
        value.put("ports", Arrays.asList(8080, 8081));

        subject.set(DOMAIN, KEY, value);

        org.mockito.ArgumentCaptor<byte[]> data =
                org.mockito.ArgumentCaptor.forClass(byte[].class);
        verify(client.setData()).forPath(eq(KEY_PATH), data.capture());

        assertEquals(
                value,
                ZookeeperConfParamOps.MAPPER.readValue(data.getValue(), Map.class));
    }

    @Test
    public void setEscapesSpecialCharactersAndPreservesUnicode() throws Exception {
        when(client.checkExists().forPath(KEY_PATH)).thenReturn(new Stat());
        String value = "quote: \" backslash: \\ newline:\n unicode: \u00e8";

        subject.set(DOMAIN, KEY, value);

        org.mockito.ArgumentCaptor<byte[]> data =
                org.mockito.ArgumentCaptor.forClass(byte[].class);
        verify(client.setData()).forPath(eq(KEY_PATH), data.capture());

        assertEquals(
                value,
                ZookeeperConfParamOps.MAPPER.readValue(data.getValue(), String.class));
    }

    @Test
    public void setNullDeletesNodeWithoutCheckingCreatingOrWriting() throws Exception {
        subject.set(DOMAIN, KEY, null);

        verify(client.delete()).forPath(KEY_PATH);
        verify(client, never()).checkExists();
        verify(client, never()).create();
        verify(client, never()).setData();
    }

    @Test
    public void setNullDelegatesToOverriddenRemove() {
        final String[] received = new String[2];

        ZookeeperConfParamOps recordingSubject = new ZookeeperConfParamOps(client) {
            @Override
            public void remove(final String domain, final String key) {
                received[0] = domain;
                received[1] = key;
            }
        };

        recordingSubject.set(DOMAIN, KEY, null);

        assertArrayEquals(new String[] {DOMAIN, KEY}, received);
        verifyNoInteractions(client);
    }

    @Test
    public void setWrapsExistenceCheckFailure() throws Exception {
        Exception failure = new Exception("existence check failed");
        when(client.checkExists().forPath(KEY_PATH)).thenThrow(failure);

        assertCause(failure, expectKeymasterException(() -> subject.set(DOMAIN, KEY, "value")));
        verify(client, never()).setData();
    }

    @Test
    public void setWrapsCreationFailureAndDoesNotWriteData() throws Exception {
        Exception failure = new Exception("creation failed");
        when(client.checkExists().forPath(KEY_PATH)).thenReturn(null);
        when(client.create().creatingParentContainersIfNeeded().forPath(KEY_PATH))
                .thenThrow(failure);

        assertCause(failure, expectKeymasterException(() -> subject.set(DOMAIN, KEY, "value")));
        verify(client, never()).setData();
    }

    @Test
    public void setWrapsDataWriteFailure() throws Exception {
        Exception failure = new Exception("write failed");
        when(client.checkExists().forPath(KEY_PATH)).thenReturn(new Stat());
        when(client.setData().forPath(eq(KEY_PATH), any(byte[].class))).thenThrow(failure);

        assertCause(failure, expectKeymasterException(() -> subject.set(DOMAIN, KEY, "value")));
    }

    @Test
    public void setWrapsSerializationFailureAndDoesNotWriteData() throws Exception {
        when(client.checkExists().forPath(KEY_PATH)).thenReturn(new Stat());

        KeymasterException error = expectKeymasterException(
                () -> subject.set(DOMAIN, KEY, new UnserializableValue()));

        assertTrue(error.getCause() instanceof com.fasterxml.jackson.core.JsonProcessingException);
        verify(client.setData(), never()).forPath(anyString(), any(byte[].class));
    }

    @Test
    public void setNullPropagatesDeletionFailureWithoutDoubleWrapping() throws Exception {
        KeeperException.NoNodeException failure = new KeeperException.NoNodeException();
        when(client.delete().forPath(KEY_PATH)).thenThrow(failure);

        assertCause(failure, expectKeymasterException(() -> subject.set(DOMAIN, KEY, null)));
        verify(client, never()).setData();
    }

    // remove()

    @Test
    public void removeDeletesExactNode() throws Exception {
        subject.remove(DOMAIN, KEY);

        verify(client.delete()).forPath(KEY_PATH);
        verify(client, never()).checkExists();
        verify(client, never()).create();
        verify(client, never()).setData();
    }

    @Test
    public void removeWrapsMissingNodeFailure() throws Exception {
        KeeperException.NoNodeException failure = new KeeperException.NoNodeException();
        when(client.delete().forPath(KEY_PATH)).thenThrow(failure);

        assertCause(failure, expectKeymasterException(() -> subject.remove(DOMAIN, KEY)));
    }

    @Test
    public void removeWrapsConnectionFailure() throws Exception {
        KeeperException.ConnectionLossException failure =
                new KeeperException.ConnectionLossException();
        when(client.delete().forPath(KEY_PATH)).thenThrow(failure);

        assertCause(failure, expectKeymasterException(() -> subject.remove(DOMAIN, KEY)));
    }

    @Test
    public void removeWrapsUnexpectedRuntimeFailure() throws Exception {
        IllegalStateException failure = new IllegalStateException("unexpected failure");
        when(client.delete().forPath(KEY_PATH)).thenThrow(failure);

        assertCause(failure, expectKeymasterException(() -> subject.remove(DOMAIN, KEY)));
    }

    /**
     * A getter that consistently fails allows serialization errors to be tested
     * without changing the production mapper or relying on optional modules.
     */
    public static class UnserializableValue {

        public String getValue() {
            throw new IllegalStateException("Deliberate serialization failure");
        }
    }
}