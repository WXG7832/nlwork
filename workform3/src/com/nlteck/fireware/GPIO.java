package com.nlteck.fireware;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 设备GPIO口信号量
 * @author Administrator
 *
 */
public class GPIO {
    
	
	
	private int pin;
	private final static String DIR = "/sys/class/gpio";
	
	public GPIO(int pin) {	
		this.pin = pin;		
	}
	
	/**
	 * 写入此编号
	 * @throws IOException 
	 */
	public boolean export() throws IOException {
		
		//检测是否已经导出该引脚标号
		File file = new File(DIR + "/gpio" + pin);
		if(file.exists()) {
			
			return true;
		}
		
		FileWriter fw = new FileWriter(DIR + "/export");
		fw.write(pin + "\n");
		fw.close();
		System.out.println("export " + pin + " success!");
		return true;
	}
	
	//删除指定编号的IO口
	public void unexport() throws IOException {
		
		FileWriter fw = new FileWriter(DIR + "/unexport");
		fw.write(pin + "\n");
		fw.close();
		System.out.println("unexport " + pin + " success!");
	}
	
	//设置引脚为输出
	public void setOutput() throws IOException {
		
		FileWriter fw = new FileWriter(DIR + "/gpio" + pin + "/direction");
		fw.write("out\n");
		fw.close();
		
	}
	
	//设置引脚为输入
	public void setInput() throws IOException {
		
		FileWriter fw = new FileWriter(DIR + "/gpio" + pin + "/direction");
		fw.write("in\n");
		fw.close();
	}
	/**
	 * 
	 * @return 1为高电平 0为低电平
	 * @throws IOException 
	 */
	public int readValue() throws IOException {
		
		FileReader fr = new FileReader(DIR + "/gpio" + pin + "/value");
		int val = fr.read();
		fr.close();
		
		return val >= 48 ? val - 48 : val;
	}
	/**
	 * 
	 * @param value  1高电平  0低电平
	 * @throws IOException
	 */
	public void writeValue(int value) throws IOException {
		
		FileWriter fw = new FileWriter(DIR + "/gpio" + pin + "/value");
		fw.write(value + "\n");
		fw.close();
	}
}
