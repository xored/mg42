package com.xored.mg42.agent;

import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;

import com.google.common.base.Function;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ClassTrace {
	public ClassTrace(String name, MethodTrace[] methods) {
		this.name = name;
		this.methods = methods;
	}

	public final String name;
	public final MethodTrace[] methods;

	public MethodTrace find(String methodName, String signature) {
		for (MethodTrace trace : methods) {
			if (trace.matches(methodName, signature)) {
				return trace;
			}
		}

		return null;
	}

	public static ClassTrace fromJson(String name, JsonArray array) {
		return new ClassTrace(name, toArray(
				transform(array, new Function<JsonElement, MethodTrace>() {
					@Override
					public MethodTrace apply(JsonElement input) {
						return MethodTrace.fromJson((JsonObject) input);
					}
				}), MethodTrace.class));
	}
}
