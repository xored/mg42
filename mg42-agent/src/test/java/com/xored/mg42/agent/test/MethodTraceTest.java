package com.xored.mg42.agent.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.xored.mg42.agent.MethodTrace;

public class MethodTraceTest {
	private static final String[] descriptors = { "()V", // void
			"(Ljava/lang/String;)V",// String
			"(I)V", // int
			"([Ljava/lang/String;)V", // String[]
			"(Ljava/lang/String;I)V" // String,int
	};

	private static final MethodTrace[] traces = { forArgs(), // void
			forArgs("java.lang.String"), // String
			forArgs("int"), // int
			forArgs("java.lang.String[]"), // String[]
			forArgs("java.lang.String", "int") // String,int
	};

	private static MethodTrace forArgs(String... args) {
		return new MethodTrace("process", args, null, null);
	}

	@Test
	public void test() {
		for (int i = 0; i < descriptors.length; i++) {
			for (int j = 0; j < traces.length; j++) {
				String desc = descriptors[i];
				MethodTrace trace = traces[j];
				boolean matches = trace.matches("process", desc);
				assertTrue(String.format("desc - %s, trace - %s", desc, trace),
						i == j ? matches : !matches);
			}
		}
	}
}
