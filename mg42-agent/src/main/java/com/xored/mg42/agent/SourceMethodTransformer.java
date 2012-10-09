package com.xored.mg42.agent;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

final class SourceMethodTransformer extends AdviceAdapter implements
		MG42Runtime {

	private final SourceMethod sourceMethod;
	private final Method method;
	private final Label startFinally = new Label();

	SourceMethodTransformer(SourceMethod sourceMethod, MethodVisitor mv,
			int access, String name, String desc) {
		super(ASM4, mv, access, name, desc);
		this.sourceMethod = sourceMethod;
		this.method = new Method(name, desc);
	}

	@Override
	public void visitCode() {
		super.visitCode();
		if (getOnExit() != null) {
			mv.visitLabel(startFinally);
		}
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		if (getOnExit() != null) {
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
		if (opcode != ATHROW && getOnExit() != null) {
			onFinally(opcode);
		}
	}

	protected void onMethodEnter() {
		super.onMethodEnter();
		if (getOnEnter() == null) {
			return;
		}
		push(getOnEnter().parent.classId);
		push(getOnEnter().id);
		loadThis();
		loadArgArray();
		invokeStatic(Tracer, methodStart);
	}

	private void onFinally(int opcode) {
		if (opcode == ATHROW || opcode == ARETURN) {
			dup(); // make a copy of exception object
		} else if (opcode == IRETURN) {
			box(method.getReturnType());
		}
		push(getOnExit().parent.classId);
		push(getOnExit().id);
		loadThis();
		loadArgArray();
		invokeStatic(Tracer, methodEnd);
	}

	private HandlerMethod getOnEnter() {
		return sourceMethod.points.get(Config.ON_ENTER_POINT);
	}

	private HandlerMethod getOnExit() {
		return sourceMethod.points.get(Config.ON_EXIT_POINT);
	}
}