package com.xored.mg42.agent;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class HandlerClassTransformer extends ClassTransformer {

	private final static String objDesc = "Ljava/lang/Object;";
	private final static String objArrayDesc = "[Ljava/lang/Object;";
	private final static String intDesc = "I";

	private final static String tracerArgName = "tracer";
	private final static String instanceArgName = "instance";
	private final static String argsArgName = "args";
	private final static String retValArgName = "returnValue";
	private final static String thisLocalVar = "this";

	private final HandlerClass handlerClass;

	public HandlerClassTransformer(HandlerClass handlerClass) {
		this.handlerClass = handlerClass;
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {

		String[] modifiedInterfaces = new String[interfaces.length + 1];
		for (int i = 0; i < interfaces.length; i++) {
			modifiedInterfaces[i] = interfaces[i];
		}
		modifiedInterfaces[modifiedInterfaces.length - 1] = MG42Runtime.interfaceTracerGroup;

		super.visit(version, access, name, signature, superName,
				modifiedInterfaces);
	}

	@Override
	public void visitEnd() {
		generateMethodEnter();
		generateMethodExit();
		super.visitEnd();
	}

	private void generateMethodEnter() {
		MethodVisitor mv = cv.visitMethod(ACC_PUBLIC,
				MG42Runtime.methodEnterTGName,
				MG42Runtime.methodEnterTGSignature, null, null);
		mv.visitCode();

		Label labelmetodStart = new Label();
		mv.visitLabel(labelmetodStart);
		// Method body
		mv.visitInsn(ACONST_NULL);
		mv.visitInsn(ARETURN);

		Label labelMethodEnd = new Label();
		mv.visitLabel(labelMethodEnd);

		mv.visitLocalVariable(thisLocalVar, handlerClass.type.getDescriptor(),
				null, labelmetodStart, labelMethodEnd, 0);
		mv.visitLocalVariable(tracerArgName, intDesc, null, labelmetodStart,
				labelMethodEnd, 1);
		mv.visitLocalVariable(instanceArgName, objDesc, null, labelmetodStart,
				labelMethodEnd, 2);
		mv.visitLocalVariable(argsArgName, objArrayDesc, null, labelmetodStart,
				labelMethodEnd, 3);

		mv.visitMaxs(1, 4);
		mv.visitEnd();
	}

	private void generateMethodExit() {
		MethodVisitor mv = cv.visitMethod(ACC_PUBLIC,
				MG42Runtime.methodExitTGName,
				MG42Runtime.methodExitTGSignature, null, null);
		mv.visitCode();
		Label labelmetodStart = new Label();
		mv.visitLabel(labelmetodStart);
		// Method body
		mv.visitInsn(ACONST_NULL);
		mv.visitInsn(ARETURN);

		Label labelMethodEnd = new Label();
		mv.visitLabel(labelMethodEnd);

		mv.visitLocalVariable(thisLocalVar, handlerClass.type.getDescriptor(),
				null, labelmetodStart, labelMethodEnd, 0);
		mv.visitLocalVariable(tracerArgName, intDesc, null, labelmetodStart,
				labelMethodEnd, 1);
		mv.visitLocalVariable(instanceArgName, objDesc, null, labelmetodStart,
				labelMethodEnd, 2);
		mv.visitLocalVariable(argsArgName, objArrayDesc, null, labelmetodStart,
				labelMethodEnd, 3);
		mv.visitLocalVariable(retValArgName, objDesc, null, labelmetodStart,
				labelMethodEnd, 4);

		mv.visitMaxs(1, 5);
		mv.visitEnd();
	}

}
