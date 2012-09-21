package com.xored.mg42.agent;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

final class MethodTransformer extends AdviceAdapter implements MG42Runtime {
	private final MethodTrace trace;
	private final String methodHandle;

	private final Label startFinally = new Label();

	MethodTransformer(ClassTrace ct, MethodTrace trace, MethodVisitor mv,
			int access, String name, String desc) {
		super(ASM4, mv, access, name, desc);
		this.trace = trace;
		this.methodHandle = trace.toHandle(ct.name);
	}

	@Override
	public void visitCode() {
		super.visitCode();
		if (trace.onExit != null) {
			mv.visitLabel(startFinally);
		}
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		if (trace.onExit != null) {
			Label endFinally = new Label();
			mv.visitTryCatchBlock(startFinally, endFinally, endFinally, null);
			mv.visitLabel(endFinally);
			onFinally(ATHROW);
			mv.visitInsn(ATHROW);
		}
		super.visitMaxs(maxStack, maxLocals);
	}

	@Override
	protected void onMethodExit(int opcode) {
		if (opcode != ATHROW && trace.onExit != null) {
			onFinally(opcode);
		}
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

	private void onFinally(int opcode) {
		if (opcode == ATHROW || opcode == ARETURN) {
			dup(); // make a copy of exception object
		} else {
			push((Type) null);
		}
		invokeStatic(Tracer, getDefault);
		swap(); // swap exception object with tracer instance
		push(methodHandle);
		loadThis();
		loadArgArray();
		invokeVirtual(Tracer, methodEnd);
	}

}