package com.lambdazen.websocket.sample;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.JsonNode;

import com.lambdazen.websocket.IRMIWebSocket;
import com.lambdazen.websocket.IRMIWebSocketListener;
import com.lambdazen.websocket.RMIWebSocketException;

public class CallTestService implements IRMIWebSocketListener {
    IRMIWebSocket rws;

    public CallTestService(IRMIWebSocket rws) {
        this.rws = rws;
    }

    public void onClose() {
        // Nothing to do
    }

    public void onDeserializationError(Exception e, String msg) {
        // Some error reporting
        System.err.println("RMIWebSocket deserialization error " + msg);
        e.printStackTrace();
    }

    public void onError(RMIWebSocketException e) {
        // Some error reporting
        System.err.println("RMIWebSocket error");
        e.printStackTrace();
    }

    // Remote method calls
    public void testSimpleTypes(String s, Integer i, BigDecimal bd, Double d, Float f) {
        try {
            rws.call("append", "testSimpleTypes", "String " + s + ", integer " + i + ", big decimal " + bd + ", double " + d + ", float " + f);
            rws.call("append", "testSimpleTypes (raw)", new Object[] { s, i, bd, d, f });
        } catch (RMIWebSocketException e) {
            e.printStackTrace();
        }
    }

    public void testUnboxedTypes(int i, double d, float f) {
        try {
            rws.call("append", "testUnboxedTypes", "integer " + i + ", double " + d + ", float " + f);
            rws.call("append", "testUnboxedTypes (raw)", new Object[] {i, d, f});
        } catch (RMIWebSocketException e) {
            e.printStackTrace();
        }
    }

    public void testArrays(String[] s, int[] i, BigDecimal[] bd, double[] d, Float[] f) {
        try {
            rws.call("append", "testArrays", "String[] " + Arrays.asList(s) + ", integer[] " + Arrays.asList(ArrayUtils.toObject(i)) + ", big decimal[] "
                    + Arrays.asList(bd) + ", double[] " + Arrays.asList(ArrayUtils.toObject(d)) + ", float[] " + Arrays.asList(f));

            // NOTE: Unboxed types won't work for array invocations to javascript
            rws.call("append", "testArrays (raw)", new Object[]{ s, ArrayUtils.toObject(i), bd, ArrayUtils.toObject(d), f });
        } catch (RMIWebSocketException e) {
            e.printStackTrace();
        }
    }

    // NOTE: Any object can be consumed as a JsonNode
    public void testJSONNode(JsonNode jn) {
        try {
            rws.call("append", "testJSONNodes", jn);
        } catch (RMIWebSocketException e) {
            e.printStackTrace();
        }
    }

    public void testMapper(Pojo p) {
        try {
            rws.call("append", "testMapper", p.toString());
        } catch (RMIWebSocketException e) {
            e.printStackTrace();
        }
    }

    public void testMap(Map m) {
        try {
            rws.call("append", "testMap", m);
        } catch (RMIWebSocketException e) {
            e.printStackTrace();
        }
    }

    public static class Pojo {
        String id;
        int[] intArray;
        Mojo m;

        public void setMojo(Mojo x) {
            m = x;
        }

        public void setId(String x) {
            id = x;
        }

        public void setIntArray(int[] x) {
            intArray = x;
        }

        public String toString() {
            return "Pojo(id=" + id + ", int[]=" + Arrays.asList(ArrayUtils.toObject(intArray)) + ", mojo=" + m + ")";
        }
    }

    public static class Mojo {
        int level;

        public void setLevel(int x) {
            level = x;
        }

        public String toString() {
            return "Mojo(" + level + ")";
        }
    }
}
