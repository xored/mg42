package com.xored.mg42.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.JSRInlinerAdapter;

public class SourceClassTransformer extends ClassTransformer {
	private final SourceClass sourceClass;

	public SourceClassTransformer(SourceClass sourceClass) {
		this.sourceClass = sourceClass;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);
		SourceMethod sourceMethod = sourceClass.find(name, desc);
		if (sourceMethod == null) {
			return new JSRInlinerAdapter(mv, access, name, desc, signature,
					exceptions);
		}

		return new JSRInlinerAdapter(new SourceMethodTransformer(sourceMethod,
				mv, access, name, desc), access, name, desc, signature,
				exceptions);
	}

}
