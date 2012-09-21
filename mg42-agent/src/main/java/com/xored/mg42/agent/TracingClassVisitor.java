package com.xored.mg42.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

final class TracingClassVisitor extends ClassVisitor {
	private final ClassTrace classTrace;

	TracingClassVisitor(ClassVisitor cv, ClassTrace classTrace) {
		super(Transformer.ASM4, cv);
		this.classTrace = classTrace;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);

		MethodTrace methodTrace = classTrace.find(name, desc);
		if (methodTrace == null) {
			return mv;
		}

		return new MethodTransformer(classTrace, methodTrace, mv, access, name,
				desc);
	}
}