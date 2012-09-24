package com.xored.mg42.agent;

import java.util.Map;

public class SourceClasses {
	public SourceClasses(Map<String, SourceClass> classes) {
		this.classes = classes;
	}

	public final Map<String, SourceClass> classes;

	public boolean isSourceClass(String internalName) {
		return classes.containsKey(internalName);
	}

	public SourceClass findSource(String internalName) {
		return classes.get(internalName);
	}
}
