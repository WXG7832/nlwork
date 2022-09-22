package com.nlteck;

import java.util.List;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuImpl;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IWindowCloseHandler;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.EventHandler;

import com.nlteck.firmware.WorkBench;
import com.nlteck.model.BaseCfg.TestMode;

import nlcal.NlteckCalEnvrionment;

@SuppressWarnings("restriction")
public class E4LifeCycle {

	public static EPartService partService;
	public static EModelService modelService;
	public static MApplication application;
	
	public static final String TITLE = "纽联动力平台校准测试软件v3.0.10.20220611";

	@PostContextCreate
	void postContextCreate(IEclipseContext workbenchContext) {
	}

	@PreSave
	void preSave(IEclipseContext workbenchContext) {
	}

	@ProcessAdditions
	void processAdditions(IEventBroker eventBroker, MApplication app, EModelService modelService,
			IEclipseContext context, IApplicationContext appContext, EPartService partService) {
		System.out.println("life cycle start");
		E4LifeCycle.partService = partService;
		E4LifeCycle.modelService = modelService;
		E4LifeCycle.application = app;
		MWindow window = (MWindow) (MWindow) modelService.find("NLCal.window.main", app);
		window.setLabel(TITLE + "(" + WorkBench.baseCfg.testMode + ")");
		eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new AppStartupCompleteEventHandler(window));
		appContext.applicationRunning();
	}

	@ProcessRemovals
	void processRemovals(IEclipseContext workbenchContext) {
	}

	public static void showPart(MPart part) {

		partService.showPart(part, PartState.ACTIVATE);
	}

	public static void hidePart(MPart part, boolean force) {

		partService.hidePart(part, force);
	}

	public static MPart createPart(String partId, String title, boolean visible) {

		MPart part = partService.createPart(partId);
		part.setLabel(title);
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		partService.showPart(part, visible ? PartState.VISIBLE : PartState.CREATE);
		return part;
	}

	public static MPart showOrCreatePart(String partId, String title) {
		MPart part = findPart(partId, null);
		if (part == null) {
			part = createPart(partId, title, true);
		} else {
			part.setLabel(title);
		}
		showPart(part);
		return part;
	}

	public static MPart findPart(String partId, Object content) {
		List<MPart> partlist = modelService.findElements(application, partId, MPart.class, null);
		for (MPart p : partlist) {
			if (content == null)
				return p;
			if (p.getObject() == content) {
				return p;
			}
		}
		return null;
	}

	private class AppStartupCompleteEventHandler implements EventHandler {
		private MWindow theWindow;

		AppStartupCompleteEventHandler(MWindow window) {
			theWindow = window;
		}

		@Override
		public void handleEvent(org.osgi.service.event.Event event) {
			theWindow.getContext().set(IWindowCloseHandler.class, new WindowCloseHandler());
			//Shell mainShell = (Shell) theWindow.getWidget();
			//mainShell.setMaximized(true);
			// 开发者专用菜单的隐藏
//			((MenuImpl) application.getChildren().get(0).getMainMenu().getChildren().get(0)).getChildren().get(0)
//					.setVisible(NlteckCalEnvrionment.isDevelopMode);
//			((MenuImpl) application.getChildren().get(0).getMainMenu().getChildren().get(0)).getChildren().get(3)
//					.setVisible(NlteckCalEnvrionment.isDevelopMode);
		}
	}

	private class WindowCloseHandler implements IWindowCloseHandler {
		@Override
		public boolean close(MWindow window) {
			if (!NlteckCalEnvrionment.isDevelopMode) {
				return MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "退出确认", "确定要退出系统吗?");
			} else {
				return true;
			}
		}
	}
}
