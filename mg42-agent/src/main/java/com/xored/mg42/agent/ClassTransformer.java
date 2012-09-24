package com.xored.mg42.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class ClassTransformer extends ClassVisitor implements Opcodes {
	public ClassTransformer() {
		super(ASM4);
	}

	public void setWriter(ClassWriter cw) {
		this.cv = cw;
	}
}
