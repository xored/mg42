package com.xored.mg42.runtime;

public class Tracer {

	private Tracer() {}
	private static Tracer instance = new Tracer();
	public Tracer getDefault() {
		return instance;
	}

	/**
	 * Invoked on method start
	 * @param methodHandle description of called method
	 * @param instance instance on which the method is called. <code>null</code> if method is static
	 * @param args method arguments
	 */
	public void methodStart(String methodHandle, Object instance, Object[] args) {
		System.out.println(String.format("Started method %s", methodHandle));
	}
	
	public void methodEnd(String methodHandle, Object instance, Object[] args) {
		System.out.println(String.format("Ended method %s", methodHandle));
	}
}
