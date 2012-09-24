package com.xored.mg42.runtime;

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
	public static void methodStart(String methodHandle, Object instance,
			Object[] args) {
		System.out.println(String.format("Started method %s", methodHandle));
	}

	public static void methodEnd(Object result, String methodHandle,
			Object instance, Object[] args) {
		System.out.println(String.format("Ended method %s, result: %s",
				methodHandle, result));
	}

}
