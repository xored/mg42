package com.xored.mg42.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class ClassTransformer extends ClassVisitor implements Opcodes {
	public ClassTransformer() {
		super(ASM4);
	}

	public void setWriter(ClassVisitor cv) {
		this.cv = cv;
	}
}
