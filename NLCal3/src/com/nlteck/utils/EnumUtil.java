package com.nlteck.utils;

/**
 * 枚举处理类
 * @author：admin   
 * @Date：2021年1月28日 上午11:42:42   
 * @version
 */
public class EnumUtil {


	/**
	 * 根据名称获取枚举
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static <T extends Enum<T>> T getEnumByName(Class<T> clazz,String name){
	
		for(T entity : clazz.getEnumConstants()){
			if(entity.name().equals(name)) {
				return entity;
			}
		}	
		return null;
		
   }
}
