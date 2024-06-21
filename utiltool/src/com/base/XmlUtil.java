package com.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlUtil {

	/**
	 * 载入xml文件到内存
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static Document loadXml(String path) throws Exception {

		File file = new File(path);
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		return doc;

	}

	/**
	 * 写入或生成xml文件
	 * 
	 * @param path
	 * @param root
	 * @throws IOException
	 */
	public static void saveXml(String path, Document document) throws IOException {

		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");
		XMLWriter writer = new XMLWriter(new FileOutputStream(path), format);
		writer.write(document);
		writer.flush();
		writer.close();
	}
	
	

}
