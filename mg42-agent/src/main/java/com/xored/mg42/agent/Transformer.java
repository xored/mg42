package com.xored.mg42.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
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
			ClassTransformer ct = null;
			if (config.handlers.isGroupClass(className)) {
				System.out.println(className + " is handler group");
				ct = new HandlerGroupTransformer(
						config.handlers.findGroup(className));
			}

			if (config.handlers.isHandlerClass(className)) {
				System.out.println(className + " is handler class");
				ct = new HandlerClassTransformer(
						config.handlers.findHandlerClass(className));
			}

			if (config.sources.isSourceClass(className)) {
				System.out.println(className + " is source class");
				ct = new SourceClassTransformer(
						config.sources.findSource(className));
			}

			if (ct == null) {
				return classfileBuffer;
			}

			byte[] result = applyTransform(classfileBuffer, ct);
			dumpResult(result, className);
			return result;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new IllegalClassFormatException();
		}
	}

	private static final boolean DEBUG = false;

	private static final File baseDir = new File(
			System.getProperty("user.home"), "Temp/mg42");

	@SuppressWarnings("unused")
	private static void dumpResult(byte[] result, String internalName) {
		if (!DEBUG) {
			return;
		}
		internalName = internalName + ".class";
		File outFile = new File(baseDir, internalName);
		outFile.getParentFile().mkdirs();
		try {
			Files.write(result, outFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final byte[] applyTransform(byte[] classfile,
			ClassTransformer transformer) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassReader cr = new ClassReader(classfile);
		transformer.setWriter(cw);
		cr.accept(transformer, ClassReader.EXPAND_FRAMES);
		return cw.toByteArray();
	}
}
