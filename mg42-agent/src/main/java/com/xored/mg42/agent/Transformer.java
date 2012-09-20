package com.xored.mg42.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Transformer implements ClassFileTransformer, Opcodes {
	private final Config config;

	public Transformer(Config config) {
		this.config = config;
	}

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String configName = toConfigName(className);
		if (!config.getTracers().containsKey(configName)) {
			return classfileBuffer;
		}

		final ClassTrace classTrace = config.getTracers().get(configName);

		ClassWriter cw = new ClassWriter(0);
		ClassReader cr = new ClassReader(classfileBuffer);
		ClassVisitor cv = new ClassVisitor(ASM4, cw) {
			@Override
			public MethodVisitor visitMethod(int access, String name,
					String desc, String signature, String[] exceptions) {
				MethodTrace methodTrace = classTrace.find(name, desc);
				if (methodTrace != null) {
					System.out.println(String.format("About to apply %s",
							methodTrace));
				}
				return super.visitMethod(access, name, desc, signature,
						exceptions);
			}
		};
		cr.accept(cv, 0);

		return cw.toByteArray();
	}

	private static String toConfigName(String className) {
		return className.replace('/', '.');
	}
}
