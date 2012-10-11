package com.xored.mg42.runtime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Tracer {

	private static Map<Integer, TracerGroup> traceGroups = new HashMap<Integer, TracerGroup>();
	private static Map<Integer, Map<Integer, String>> methodDescriptions = new HashMap<Integer, Map<Integer, String>>();

	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static DateFormat dateFormat = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss.SSS");

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

			sendOutput(captured);
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

			sendOutput(captured);
		}
	}

	private static void fillCapturedInfo(int classId, int traceId,
			CapturedEvent captured) {
		captured.timestamp = dateFormat.format(new Date());
		captured.threadId = Thread.currentThread().getId();
		captured.threadName = Thread.currentThread().getName();
		Map<Integer, String> record = methodDescriptions.get(classId);
		if (record != null) {
			captured.method = record.get(traceId);
		}
	}

	private static void sendOutput(Object data) {
		System.out.println(gson.toJson(data));
	}

	public static void addGroup(int classId, TracerGroup group) {
		traceGroups.put(classId, group);
	}

	public static void addMethodDescription(int classId, int methodId,
			String desc) {
		if (!methodDescriptions.containsKey(classId)) {
			methodDescriptions.put(classId, new HashMap<Integer, String>());
		}
		Map<Integer, String> record = methodDescriptions.get(classId);
		record.put(methodId, desc);
	}

	static class CapturedEvent {
		String method;
		String kind;
		long threadId;
		String threadName;
		String timestamp;
		Object data;
	}
}
