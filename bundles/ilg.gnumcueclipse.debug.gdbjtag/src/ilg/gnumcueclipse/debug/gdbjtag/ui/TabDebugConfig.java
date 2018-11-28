package ilg.gnumcueclipse.debug.gdbjtag.ui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.debug.gdbjtag.Activator;
import ilg.gnumcueclipse.debug.gdbjtag.ConfigurationAttributes;
import ilg.gnumcueclipse.debug.gdbjtag.DebugUtils;
import ilg.gnumcueclipse.debug.gdbjtag.preferences.GlobalHwBreakpointLimitPage;
import ilg.gnumcueclipse.debug.gdbjtag.preferences.PersistentPreferences;
import ilg.gnumcueclipse.debug.gdbjtag.preferences.WorkspaceHwBreakpointLimitPage;
import ilg.gnumcueclipse.debug.gdbjtag.ui.properties.ProjectHwBreakpointLimitPage;

public class TabDebugConfig extends AbstractLaunchConfigurationTab {

	private static final String TAB_NAME = "Config";
	private static final String TAB_ID = Activator.PLUGIN_ID + ".ui.configtab";

	private Text fRegisterListPathExpr;
	private Button fRegisterListBrowseButton;
	private Button fRegisterListVariablesButton;
	private Text fRegisterListActualPath;
	private ILaunchConfiguration fConfiguration;
	private Text fHwBreakpointLimitText;
	private Link fHwBreakpointLimitExplanation;

	public TabDebugConfig() {
	}
	
	@Override
	public void createControl(Composite parent) {

		Composite comp = new Composite(parent, SWT.NONE);
		{
			setControl(comp);
			GridLayout layout = new GridLayout();
			comp.setLayout(layout);
		}

		createRegisterListControls(comp);
		createHwBreakpointControls(comp);		
	}

	private void createRegisterListControls(Composite comp) {
		Group group = new Group(comp, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
			group.setText(Messages.TabConfig_registerlist_group_text);
		}

		Composite groupComp = new Composite(group, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 5;
			layout.marginHeight = 0;
			groupComp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			groupComp.setLayoutData(gd);
		}

		{
			Label label = new Label(groupComp, SWT.NONE);
			label.setText(Messages.TabConfig_registerlist_label_text);

			Composite local = new Composite(groupComp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 3;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			local.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) groupComp.getLayout()).numColumns - 1;
			local.setLayoutData(gd);
			{
				fRegisterListPathExpr = new Text(local, SWT.SINGLE | SWT.BORDER);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				fRegisterListPathExpr.setLayoutData(gd);

				fRegisterListBrowseButton = new Button(local, SWT.NONE);
				fRegisterListBrowseButton.setText(Messages.TabConfig_registerlist_button_Browse_text);

				fRegisterListVariablesButton = new Button(local, SWT.NONE);
				fRegisterListVariablesButton.setText(Messages.TabConfig_registerlist_button_Variables_text);
			}
		}

		{
			Label label = new Label(groupComp, SWT.NONE);
			label.setText(Messages.TabConfig_registerlist_path_text);

			fRegisterListActualPath = new Text(groupComp, SWT.SINGLE | SWT.BORDER);
			GridData gd = new GridData(SWT.FILL, 0, true, false);
			gd.horizontalSpan = 4;
			fRegisterListActualPath.setLayoutData(gd);

			fRegisterListActualPath.setEnabled(true);
			fRegisterListActualPath.setEditable(false);
		}
		
