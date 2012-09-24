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
	public void methodStart(int groupId, int traceId, Object instance,
			Object[] args) {
		System.out.println(String.format("Started method %d:%d", groupId,
				traceId));
		if (traceGroups.containsKey(groupId)) {
			System.out.println(String.format(
					"Client code returned %s",
					traceGroups.get(groupId).methodEnter(traceId, instance,
							args)));
		}

	}

	public void methodEnd(Object result, int groupId, int traceId,
			Object instance, Object[] args) {
		System.out.println(String.format("Ended method %d:%d, result: %s",
				groupId, traceId, result));
	}

	private static Map<Integer, TracerGroup> traceGroups = new HashMap<Integer, TracerGroup>();

	public static void addGroup(int groupId, TracerGroup group) {
		traceGroups.put(groupId, group);
	}
}
