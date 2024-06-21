package com.base;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

public class LogUtil {
     
	  private static Map<String,Logger> loggerMap = new HashMap<>();
	
	
	  public static Logger createLog(String path) throws Exception{
		  
		  
		  if(!loggerMap.containsKey(path)) {
		  
		    Logger logger = Logger.getLogger(path);
		    //file log;
		    PatternLayout layout=new PatternLayout();
		    String format="%d{yyyy-MM-dd HH:mm:ss.SSS}  %p -%m%n";
		    layout.setConversionPattern(format);
		    //create appender;
		    RollingFileAppender appenderFile =null ;
		    ConsoleAppender  appenderConsole = null;
		    
		    appenderFile=new RollingFileAppender(layout,path);
		    appenderConsole = new ConsoleAppender(layout);
		   
		    appenderConsole.setThreshold(Level.DEBUG); //µ±«∞œ‘ æ
		    appenderFile.setMaxBackupIndex(20);
		    appenderFile.setThreshold(Level.INFO);
		    logger.setAdditivity(false);
		    
		    logger.addAppender(appenderFile);
		    logger.addAppender(appenderConsole);
		    
		    loggerMap.put(path, logger);
		   
		    
		    return logger;
		  } else {
			  
			  
			  return loggerMap.get(path);
		  }
		  
	  }
	  
	 
	
	
}
