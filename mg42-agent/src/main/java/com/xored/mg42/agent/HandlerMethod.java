package com.xored.mg42.agent;

import org.objectweb.asm.commons.Method;

import com.google.gson.JsonObject;

public class HandlerMethod implements MG42Runtime {
	public HandlerMethod(int methodId, String point, String callee,
			String handler) {
		this.id = methodId;
		this.point = point;
		this.callee = callee;
		this.methodName = handler;
		this.method = new Method(methodName, pointSignatures.get(point));
	}

	public HandlerClass parent;
	public final int id;
	public final String point;
	public final String callee;
	public final String methodName;
	public final Method method;

	public boolean isOnExit() {
		return point.equals(Config.ON_EXIT_POINT);
	};

	public static HandlerMethod fromJson(int id, String handler,
			JsonObject object) {
		return new HandlerMethod(id, object.get("point").getAsString(), object
				.get("method").getAsString(), handler);
	}
}
