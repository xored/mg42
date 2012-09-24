package com.xored.mg42.agent;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import com.google.common.io.Files;

public class Transformer implements ClassFileTransformer, Opcodes {
	private final Config config;

	public Transformer(Config config) {
		this.config = config;
	}

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		try {
			System.out.println(className);
			String configName = toConfigName(className);
			if (!config.getTracers().containsKey(configName)) {
				return classfileBuffer;
			}

			final ClassTrace classTrace = config.getTracers().get(configName);

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			ClassReader cr = new ClassReader(classfileBuffer);
			ClassVisitor cv = new TracingClassVisitor(cw, classTrace);
			cr.accept(cv, ClassReader.EXPAND_FRAMES);
			Files.write(classfileBuffer, new File(
					"/Users/ivaninozemtsev/Temp/CommandProcessor.java"));
			return cw.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new IllegalClassFormatException();
		}
	}

	private static String toConfigName(String className) {
		return className.replace('/', '.');
	}
}
