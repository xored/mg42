package com.xored.mg42.agent;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class MethodUtils {

	public static boolean matches(Method method, String name, String desc) {
		if (!method.getName().equals(name)) {
			return false;
		}
		Type type = Type.getMethodType(desc);
		Type[] argTypes = type.getArgumentTypes();

		if (argTypes.length != method.getArgumentTypes().length) {
			return false;
		}
		for (int i = 0; i < method.getArgumentTypes().length; i++) {
			if (!argTypes[i].equals(method.getArgumentTypes()[i])) {
				return false;
			}
		}
		return true;
	}

}
