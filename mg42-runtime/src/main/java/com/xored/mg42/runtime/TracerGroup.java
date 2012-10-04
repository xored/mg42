package com.xored.mg42.runtime;

public interface TracerGroup {

	Object mg42MethodProxy(int tracer, Object instance, Object[] args,
			Object returnValue);
}
