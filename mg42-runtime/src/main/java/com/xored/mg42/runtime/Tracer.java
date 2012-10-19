package com.xored.mg42.runtime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Tracer {

	private static Map<Integer, TracerGroup> traceGroups = new HashMap<Integer, TracerGroup>();
	private static Map<Integer, Map<Integer, MethodDescription>> methodDescriptions = new HashMap<Integer, Map<Integer, MethodDescription>>();
	private static Map<String, Long> callIds = new ConcurrentHashMap<String, Long>();
	private static long nextId;

	private static DateFormat dateFormat = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss.SSS");
	private static JsonOutputWriter outputWriter = new JsonOutputWriter(
			TracerConfig.getOutput());

	public static synchronized long getNextId() {
		return nextId++;
	}

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
			CapturedEvent captured = new CapturedEvent();
			fillCapturedInfo(classId, traceId, captured);
			captured.data = traceGroups.get(classId).mg42MethodProxy(traceId,
					instance, args, null);
			captured.kind = "start";

			outputWriter.write(captured);
		}
	}

	public static void methodEnd(Object result, int classId, int traceId,
			Object instance, Object[] args) {
		if (traceGroups.containsKey(classId)) {
			CapturedEvent captured = new CapturedEvent();
			captured.data = traceGroups.get(classId).mg42MethodProxy(traceId,
					instance, args, result);
			captured.kind = "end";
			fillCapturedInfo(classId, traceId, captured);

			outputWriter.write(captured);
		}
	}

	private static void fillCapturedInfo(int classId, int traceId,
			CapturedEvent captured) {
		captured.timestamp = dateFormat.format(new Date());
		captured.threadId = Thread.currentThread().getId();
		captured.threadName = Thread.currentThread().getName();
		Map<Integer, MethodDescription> record = methodDescriptions
				.get(classId);
		if (record != null) {
			MethodDescription desc = record.get(traceId);
			if (desc != null) {
				captured.method = desc.method;
				String methodKey = captured.threadId + captured.method;
				if (desc.hasRespectiveExitPoint) {
					captured.callId = getNextId();
					callIds.put(methodKey, captured.callId);
				} else {
					if (callIds.containsKey(methodKey)) {
						captured.callId = callIds.get(methodKey);
						callIds.remove(methodKey);
					}
				}
			}
		}
	}

	public static void addGroup(int classId, TracerGroup group) {
		traceGroups.put(classId, group);
	}

	public static void addMethodDescription(int classId, int methodId,
			String desc, boolean hasRespectiveExitPoint) {
		if (!methodDescriptions.containsKey(classId)) {
			methodDescriptions.put(classId,
					new HashMap<Integer, MethodDescription>());
		}
		Map<Integer, MethodDescription> record = methodDescriptions
				.get(classId);
		record.put(methodId,
				new MethodDescription(desc, hasRespectiveExitPoint));
	}

	static class MethodDescription {
		public MethodDescription(String method, boolean hasRespectiveExitPoint) {
			this.method = method;
			this.hasRespectiveExitPoint = hasRespectiveExitPoint;
		}

		final String method;
		final boolean hasRespectiveExitPoint;
	}

	static class CapturedEvent {
		String method;
		String kind;
		long threadId;
		String threadName;
		String timestamp;
		long callId;
		Object data;
	}
}
