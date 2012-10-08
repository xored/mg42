package com.xored.mg42.agent;

import static com.google.common.collect.ObjectArrays.concat;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.TableSwitchGenerator;

public class HandlerClassTransformer extends ClassTransformer implements
		MG42Runtime {

	private final HandlerClass handlerClass;

	public HandlerClassTransformer(HandlerClass handlerClass) {
		this.handlerClass = handlerClass;
		this.accessFlags = new int[handlerClass.handlers.length];
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName,
				concat(interfaces, TracerGroup.getInternalName()));
	}

	private int[] accessFlags;

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		HandlerMethod method = handlerClass.find(name);
		if (method != null) {
			accessFlags[method.id] = access;
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}

	@Override
	public void visitEnd() {
		generateSwitch();
		super.visitEnd();
	}

	private void generateSwitch() {
		final GeneratorAdapter mv = new GeneratorAdapter(ACC_PUBLIC,
				methodMG42Proxy, null, null, cv);
		mv.loadArg(ARG_METHOD_ID);
		mv.tableSwitch(upto(handlerClass.handlers.length),
				new TableSwitchGenerator() {

					@Override
					public void generateDefault() {
						mv.push((Type) null);
						mv.returnValue();
					}

					@Override
					public void generateCase(int key, Label end) {
						HandlerMethod method = handlerClass.handlers[key];
						boolean isStatic = (accessFlags[key] & ACC_STATIC) != 0;
						if (!isStatic) {
							mv.loadThis();
						}
						if (method.isOnExit()) {
							mv.loadArg(ARG_RETVAL);
						}

						mv.loadArg(ARG_INSTANCE);
						mv.loadArg(ARG_ARGS);

						if (isStatic) {
							mv.invokeStatic(handlerClass.type, method.method);
						} else {
							mv.invokeVirtual(handlerClass.type, method.method);
						}
						mv.returnValue();
					}
				});
		mv.visitMaxs(0, 0);
	}

	private static int[] upto(int length) {
		int[] result = new int[length];
		for (int i = 0; i < result.length; i++) {
			result[i] = i;
		}
		return result;
	}

	private final static int ARG_RETVAL = 3;
	private final static int ARG_ARGS = 2;
	private final static int ARG_INSTANCE = 1;
	private final static int ARG_METHOD_ID = 0;

}
