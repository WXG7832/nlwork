package com.nlteck.service;

import java.util.List;

import com.nlteck.firmware.LogicBoard;

/**
 * 流程管理器
 * @author Administrator
 *
 */
public class ProcedureManager {
    
	  
	private List<LogicBoard> logicBoards;
	private boolean syncMode;
	
	
	public ProcedureManager(List<LogicBoard> logicBoards , boolean syncMode) {
		
		this.syncMode = syncMode;
		this.logicBoards = logicBoards;
	}
	
	
}
