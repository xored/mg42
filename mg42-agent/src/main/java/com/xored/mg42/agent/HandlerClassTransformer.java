package com.xored.mg42.agent;

import static com.google.common.collect.ObjectArrays.concat;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class HandlerClassTransformer extends ClassTransformer implements
		MG42Runtime {

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
		this.accessFlags = new int[handlerClass.handlers.length];
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName,
				concat(interfaces, interfaceTracerGroup));
	}

	private int[] accessFlags;

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		HandlerMethod method = handlerClass.find(name);
		if (method != null) {
			accessFlags[method.id] = access;
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
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
		//
		// GeneratorAdapter mv = new GeneratorAdapter(ACC_PUBLIC,
		// methodMG42Proxy,
		// null, null, cv);
		mv.visitCode();
		Label labelmetodStart = new Label();
		mv.visitLabel(labelmetodStart);

		// === Method body ===
		// load tracer argument and generate switch statement
		mv.visitVarInsn(ILOAD, 1);
		Label switchLabel = new Label();
		int[] caseValues = new int[handlerClass.handlers.length];
		Label[] caseLabels = new Label[handlerClass.handlers.length];
		for (int i = 0; i < handlerClass.handlers.length; i++) {
			caseValues[i] = handlerClass.handlers[i].id;
			caseLabels[i] = new Label();
		}
		mv.visitLookupSwitchInsn(switchLabel, caseValues, caseLabels);

		// generate switch cases
		for (int i = 0; i < caseValues.length; i++) {
			HandlerMethod current = handlerClass.handlers[i];
			boolean isStatic = (accessFlags[i] & ACC_STATIC) != 0;
			mv.visitLabel(caseLabels[i]);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			// Load arguments, for exit point first argument is return value
			String handlerSignature;
			if (!isStatic) {
				mv.visitVarInsn(ALOAD, 0); // load 'this' on stack
			}
			if (Config.ON_EXIT_POINT.equals(current.point)) {
				mv.visitVarInsn(ALOAD, 4);
				handlerSignature = MG42Runtime.pointExitSignature;
			} else {
				handlerSignature = MG42Runtime.pointEnterSignature;
			}
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 3);
			// Invoke handler
			int instruction = isStatic ? INVOKESTATIC : INVOKEVIRTUAL;
			mv.visitMethodInsn(instruction,
					handlerClass.type.getInternalName(), current.methodName,
					handlerSignature);
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
