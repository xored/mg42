package com.xored.mg42.runtime;

import java.util.HashMap;
import java.util.Map;

public class Tracer {
	/**
	 * Invoked on method start
	 * 
	 * @param methodHandle
	 *            description of called method
	 * @param instance
	 *            instance on which the method is called. <code>null</code> if
	 *            method is static
	 * @param args
	 *            method arguments
	 */
	public static void methodStart(int classId, int traceId, Object instance,
			Object[] args) {
		System.out.println(String.format("Started method %d:%d", classId,
				traceId));
		if (traceGroups.containsKey(classId)) {
			System.out.println(String.format(
					"Client code returned %s",
					traceGroups.get(classId).methodEnter(traceId, instance,
							args)));
		}
	}

	public static void methodEnd(Object result, int classId, int traceId,
			Object instance, Object[] args) {
		System.out.println(String.format("Ended method %d:%d, result: %s",
				classId, traceId, result));
	}

	private static Map<Integer, TracerGroup> traceGroups = new HashMap<Integer, TracerGroup>();

	public static void addGroup(int groupId, TracerGroup group) {
		traceGroups.put(groupId, group);
	}
}
