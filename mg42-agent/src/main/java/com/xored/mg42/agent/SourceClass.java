package com.xored.mg42.agent;

import org.objectweb.asm.Type;

public class SourceClass {
	public SourceClass(String type, SourceMethod[] methods) {
		this.type = HandlerGroup.getTypeByQName(type);
		this.methods = methods;
	}

	public final Type type;
	public final SourceMethod[] methods;
}
