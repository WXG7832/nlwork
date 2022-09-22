package com.nlteck.parts.uiComponent;

import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * 设备树数据模型
 * 
 * @author Administrator
 *
 */
public class TreeNodeDataProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List) {

			List list = (List) inputElement;
			return list.toArray();
		} else {

			return new Object[0]; // 生成一个空数组
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		TreeNode node = (TreeNode) parentElement;
		if (node.getChildren() == null)
			return null;
		return node.getChildren().toArray();
	}

	@Override
	public Object getParent(Object element) {

		TreeNode node = (TreeNode) element;
		return node.getParent();

	}

	@Override
	public boolean hasChildren(Object element) {

		TreeNode node = (TreeNode) element;
		return node.getChildren() != null && node.getChildren().size() > 0;
	}

}
