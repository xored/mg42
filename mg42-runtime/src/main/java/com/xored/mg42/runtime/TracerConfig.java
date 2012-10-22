package com.xored.mg42.runtime;

import java.net.URI;

public class TracerConfig {

	private static String getOutputArg() {
		return null;
	}

	public static URI getOutput() {
		return getOutputArg() != null ? URI.create(getOutputArg()) : null;
	}

	public static boolean getStartArg() {
		return true;
	}
}
