package com.xored.mg42.runtime;

import java.util.HashMap;
import java.util.Map;

public class Tracer {

	private static Map<Integer, TracerGroup> traceGroups = new HashMap<Integer, TracerGroup>();

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
		if (traceGroups.containsKey(classId)) {
			traceGroups.get(classId).mg42MethodProxy(traceId, instance, args,
					null);
		}
	}

	public static void methodEnd(Object result, int classId, int traceId,
			Object instance, Object[] args) {
		if (traceGroups.containsKey(classId)) {
			traceGroups.get(classId).mg42MethodProxy(traceId, instance, args,
					result);
		}
	}

	public static void addGroup(int groupId, TracerGroup group) {
		traceGroups.put(groupId, group);
	}
}
