package com.xored.mg42.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import com.google.common.io.ByteStreams;

public class Agent {
	private static Config config;

	public static Config getConfig() {
		return config;
	}

	public static void premain(String args, Instrumentation inst)
			throws Exception {
		inst.appendToBootstrapClassLoaderSearch(extractRuntimeJar());
		config = Config.fromArgs(args);
		inst.addTransformer(new Transformer(config), true);
		Class<?>[] toRetransform = classesToRetransform(config);
		if (toRetransform.length > 0) {
			inst.retransformClasses(toRetransform);
		}
	}

	public static Class<?>[] classesToRetransform(Config config)
			throws ClassNotFoundException {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		List<Class<?>> result = new ArrayList<Class<?>>();
		for (SourceClass sc : config.sources.classes.values()) {
			String classname = sc.type.getClassName();
			if (classname.startsWith("java")) {
				result.add(cl.loadClass(classname));
			}
		}
		return result.toArray(new Class<?>[result.size()]);
	}

	public static void agentmain(String args, Instrumentation inst)
			throws Exception {
		premain(args, inst);
	}

	private static final String RUNTIME_FILE = "mg42-runtime-0.0.1-SNAPSHOT-jar-with-dependencies.jar";

	private static JarFile extractRuntimeJar() throws IOException {
		InputStream input = Agent.class.getResourceAsStream(String.format(
				"/%s", RUNTIME_FILE));
		File outFile = File.createTempFile("mg42runtime", "jar");
		outFile.deleteOnExit();
		FileOutputStream out = new FileOutputStream(outFile);
		ByteStreams.copy(input, out);
		out.close();
		return new JarFile(outFile);
	}
}
