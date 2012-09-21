package com.xored.mg42.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

final class MethodTransformer extends AdviceAdapter implements MG42Runtime {
	private final MethodTrace trace;
	private final String methodHandle;

	MethodTransformer(ClassTrace ct, MethodTrace trace, MethodVisitor mv,
			int access, String name, String desc) {
		super(ASM4, mv, access, name, desc);
		this.trace = trace;
		this.methodHandle = trace.toHandle(ct.name);
	}

	@Override
	protected void onMethodExit(int opcode) {
		super.onMethodExit(opcode);
	}

	protected void onMethodEnter() {
		super.onMethodEnter();
		if (trace.onEnter == null) {
			return;
		}
		invokeStatic(Tracer, getDefault);
		push(methodHandle);
		loadThis();
		loadArgArray();
		invokeVirtual(Tracer, methodStart);
	}
}