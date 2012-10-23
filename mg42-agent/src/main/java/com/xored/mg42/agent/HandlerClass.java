package com.xored.mg42.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HandlerClass {
	public HandlerClass(int classId, String qname, HandlerMethod[] handlers) {
		this.classId = classId;
		this.type = HandlerGroup.getTypeByQName(qname);
		this.handlers = handlers;
		for (HandlerMethod method : handlers) {
			method.parent = this;
			methodsMap.put(method.methodName, method);
		}
	}

	public final int classId;
	public final Type type;
	public final HandlerMethod[] handlers;
	private final Map<String, HandlerMethod> methodsMap = new HashMap<String, HandlerMethod>();

	public HandlerMethod find(String name) {
		return methodsMap.get(name);
	}

	public static HandlerClass fromJson(int classId, String qname,
			JsonObject methods) {
		int methodId = 0;
		List<HandlerMethod> children = new ArrayList<HandlerMethod>();
		List<HandlerMethod> onEnterMethods = new ArrayList<HandlerMethod>();
		List<HandlerMethod> onExitMethods = new ArrayList<HandlerMethod>();
		for (Entry<String, JsonElement> entry : methods.entrySet()) {
			HandlerMethod method = HandlerMethod.fromJson(methodId,
					entry.getKey(), entry.getValue().getAsJsonObject());
			children.add(method);
			if (Config.ON_ENTER_POINT.equals(method.point)) {
				onEnterMethods.add(method);
			}
			if (Config.ON_EXIT_POINT.equals(method.point)) {
				onExitMethods.add(method);
			}
			methodId++;
		}
		for (HandlerMethod enterMethod : onEnterMethods) {
			for (HandlerMethod exitMethod : onExitMethods) {
				if (enterMethod.callee.equals(exitMethod.callee)) {
					enterMethod.setHasRespectiveExitPoint();
					exitMethod.setHasRespectiveEnterPoint();
					break;
				}
			}
		}
		return new HandlerClass(classId, qname,
				children.toArray(new HandlerMethod[children.size()]));
	}
}
