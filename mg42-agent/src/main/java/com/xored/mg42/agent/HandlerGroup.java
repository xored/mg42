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
		return getTypeByQName(methodQname.substring(0, sharpIndex).replace('.',
				'/'));
	}

	public static Type getTypeByQName(String typeName) {
		return Type.getObjectType(typeName);
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

	public static HandlerGroup fromJson(int firstClassId, JsonObject object) {
		Map<String, HandlerClass> children = new HashMap<String, HandlerClass>();
		int classId = firstClassId;
		int firstMethodId = 0;
		for (Entry<String, JsonElement> entry : object.get("tracers")
				.getAsJsonObject().entrySet()) {
			HandlerClass child = HandlerClass.fromJson(classId, firstMethodId,
					entry.getKey(), entry.getValue().getAsJsonObject());
			classId++;
			firstMethodId += child.tracers.length;
			children.put(child.type.getClassName(), child);
		}
		return new HandlerGroup(object.get("entryPoint").getAsString(),
				children);
	}
}
