package com.xored.mg42.runtime;

import com.google.gson.JsonElement;

public interface MethodProcessor {
	JsonElement handleMethod(Object instance, Object[] args);
}
