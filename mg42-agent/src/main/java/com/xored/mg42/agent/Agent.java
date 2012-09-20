package com.xored.mg42.agent;

import java.lang.instrument.Instrumentation;

public class Agent {
	public static void premain(String args, Instrumentation inst)
			throws Exception {
		inst.addTransformer(new Transformer(Config.fromArgs(args)));
	}
}
