package com.nlteck.table.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.h2.util.New;

import com.csvreader.CsvReader;
import com.nlteck.model.CalculatePlanDotDO;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;


public  class CSVReport {

	
	/**
	 * 
	 * @param path
	 * @param titles
	 * @param propertys
	 * @param list
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	 public static <T> String exportCsv(String path, String[] titles, String[] propertys, List<T> list) 
			 throws IOException, IllegalArgumentException, IllegalAccessException{
		 
		  File file = new File(path);
		  if(!file.exists()) {
			  file.createNewFile();
		  }
		  //构建输出流，同时指定编码
		  OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), "GBK");
		   
		  //csv文件是逗号分隔，除第一个外，每次写入一个单元格数据后需要输入逗号
		  for(String title : titles){
		   ow.write(title);
		   ow.write(",");
		  }
		  //写完文件头后换行
		  ow.write("\r\n");
		  
		  //写内容
		  for(Object obj : list){
			  
			   //利用反射获取所有字段
			   Field[] fields = obj.getClass().getDeclaredFields();
			   for(String property : propertys){
			    for(Field field : fields){
			     //设置字段可见性
			     field.setAccessible(true); 
			     if(property.equals(field.getName())){
			      ow.write(field.get(obj) == null ? "" : field.get(obj).toString());
			      ow.write(",");
			      continue;
			     }
			    }
			   }
			   //写完一行换行
			   ow.write("\r\n");
		  }
		  
		  ow.flush();
		  ow.close();
		  
		  return "0";
		 }
	 
	 
	 
/*       public static void main(String[] args) {
	
          String[] titles = new String[]{"ID","姓名"};
		  String[] propertys = new String[]{"id","name"};
		  List<User> list = new ArrayList<User>();
		  User user;
		  user = new User();
		  user.setId(1L);
		  user.setName("张三");
		  list.add(user);
		  user = new User();
		  user.setId(2L);
		  user.setName("李四");
		  list.add(user);
		  CsvUtil.getInstance().exportCsv(titles,propertys, list);
      }*/
	 
	 
	 public static Map<Integer, String[]> importCSV(String path,String[] header){
		 
	        File inFile = new File(path); // 读取的CSV文件
	        String inString = "";
	        
	        Map<Integer,  String[]> map = new HashMap<>();
	        
	        try {
	            BufferedReader reader = new BufferedReader(new FileReader(inFile));
	            CsvReader creader = new CsvReader(reader, ',');
	            int count = 0;
	            while(creader.readRecord()){
	                inString = creader.getRawRecord();//读取一行数据

	                if(inString.length() > 0 && inString.substring(inString.length() - 1).equals(",")) {
	                	inString = inString.substring(0, inString.length() - 2);
	                }
	                if(count++ > 0){
	                	map.put(count, inString.split(","));
	                }
	                
	            }
	            
	          }catch (Exception e) {
				// TODO: handle exception
			}
	        
	        return map;
	 }
	 
	
/*	 public static void main(String[] args) {


		 Map<Integer, String[]>  map = CSVReport.importCSV("D:\\test.csv", new String[]{"序号","模式","极性","计量点"});
		 
		 System.out.println("==========export success end===========");
		 
		 for(Integer index : map.keySet()) {
			 System.out.println(" index:"+index+", data row :"+map.get(index)[0]+""+map.get(index)[1]+""+map.get(index)[2]+""+map.get(index)[3]);
		 }
	 }*/
	 
	 


}
