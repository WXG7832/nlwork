package com.nlteck.parts.uiComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备树列表结点
 * 
 * @author Administrator
 *
 */
public class TreeNode {

	private Object content; // 结点实际对象
	private String title; // 结点标题
	private TreeNode parent; // 父结点
	private List<TreeNode> children = new ArrayList<TreeNode>(); // 子结点集合
    
	
	public TreeNode(Object content , String title , TreeNode parent) {
		
		this.content = content;
		this.title   = title;
		this.parent  = parent;
		if(parent != null) {
		   parent.children.add(this);
		   
		}
		
	}
	
	public TreeNode findChildByContent(Object content) {
		
		for(TreeNode node : children) {
			
			if(node.getContent() == content) {
				
				return node;
			}
		}
		
		return null;
		
	}
	
	public void removeChild(Object content) {

		for (int n = 0; n < children.size(); n++) {

			if (children.get(n).content.equals(content)) {

				children.remove(n);
				break;
			}
		}
	}
	
	
	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public TreeNode getParent() {
		return parent;
	}


	public List<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}
	
	
	public TreeNode addChild(Object content, String label) {

		TreeNode node = new TreeNode(content, label, this);
		//children.add(node);
		return node;
	}

	@Override
	public String toString() {
		return "TreeNode [content=" + content + ", title=" + title + ", children=" + children
				+ "]";
	}
	
	

}
