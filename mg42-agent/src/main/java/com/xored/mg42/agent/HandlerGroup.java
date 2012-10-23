package com.xored.mg42.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Group of class tracers which share a single entry point
 * 
 * @author ivaninozemtsev
 * 
 */
public class HandlerGroup {
	public HandlerGroup(String entryPoint, Map<String, HandlerClass> classes) {
		this.entryClass = getType(entryPoint);
		this.entryMethod = getMethod(entryPoint);
		this.classes = classes;
	}

	public static Type getType(String methodQname) {
		int sharpIndex = methodQname.indexOf('#');
		return getTypeByQName(methodQname.substring(0, sharpIndex));
	}

	public static Type getTypeByQName(String typeName) {
		return Type.getObjectType(typeName.replace('.', '/'));
	}

	public static Method getMethod(String methodQname) {
		int sharpIndex = methodQname.indexOf('#');
		return Method.getMethod(String.format("void %s",
				methodQname.substring(sharpIndex + 1)));
	}

	public final Type entryClass;
	public final Method entryMethod;
	public final Map<String, HandlerClass> classes;

	public boolean hasClass(String internalName) {
		return classes.containsKey(internalName);
	}

	public HandlerClass findClass(String internalName) {
		return classes.get(internalName);
	}

	public boolean isEntryMethod(String name, String desc) {
		return MethodUtils.matches(entryMethod, name, desc);
	}

	public static HandlerGroup fromJson(int firstClassId, JsonObject object) {
		Map<String, HandlerClass> children = new HashMap<String, HandlerClass>();
		int classId = firstClassId;
		for (Entry<String, JsonElement> entry : object.get("handlers")
				.getAsJsonObject().entrySet()) {
			HandlerClass child = HandlerClass.fromJson(classId, entry.getKey(),
					entry.getValue().getAsJsonObject());
			classId++;
			children.put(child.type.getInternalName(), child);
		}
		return new HandlerGroup(object.get("entryPoint").getAsString(),
				children);
	}
}