		fRegisterListPathExpr.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		fRegisterListBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(Messages.TabConfig_registerList_browse_title, fRegisterListPathExpr);
			}
		});

		fRegisterListVariablesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variablesButtonSelected(fRegisterListPathExpr);
			}
		});
		
		fRegisterListPathExpr.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
				updateActualRegisterListPath();
			}
		});

	}
	
	private void createHwBreakpointControls(Composite comp) {
		Group group = new Group(comp, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
			group.setText(Messages.TabConfig_hwbp_group_text);
		}

		Composite groupComp = new Composite(group, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			groupComp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			groupComp.setLayoutData(gd);
		}

		Composite local = new Composite(groupComp, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		local.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		local.setLayoutData(gd);
		{
			Label label = new Label(local, SWT.NONE);
			label.setText(Messages.TabConfig_hwbplimit_label_text);
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(label);

			fHwBreakpointLimitText = new Text(local, SWT.SINGLE | SWT.BORDER);
			GridDataFactory.fillDefaults().hint(100,SWT.DEFAULT).applyTo(fHwBreakpointLimitText);
		}

		{
			fHwBreakpointLimitExplanation = new Link(groupComp, SWT.LEFT);
			GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(fHwBreakpointLimitExplanation);
			fHwBreakpointLimitExplanation.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {

					String text = e.text;
					if (Activator.getInstance().isDebugging()) {
						System.out.println(text);
					}

					int ret = -1;
					if ("global".equals(text)) {
						ret = PreferencesUtil.createPreferenceDialogOn(comp.getShell(), GlobalHwBreakpointLimitPage.ID, null, null)
								.open();
					} else if ("workspace".equals(text)) {
						ret = PreferencesUtil.createPreferenceDialogOn(comp.getShell(), WorkspaceHwBreakpointLimitPage.ID, null, null)
								.open();
					} else if ("project".equals(text)) {
						assert (fConfiguration != null);
						IProject project = EclipseUtils.getProjectByLaunchConfiguration(fConfiguration);
						ret = PreferencesUtil
								.createPropertyDialogOn(comp.getShell(), project, ProjectHwBreakpointLimitPage.ID, null, null, 0)
								.open();
					} else if ("default".equals(text)) {
						MessageDialog.openInformation(comp.getShell(), "Default value", Messages.TabConfig_hwbplimit_default);
					}
					
					if (ret == Window.OK) {
						updateActualHwBreakpointLimit();
					}
				}
				
			});
		}

		fHwBreakpointLimitText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
				updateActualHwBreakpointLimit();
			}
		});
	}

	private void updateActualHwBreakpointLimit() {

		assert (fConfiguration != null);
		String attr = fHwBreakpointLimitText.getText().toLowerCase().trim();
		String expr = attr;
		String pref = PersistentPreferences.getPreferenceValueForId(Activator.PLUGIN_ID, PersistentPreferences.HW_BP_LIMIT, PersistentPreferences.HW_BP_LIMIT_DEFAULT, EclipseUtils.getProjectByLaunchConfiguration(fConfiguration));
		if (expr == null || expr.isEmpty()) {
			expr = pref; 
		}

		StringBuilder cautionMsg = new StringBuilder();
		if (expr.equals("0") || expr.toLowerCase().equals(PersistentPreferences.HW_BP_LIMIT_NONE)) {
			cautionMsg.append(PersistentPreferences.subTokens(Messages.TabConfig_hwbplimit_links_2, expr));
		}
		else if (expr.equals("-1") || expr.equals(PersistentPreferences.HW_BP_LIMIT_UNLIMITED)) {
			cautionMsg.append(PersistentPreferences.subTokens(Messages.TabConfig_hwbplimit_links_3, expr));
		}
		
		String scope = PersistentPreferences.getPreferenceScopeForId(Activator.PLUGIN_ID, PersistentPreferences.HW_BP_LIMIT, EclipseUtils.getProjectByLaunchConfiguration(fConfiguration));
		StringBuilder scopeMsg = new StringBuilder();
		if (attr == null || attr.isEmpty()) {
			scopeMsg.append(PersistentPreferences.subTokens(PersistentPreferences.subTokens(Messages.TabConfig_hwbplimit_links_1, pref, scope)));
		}

		StringBuilder linkMsg = new StringBuilder();
		linkMsg.append(PersistentPreferences.subTokens(PersistentPreferences.subTokens(Messages.TabConfig_hwbplimit_links_4, pref, scope)));

		StringBuilder finalMsg = new StringBuilder(scopeMsg);
		if (scopeMsg.length() > 0) {
			finalMsg.append("\n");
		}
		finalMsg.append(cautionMsg);
		if (cautionMsg.length() > 0) {
			finalMsg.append("\n");
		}
			
		finalMsg.append(linkMsg);

		fHwBreakpointLimitExplanation.setText(finalMsg.toString());
	}


	private void updateActualRegisterListPath() {

		assert (fConfiguration != null);
		try {
			String expr = fRegisterListPathExpr.getText().trim();
			if (expr == null || expr.isEmpty()) {
				expr = PersistentPreferences.getPreferenceValueForId(Activator.PLUGIN_ID, PersistentPreferences.REGISTER_LIST, "", EclipseUtils.getProjectByLaunchConfiguration(fConfiguration));
			}
			expr = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(expr);
			fRegisterListActualPath.setText(expr);
		} catch (CoreException e) {
			fRegisterListActualPath.setText(e.getMessage());
		}
	}

	private void browseButtonSelected(String title, Text text) {

		FileDialog dialog = new FileDialog(getShell(), SWT.NONE);
		dialog.setText(title);
		String str = text.getText().trim();
		int lastSeparatorIndex = str.lastIndexOf(File.separator);
		if (lastSeparatorIndex != -1)
			dialog.setFilterPath(str.substring(0, lastSeparatorIndex));
		str = dialog.open();
		if (str != null) {
			String tmp = DebugUtils.constructExpr(str, fConfiguration);
			text.setText(tmp);
			updateActualRegisterListPath();
		}
	}

	private void variablesButtonSelected(Text text) {

		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(getShell());
		if (dialog.open() == StringVariableSelectionDialog.OK) {
			text.insert(dialog.getVariableExpression());
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("TabDebugConfig.setDefaults() " + configuration.getName());
		}
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		fConfiguration = configuration;
				
		try {
			fRegisterListPathExpr.setText(configuration.getAttribute(ConfigurationAttributes.ATTR_REGISTER_LIST_EXPR, ""));
			fHwBreakpointLimitText.setText(configuration.getAttribute(ConfigurationAttributes.ATTR_HW_BREAKPOINT_LIMIT, ""));
			updateActualHwBreakpointLimit();
		} catch (CoreException e) {
			Activator.log(e.getStatus());
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String temp = fRegisterListPathExpr.getText().trim();
		configuration.setAttribute(ConfigurationAttributes.ATTR_REGISTER_LIST_EXPR, temp);
		
		temp = fHwBreakpointLimitText.getText().trim();
		if (temp.isEmpty()) {
			temp = PersistentPreferences.getPreferenceValueForId(Activator.PLUGIN_ID, PersistentPreferences.HW_BP_LIMIT, PersistentPreferences.HW_BP_LIMIT_DEFAULT, EclipseUtils.getProjectByLaunchConfiguration(fConfiguration));
		}
		configuration.setAttribute(ConfigurationAttributes.ATTR_HW_BREAKPOINT_LIMIT, temp);
	}

	@Override
	public String getName() {
		return TAB_NAME;
	}

	@Override
	public Image getImage() {
		return Activator.getInstance().getImage("config");
	}

	@Override
	public String getId() {
		return TAB_ID;
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		
		String regList = fRegisterListActualPath.getText();
		if (!regList.isEmpty()) {
			try {
				Path p = Paths.get(regList);
				if (!Files.isReadable(p)) {
					setErrorMessage("Register List: " + p.toString() + " is not readable or does not exist");
					return false;
				}
			} catch (InvalidPathException e) {
				setErrorMessage(e.getMessage());
				return false;
			}
		}
		
		String limit = fHwBreakpointLimitText.getText().trim();
		if (!limit.isEmpty()) {
			if (!limit.equals(PersistentPreferences.HW_BP_LIMIT_UNLIMITED) && 
					!limit.equals(PersistentPreferences.HW_BP_LIMIT_NONE)) { 
				try {
					Integer.parseInt(limit); 
				}
				catch (NumberFormatException e) {
					String message = PersistentPreferences.subTokens(Messages.TabConfig_hwbplimit_error);
					setErrorMessage(message);
					return false;
				}
			}
		}
		
		/*
		 *  Everything looks good...
		 */
		return true;
	}
}
