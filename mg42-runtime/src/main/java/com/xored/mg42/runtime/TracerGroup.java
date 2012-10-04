package com.xored.mg42.runtime;

public interface TracerGroup {
	Object mg42MethodEnter(int tracer, Object instance, Object[] args);

	Object mg42MethodExit(int tracer, Object instance, Object[] args,
			Object returnValue);
}
