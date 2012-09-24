package com.xored.mg42.agent;


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

}
