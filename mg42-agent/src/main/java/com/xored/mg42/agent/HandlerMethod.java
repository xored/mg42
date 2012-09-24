package com.xored.mg42.agent;

import com.google.gson.JsonObject;

public class HandlerMethod {
	public HandlerMethod(int methodId, String point, String callee,
			String handler) {
		this.methodId = methodId;
		this.point = point;
		this.callee = callee;
		this.handler = handler;
	}

	public HandlerClass parent;
	public final int methodId;
	public final String point;
	public final String callee;
	public final String handler;

	public static HandlerMethod fromJson(int id, String handler,
			JsonObject object) {
		return new HandlerMethod(id, object.get("point").getAsString(), object
				.get("method").getAsString(), handler);
	}
}
