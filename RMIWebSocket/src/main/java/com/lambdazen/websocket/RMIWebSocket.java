package com.lambdazen.websocket;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.jetty.websocket.WebSocket;

public class RMIWebSocket implements WebSocket.OnTextMessage, IRMIWebSocket {
    public static final String METHOD = "method";
    public static final String PARAMS = "params";

    private Connection connection;
    private IRMIWebSocketListener wsListener;
    private HttpServletRequest request;
    private IRMIWebSocketListenerFactory factory;
    private boolean closed = false;
    private ObjectMapper mapper;
    private Map<String, Method> methodMap;

    public RMIWebSocket(HttpServletRequest request, IRMIWebSocketListenerFactory factory) {
        this.request = request;
        this.factory = factory;
        this.mapper = new ObjectMapper();
        this.methodMap = new HashMap<String, Method>();
    }

    @Override
    public synchronized void onClose(int closeCode, String message) {
        if ((!closed) && (wsListener != null)) {
            wsListener.onClose();
        }
        closed = true;
    }

    @Override
    public synchronized void onOpen(Connection conn) {
        this.connection = conn;

        this.wsListener = factory.createListener(this);
        
        if (wsListener == null) {
            // Listener was not successfully created. Close the connection
            close();
        } else {
            try {
                loadObjectDetails();
            } catch (RMIWebSocketException e) {
                wsListener.onError(e);
            }
        }
    }

    // Go through the callback handler's methods
    private void loadObjectDetails() throws RMIWebSocketException {
        //for (Method method : wsListener.getClass().getDeclaredMethods()) {
        for (Method method : wsListener.getClass().getMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) {
                // Skip protected and private methods
                continue;
            }

            String methodName = method.getName();
            String methodKey = getKey(methodName, method.getParameterTypes().length);

            if (methodMap.containsKey(methodKey)) {
                throw new RMIWebSocketException("Duplicate method with name " + methodName + " and parameter count " + method.getParameterTypes().length
                        + " defined in " + wsListener.getClass());
            }

            methodMap.put(methodKey, method);
        }
    }

    @Override
    public synchronized void onMessage(String jsonMsg) {
        if ((!closed) && (wsListener != null)) {
            String methodName;
            List<String> paramStrList = new ArrayList<String>();

            try {
                // Get the method and parameters
                JsonNode rootNode = mapper.readValue(jsonMsg, JsonNode.class);
                methodName = rootNode.get(METHOD).asText();
                Iterator<JsonNode> paramNodes = rootNode.get(PARAMS).elements();

                while (paramNodes.hasNext()) {
                    JsonNode param = paramNodes.next();
                    String paramStr = mapper.writeValueAsString(param);
                    paramStrList.add(paramStr);
                }

                // Lookup the appropriate method
                String methodKey = getKey(methodName, paramStrList.size());
                Method method = methodMap.get(methodKey);
                if (method == null) {
                    throw new RMIWebSocketException("Unable to locate method " + methodKey + " in the defined public methods " + methodMap.keySet() + " of "
                            + wsListener.getClass());
                }

                // Deserialize the parameters
                Object[] paramObjs = new Object[paramStrList.size()];
                Class[] paramTypes = method.getParameterTypes();
                for (int i = 0; i < paramTypes.length; i++) {
                    if (ComplexType.class.isAssignableFrom(paramTypes[i])) {
                        ComplexType paramHolder = (ComplexType) (paramTypes[i].newInstance());
                        Object value = mapper.readValue(paramStrList.get(i), paramHolder.getTypeReference());
                        paramHolder.setValue(value);
                        paramObjs[i] = paramHolder;
                    } else {
                        paramObjs[i] = mapper.readValue(paramStrList.get(i), paramTypes[i]);
                    }
                }

                // Invoke the method
                try {
                    method.invoke(wsListener, paramObjs);
                } catch (Exception e) {
                    wsListener.onError(new RMIWebSocketException("Error invoking method " + methodName, e));
                    return;
                }
            } catch (Exception e) {
                wsListener.onDeserializationError(e, jsonMsg);
                return;
            }
        }
    }

    private String getKey(String methodName, int paramCount) {
        return methodName + ":" + paramCount;
    }

    @Override
    public synchronized void close() {
        if (!closed) {
            connection.close();
        }
        closed = true;
    }

    @Override
    public synchronized void call(String method, Object... params) throws RMIWebSocketException {
        if (closed) {
            throw new RMIWebSocketException("WebSocket connection is already closed");
        } else if (!connection.isOpen()) {
            throw new RMIWebSocketException("WebSocket connection already closed by client");
        } else {
            ObjectNode rootNode = (ObjectNode) mapper.createObjectNode();
            rootNode.put(METHOD, method);
            ArrayNode paramArray = rootNode.putArray(PARAMS);

            for (Object param : params) {
                addToArray(paramArray, param, mapper);
            }

            String msg;
            try {
                msg = mapper.writeValueAsString(rootNode);
            } catch (IOException e) {
                throw new RMIWebSocketException("Unable to send serialize JsonNode " + rootNode, e);
            }

            try {
                connection.sendMessage(msg);
            } catch (IOException e) {
                throw new RMIWebSocketException("Unable to send message " + (msg.length() > 200 ? msg.substring(0, 200) + "..." : msg));
            }
        }
    }

    private void addToArray(ArrayNode arr, Object param, ObjectMapper mapper) throws RMIWebSocketException {
        if (param == null) {
            arr.addNull();
        } else if (param instanceof JsonNode) {
            arr.add((JsonNode) param);
        } else if (param instanceof Integer) {
            arr.add((Integer) param);
        } else if (param instanceof String) {
            arr.add((String) param);
        } else if (param instanceof Float) {
            arr.add((Float) param);
        } else if (param instanceof Double) {
            arr.add((Double) param);
        } else if (param instanceof BigDecimal) {
            arr.add((BigDecimal) param);
        } else if (param instanceof Map) {
            String jsonRepr;
            try {
                jsonRepr = mapper.writeValueAsString((Map) param);
            } catch (Exception e) {
                throw new RMIWebSocketException("Unable to serialize map " + param, e);
            }

            try {
                arr.add(mapper.readValue(jsonRepr, JsonNode.class));
            } catch (Exception e) {
                throw new RMIWebSocketException("Unable to de-serialize map contents " + jsonRepr, e);
            }
        } else if (param.getClass().isArray()) {
            ArrayNode childArr = arr.addArray();
            Object[] paramArr;
            try {
                paramArr = (Object[])param;
            } catch (ClassCastException e) {
                throw new RMIWebSocketException("Arrays of unboxed types like int, double are not supported. Use Integer[] and Double[]", e);
            }
            for (Object paramElem : (Object[])paramArr) {
                addToArray(childArr, paramElem, mapper);
            }
        } else {
            arr.addPOJO(param);
        }
    }

    @Override
    public HttpServletRequest getHttpServletRequest() {
        // TODO Auto-generated method stub
        return request;
    }
}
