package com.nlteck.calSoftConfig.view;

import org.eclipse.swt.widgets.Composite;

import com.nlteck.calSoftConfig.viewModel.ViewModel;

/**
 * @description 使用视图模型的控件基类
 * @author zemin_zhu
 * @dateTime Jun 29, 2022 11:53:17 AM
 */
public abstract class ViewModelComposite extends Composite {

    public ViewModel viewModel;
    public Object[] args;

    public ViewModelComposite(Composite parent, int style, Object[] args) {
	super(parent, style);
	this.args = args;
	viewModel = createViewModel(this);
	createControl(parent);
	viewModel.createSubViewModel();
	createSubViewModelControl(parent);
	viewModel.refreshControl();
    }

    protected abstract void createControl(Composite parent);

    /**
     * @description 实例化子视图模型绑定的控件
     * @author zemin_zhu
     * @dateTime Jul 13, 2022 2:11:18 PM
     */
    protected void createSubViewModelControl(Composite parent) {

    }

    protected abstract ViewModel createViewModel(ViewModelComposite composite);
}
