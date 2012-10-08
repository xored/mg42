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

	public static final Method methodStart = Method
			.getMethod("void methodStart(int, int, Object, Object[])");

	public static final Method methodEnd = Method
			.getMethod("void methodEnd(Object, int, int, Object, Object[])");

	public static final Method methodAddGroup = Method
			.getMethod("void addGroup(int, com.xored.mg42.runtime.TracerGroup)");

	public final static String interfaceTracerGroup = "com/xored/mg42/runtime/TracerGroup";
	public final static String methodProxyTGName = "mg42MethodProxy";
	public final static String methodProxyTGSignature = "(ILjava/lang/Object;[Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
	public final static Method methodMG42Proxy = new Method(methodProxyTGName,
			methodProxyTGSignature);

	public final static String pointEnterSignature = "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";
	public final static String pointExitSignature = "(Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";
}
