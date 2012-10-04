package com.xored.mg42.agent;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
		generateMethodProxy();
		super.visitEnd();
	}

	private void generateMethodProxy() {
		MethodVisitor mv = cv.visitMethod(ACC_PUBLIC,
				MG42Runtime.methodProxyTGName,
				MG42Runtime.methodProxyTGSignature, null, null);
		mv.visitCode();
		Label labelmetodStart = new Label();
		mv.visitLabel(labelmetodStart);

		// === Method body ===
		// load tracer argument and generate switch statement
		mv.visitVarInsn(ILOAD, 1);
		Label switchLabel = new Label();
		int[] caseValues = new int[handlerClass.tracers.length];
		Label[] caseLabels = new Label[handlerClass.tracers.length];
		for (int i = 0; i < handlerClass.tracers.length; i++) {
			caseValues[i] = handlerClass.tracers[i].methodId;
			caseLabels[i] = new Label();
		}
		mv.visitLookupSwitchInsn(switchLabel, caseValues, caseLabels);

		// generate switch cases
		for (int i = 0; i < caseValues.length; i++) {
			mv.visitLabel(caseLabels[i]);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			// Load arguments, for exit point first argument is return value
			String handlerSignature;
			if (Config.ON_EXIT_POINT.equals(handlerClass.tracers[i].point)) {
				mv.visitVarInsn(ALOAD, 4);
				handlerSignature = MG42Runtime.pointExitSignature;
			} else {
				handlerSignature = MG42Runtime.pointEnterSignature;
			}
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 3);
			// Invoke handler
			mv.visitMethodInsn(INVOKESTATIC,
					handlerClass.type.getInternalName(),
					handlerClass.tracers[i].handler, handlerSignature);
			mv.visitInsn(ARETURN);
		}

		mv.visitLabel(switchLabel);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
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

		mv.visitMaxs(2, 5);
		mv.visitEnd();
	}

}
