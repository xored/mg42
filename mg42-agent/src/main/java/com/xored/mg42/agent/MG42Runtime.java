package com.xored.mg42.agent;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

/**
 * Class and method names from mg42 runtime (to avoid direct dependencies)
 * 
 * @author ivaninozemtsev
 * 
 */
public interface MG42Runtime {

	static final String TRACER_DESCRIPTOR = "com/xored/mg42/runtime/Tracer";
	public static final Type Tracer = Type.getObjectType(TRACER_DESCRIPTOR);
	static final String TRACER_QNAME = Tracer.getClassName();

	public static final Method getDefault = Method.getMethod(String.format(
			"%s getDefault()", TRACER_QNAME));

	public static final Method methodStart = Method
			.getMethod("void methodStart(String, Object, Object[])");
	public static final Method methodEnd = Method
			.getMethod("void methodEnd(String, Object, Object[])");

}
