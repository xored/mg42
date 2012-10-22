package com.xored.mg42.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class Tracer {

	private enum PointType {
		ENTER, EXIT
	};

	private static Map<Integer, TracerGroup> traceGroups = new HashMap<Integer, TracerGroup>();
	private static Map<Integer, Map<Integer, MethodDescription>> methodDescriptions = new HashMap<Integer, Map<Integer, MethodDescription>>();
	private static Map<String, Stack<Long>> callIds = new ConcurrentHashMap<String, Stack<Long>>();
	private static long nextId;
	private static boolean isStarted = TracerConfig.getStartArg();
	private static int port = TracerConfig.getPortArg();
	private static final Thread parentThread = Thread.currentThread();

	private static DateFormat dateFormat = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss.SSS");
	private static JsonOutputWriter outputWriter = new JsonOutputWriter(
			TracerConfig.getOutput());

	public static synchronized long getNextId() {
		return nextId++;
	}

	static {
		if (port != -1) {
			Thread tracerServer = new Thread(new Runnable() {
				@Override
				public void run() {
					ServerSocket srv = null;
					try {
						srv = new ServerSocket(port);
						srv.setSoTimeout(1000);
						while (!Thread.State.TERMINATED.equals(parentThread
								.getState())) {
							try {
								Socket socket = srv.accept();
								new HandleClient(socket);
							} catch (SocketTimeoutException e) {
								// Ignore
							}
						}
					} catch (IOException e) {
						System.out.println("Tracer server error: "
								+ e.getMessage());
					} finally {
						try {
							if (srv != null) {
								srv.close();
							}
						} catch (IOException e) {
							// Ignore
						}
					}
				}
			});
			tracerServer.start();
		}
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
		if (isStarted && traceGroups.containsKey(classId)) {
			CapturedEvent captured = new CapturedEvent();
			fillCapturedInfo(classId, traceId, captured, PointType.ENTER);
			captured.data = traceGroups.get(classId).mg42MethodProxy(traceId,
					instance, args, null);
			captured.kind = "start";

			outputWriter.write(captured);
		}
	}

	public static void methodEnd(Object result, int classId, int traceId,
			Object instance, Object[] args) {
		if (isStarted && traceGroups.containsKey(classId)) {
			CapturedEvent captured = new CapturedEvent();
			captured.data = traceGroups.get(classId).mg42MethodProxy(traceId,
					instance, args, result);
			captured.kind = "end";
			fillCapturedInfo(classId, traceId, captured, PointType.EXIT);

			outputWriter.write(captured);
		}
	}

	private static void fillCapturedInfo(int classId, int traceId,
			CapturedEvent captured, PointType pointType) {
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
				if (PointType.ENTER.equals(pointType)
						&& desc.hasRespectiveExitPoint) {
					captured.callId = getNextId();
					if (!callIds.containsKey(methodKey)) {
						callIds.put(methodKey, new Stack<Long>());
					}
					Stack<Long> callStack = callIds.get(methodKey);
					callStack.push(captured.callId);
				} else if (PointType.EXIT.equals(pointType)
						&& desc.hasRespectiveEnterPoint) {
					if (callIds.containsKey(methodKey)) {
						Stack<Long> callStack = callIds.get(methodKey);
						captured.callId = callStack.pop();
						if (callStack.isEmpty()) {
							callIds.remove(methodKey);
						}
					}
				}
			}
		}
	}

	public static void addGroup(int classId, TracerGroup group) {
		traceGroups.put(classId, group);
	}

	public static void addMethodDescription(int classId, int methodId,
			String desc, boolean hasRespectiveExitPoint,
			boolean hasRespectiveEnterPoint) {
		if (!methodDescriptions.containsKey(classId)) {
			methodDescriptions.put(classId,
					new HashMap<Integer, MethodDescription>());
		}
		Map<Integer, MethodDescription> record = methodDescriptions
				.get(classId);
		record.put(methodId, new MethodDescription(desc,
				hasRespectiveExitPoint, hasRespectiveEnterPoint));
	}

	static class MethodDescription {
		final String method;
		final boolean hasRespectiveExitPoint;
		final boolean hasRespectiveEnterPoint;

		public MethodDescription(String method, boolean hasRespectiveExitPoint,
				boolean hasRespectiveEnterPoint) {
			this.method = method;
			this.hasRespectiveExitPoint = hasRespectiveExitPoint;
			this.hasRespectiveEnterPoint = hasRespectiveEnterPoint;
		}
	}

	static class CapturedEvent {
		String method;
		String kind;
		long threadId;
		String threadName;
		String timestamp;
		Long callId;
		Object data;
	}

	static class HandleClient extends Thread {
		private static final String CMD_START = "start";
		private static final String CMD_STOP = "stop";
		private static final String CMD_STATUS = "status";
		private static final String CMD_EXIT = "exit";

		private static final String lineDelimeter = System
				.getProperty("line.separator");

		Socket socket;
		BufferedReader input;
		PrintWriter output;

		public HandleClient(Socket socket) throws IOException {
			this.socket = socket;
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
			output.println("Wellcome to mg42 tracer server. Commands:"
					+ lineDelimeter + "start - start tracing" + lineDelimeter
					+ "stop - stop tracing" + lineDelimeter
					+ "status - current state of tracer server" + lineDelimeter
					+ "exit - disconnect from tracer server");
			start();
		}

		private void showStatus() {
			if (isStarted) {
				output.println("Tracer server started.");
			} else {
				output.println("Tracer server stopped.");
			}
		}

		public void run() {
			String cmd = null;
			try {
				while (!Thread.State.TERMINATED.equals(parentThread.getState())
						&& socket.isConnected() && !CMD_EXIT.equals(cmd)) {
					cmd = input.readLine();
					if (CMD_STOP.equals(cmd)) {
						isStarted = false;
						showStatus();
					} else if (CMD_START.equals(cmd)) {
						isStarted = true;
						showStatus();
					} else if (CMD_STATUS.equals(cmd)) {
						showStatus();
					} else if (!CMD_EXIT.equals(cmd) && cmd != null) {
						output.println("Unrecognized command: " + cmd);
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				try {
					input.close();
					output.close();
					socket.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
	}
}
