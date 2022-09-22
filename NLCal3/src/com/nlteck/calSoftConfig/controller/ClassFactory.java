package com.nlteck.calSoftConfig.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

/**
 * @description 类工厂
 * @author zemin_zhu
 * @dateTime Jul 2, 2022 2:35:15 PM
 */
public class ClassFactory {

    private static ClassFactory instance;
    protected ClassPool classPool;

    public ClassFactory() {
	classPool = ClassPool.getDefault();
    }

    public static ClassFactory getInstance() {
	if (instance == null) {
	    instance = new ClassFactory();
	}
	return instance;
    }

    /**
     * @description 构造新的类
     * @author zemin_zhu
     * @dateTime Jul 2, 2022 2:15:32 PM
     */
    public Class<?> newClass(String className, Class<?> superClass, Field[] newFieldArr) throws Exception {
	CtClass ctClass = classPool.makeClass(className);
	if (superClass != null) {
	    CtClass superCtClass = classPool.get(superClass.getName());
	    ctClass.setSuperclass(superCtClass);
	    CtField ctField = new CtField(superCtClass, superClass.getSimpleName(), ctClass);
	    ctClass.addField(ctField);
	}
	if (newFieldArr != null) {
	    for (Field field : newFieldArr) {
		CtField ctField = newCtField(ctClass, field);
		if (ctField != null) {
		    ctClass.addField(ctField);
		}
	    }
	}
	return ctClass.toClass();
    }

    /**
     * @description 新的成员变量
     * @author zemin_zhu
     * @dateTime Jul 2, 2022 2:16:00 PM
     */
    protected CtField newCtField(CtClass ctClass, Field field) throws Exception {
	CtClass ctFieldType = null;
	if (field.getType() == int.class) {
	    ctFieldType = CtClass.intType;
	} else if (field.getType() == double.class) {
	    ctFieldType = CtClass.doubleType;
	} else if (field.getType() == boolean.class) {
	    ctFieldType = CtClass.booleanType;
	} else {
	    ctFieldType = classPool.get(field.getType().getName());
	}
	CtField ctField = null;
	if (ctFieldType != null) {
	    ctField = new CtField(ctFieldType, field.getName(), ctClass);
	}
	return ctField;
    }

    /**
     * @description 得到父类的field在内所有filed
     * @author zemin_zhu
     * @dateTime Mar 28, 2022 6:10:50 PM
     */
    public Field[] getAllFields(Class<?> clazz) {
	List<Field> result = new ArrayList<Field>();

	Class<?> i = clazz;
	while (i != null && i != Object.class) {
	    Collections.addAll(result, i.getDeclaredFields());
	    i = i.getSuperclass();
	}

	return result.toArray(new Field[] {});
    }

    /**
     * @description 将其他对象的成员变量值, 赋值给自身同名变量
     * @author zemin_zhu
     * @dateTime Mar 26, 2022 10:04:16 PM
     */
    public void copyVal(Object target, Object source) throws Exception {
	String sourceClassName = source.getClass().getSimpleName();
	Field[] targetFieldArr = getAllFields(target.getClass());
	Field[] sourceFieldArr = getAllFields(source.getClass());
	for (Field targetField : targetFieldArr) {
	    if (targetField.getModifiers() != 25) {
		targetField.setAccessible(true);
		String targetFieldName = targetField.getName();
		for (Field sourceField : sourceFieldArr) {
		    String sourceFieldName = sourceField.getName();
		    if (sourceFieldName.matches("^" + targetFieldName + "$")) {
			sourceField.setAccessible(true);
			Object sourceFieldValue = sourceField.get(source);
			targetField.set(target, sourceFieldValue);
			break;
		    }
		}
		if (sourceClassName.contains(targetFieldName)) {
		    targetField.set(target, source);
		}
	    }
	}
    }

}
