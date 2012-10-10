package com.xored.mg42.agent;

import java.util.Map;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import com.google.common.collect.ImmutableMap;

/**
 * Class and method names from mg42 runtime (to avoid direct dependencies)
 * 
 * @author ivaninozemtsev
 * 
 */
public interface MG42Runtime {

	public static final Type Tracer = Type
			.getObjectType("com/xored/mg42/runtime/Tracer");

	public static final Method methodStart = Method
			.getMethod("void methodStart(int, int, Object, Object[])");

	public static final Method methodEnd = Method
			.getMethod("void methodEnd(Object, int, int, Object, Object[])");

	public static final Method methodAddGroup = Method
			.getMethod("void addGroup(int, com.xored.mg42.runtime.TracerGroup)");

	public static final Method methodAddMethodDescription = Method
			.getMethod("void addMethodDescription(int, int, String)");

	public static final Method defaultConstructor = Method
			.getMethod("void <init> ()");

	public static final Type TracerGroup = Type
			.getObjectType("com/xored/mg42/runtime/TracerGroup");

	public final static Method methodMG42Proxy = new Method("mg42MethodProxy",
			"(ILjava/lang/Object;[Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

	public final static Map<String, String> pointSignatures = new ImmutableMap.Builder<String, String>()
			.put(Config.ON_ENTER_POINT,
					"(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;")
			.put(Config.ON_EXIT_POINT,
					"(Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;")
			.build();

	public static final String tracerConfigClassName = "com/xored/mg42/runtime/TracerConfig";

	public static final Method methodGetOutputArg = Method
			.getMethod("String getOutputArg()");
}
