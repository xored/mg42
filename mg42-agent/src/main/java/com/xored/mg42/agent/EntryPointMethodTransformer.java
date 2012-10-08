package com.xored.mg42.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

public class EntryPointMethodTransformer extends AdviceAdapter implements
		MG42Runtime {

	private final HandlerGroup group;

	EntryPointMethodTransformer(HandlerGroup group, MethodVisitor mv,
			int access, String name, String desc) {
		super(ASM4, mv, access, name, desc);
		this.group = group;
	}

	protected void onMethodEnter() {
		super.onMethodEnter();
		for (HandlerClass handlerClass : group.classes.values()) {
			// Generate line: Tracer.addGroup(classId, new HandlerClassType());
			push(handlerClass.classId);
			newInstance(handlerClass.type);
			dup();
			invokeConstructor(handlerClass.type, defaultConstructor);
			invokeStatic(Tracer, methodAddGroup);
		}
	}

}
