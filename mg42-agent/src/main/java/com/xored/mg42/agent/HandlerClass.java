package com.xored.mg42.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.objectweb.asm.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HandlerClass {
	public HandlerClass(int classId, String qname, HandlerMethod[] tracers) {
		this.classId = classId;
		this.type = HandlerGroup.getTypeByQName(qname);
		this.tracers = tracers;
		for (HandlerMethod method : tracers) {
			method.parent = this;
		}
	}

	public final int classId;
	public final Type type;
	public final HandlerMethod[] tracers;

	public static HandlerClass fromJson(int classId, int firstMethodId,
			String qname, JsonObject methods) {
		int methodId = firstMethodId;
		List<HandlerMethod> children = new ArrayList<HandlerMethod>();
		for (Entry<String, JsonElement> entry : methods.entrySet()) {
			children.add(HandlerMethod.fromJson(methodId, entry.getKey(), entry
					.getValue().getAsJsonObject()));
			methodId++;
		}
		return new HandlerClass(classId, qname,
				children.toArray(new HandlerMethod[children.size()]));
	}
}
