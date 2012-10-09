package com.xored.mg42.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.JSRInlinerAdapter;

public class TracerConfigClassTransformer extends ClassTransformer {

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);
		if (MethodUtils.matches(MG42Runtime.methodGetOutputArg, name, desc)) {
			return new JSRInlinerAdapter(new GetOutputMethodTransformer(mv,
					access, name, desc), access, name, desc, signature,
					exceptions);
		} else {
			return new JSRInlinerAdapter(mv, access, name, desc, signature,
					exceptions);
		}
	}

	class GetOutputMethodTransformer extends AdviceAdapter {

		GetOutputMethodTransformer(MethodVisitor mv, int access, String name,
				String desc) {
			super(ASM4, mv, access, name, desc);
		}

		@Override
		public void visitCode() {
			if (Agent.getConfig().getOutput() != null) {
				push(Agent.getConfig().getOutput().toString());
				returnValue();
			} else {
				super.visitCode();
			}
		}
	}
}
