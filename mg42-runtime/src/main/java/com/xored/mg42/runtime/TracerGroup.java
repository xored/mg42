package com.xored.mg42.runtime;

public interface TracerGroup {
	Object methodEnter(int tracer, Object instance, Object[] args);

	Object methodExit(int tracer, Object instance, Object[] args,
			Object returnValue);
}
