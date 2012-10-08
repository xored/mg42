package com.xored.mg42.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.commons.Method;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class HandlerGroups {
	public HandlerGroups(Map<String, HandlerGroup> groups) {
		this.groups = groups;
	}

	private final Map<String, HandlerGroup> groups;

	public static HandlerGroups fromJson(JsonArray array) {
		int firstClassId = 0;
		Map<String, HandlerGroup> result = new HashMap<String, HandlerGroup>();
		for (JsonElement element : array) {
			HandlerGroup group = HandlerGroup.fromJson(firstClassId,
					element.getAsJsonObject());
			firstClassId += group.classes.size();
			result.put(group.entryClass.getInternalName(), group);
		}
		return new HandlerGroups(result);
	}

	public SourceClasses calcSources() {
		Map<String, Map<String, SourceMethod>> sources = new HashMap<String, Map<String, SourceMethod>>();

		Map<String, SourceClass> result = new HashMap<String, SourceClass>();

		for (HandlerGroup group : groups.values()) {
			for (HandlerClass hc : group.classes.values()) {
				for (HandlerMethod hm : hc.handlers) {
					String method = hm.callee;
					String point = hm.point;
					String sourceType = HandlerGroup.getType(method)
							.getInternalName();
					Method sourceMethod = HandlerGroup.getMethod(method);

					if (!sources.containsKey(sourceType)) {
						sources.put(sourceType,
								new HashMap<String, SourceMethod>());
					}

					Map<String, SourceMethod> sourceTypeMap = sources
							.get(sourceType);
					if (!sourceTypeMap.containsKey(method)) {
						sourceTypeMap.put(method,
								new SourceMethod(sourceMethod));
					}

					SourceMethod sm = sourceTypeMap.get(method);
					System.out.println("source method " + sm.method);
					sm.points.put(point, hm);
				}
			}
		}
		for (Entry<String, Map<String, SourceMethod>> entry : sources
				.entrySet()) {
			SourceClass sc = new SourceClass(entry.getKey(), entry.getValue()
					.values().toArray(new SourceMethod[0]));
			result.put(sc.type.getInternalName(), sc);
		}
		return new SourceClasses(result);
	}

	public HandlerGroup findGroup(String internalName) {
		return groups.get(internalName);
	}

	public boolean isGroupClass(String internalName) {
		return groups.containsKey(internalName);
	}

	public boolean isHandlerClass(String internalName) {
		for (HandlerGroup group : groups.values()) {
			if (group.hasClass(internalName)) {
				return true;
			}
		}
		return false;
	}

	public HandlerClass findHandlerClass(String internalName) {
		for (HandlerGroup group : groups.values()) {
			HandlerClass candidate = group.findClass(internalName);
			if (candidate != null) {
				return candidate;
			}
		}
		return null;
	}
}
