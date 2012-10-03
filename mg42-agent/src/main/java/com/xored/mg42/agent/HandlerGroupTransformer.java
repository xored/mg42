package com.xored.mg42.agent;

import org.objectweb.asm.MethodVisitor;

/**
 * In the beginning of given entry point adds a code like this (for each handler
 * group); <code>
 * Tracer.addGroup(0, new MyTracerGroup1());
 * Tracer.addGroup(1, new MyTracerGroup2());
 * </code>
 * 
 */
public class HandlerGroupTransformer extends ClassTransformer {
	private final HandlerGroup group;

	public HandlerGroupTransformer(HandlerGroup group) {
		this.group = group;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);
		if (group.isEntryMethod(name, desc)) {
			return new EntryPointMethodTransformer(group, mv, access, name,
					desc);
		}
		return mv;
	}

}
