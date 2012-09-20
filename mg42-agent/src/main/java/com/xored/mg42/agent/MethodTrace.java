package com.xored.mg42.agent;

import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;

import org.objectweb.asm.Type;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MethodTrace {
	public MethodTrace(String method, String[] args, String onEnter,
			String onExit) {
		this.method = method;
		this.args = args;
		this.onEnter = onEnter;
		this.onExit = onExit;
	}

	@Override
	public String toString() {
		return String.format("%s(%s) { enter(%s), exit(%s) }", method, Joiner
				.on(",").join(args), onEnter, onExit);
	}

	public final String method;
	public final String[] args;
	public final String onEnter;
	public final String onExit;

	private static final String METHOD = "method";
	private static final String ARGS = "args";
	private static final String ON_ENTER = "onEnter";
	private static final String ON_EXIT = "onExit";

	public static MethodTrace fromJson(JsonObject object) {
		return new MethodTrace(object.get(METHOD).getAsString(), toArray(
				transform(object.get(ARGS).getAsJsonArray(),
						new Function<JsonElement, String>() {
							@Override
							public String apply(JsonElement input) {
								return input.getAsString();
							}
						}), String.class), asString(object.get(ON_ENTER)),
				asString(object.get(ON_EXIT)));

	}

	private static final String asString(JsonElement element) {
		return element == null ? null : element.getAsString();
	}

	public boolean matches(String name, String desc) {
		if (!method.equals(name))
			return false;
		Type type = Type.getMethodType(desc);
		Type[] argTypes = type.getArgumentTypes();
		if (argTypes.length != args.length)
			return false;

		for (int i = 0; i < args.length; i++) {
			if (!argTypes[i].getClassName().equals(args[i])) {
				return false;
			}
		}
		return true;
	}
}
