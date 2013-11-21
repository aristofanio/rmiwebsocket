package com.lambdazen.websocket;

import com.fasterxml.jackson.core.type.TypeReference;

public interface ComplexType<T> {
    public TypeReference<T> getTypeReference();

    public T getValue();
    
    public void setValue(T val);
}
