package com.xored.mg42.agent;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.commons.Method;

public class SourceMethod {
	public SourceMethod(Method method) {
		this.method = method;
	}

	public SourceClass parent;
	public Method method;
	public final Map<String, HandlerMethod> points = new HashMap<String, HandlerMethod>();

	public boolean matches(String name, String desc) {
		return MethodUtils.matches(method, name, desc);
	}

}
