/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ilg.gnumcueclipse.core.preferences.ScopedPreferenceStoreWithoutDefaults;
import ilg.gnumcueclipse.debug.gdbjtag.Activator;
import ilg.gnumcueclipse.debug.gdbjtag.ui.Messages;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page uses special filed editors, that get the default values from the
 * preferences store, but the values are from the variables store.
 */
public class WorkspaceRegisterListPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	// ------------------------------------------------------------------------

	public static final String ID = "ilg.gnumcueclipse.debug.gdbjtag.openocd.workspacePreferencePage";

	// ------------------------------------------------------------------------

	public WorkspaceRegisterListPage() {
		super(GRID);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.WorkspaceMcuPage()");
		}

		// Explicit use of the workspace preferences.
		setPreferenceStore(new ScopedPreferenceStoreWithoutDefaults(InstanceScope.INSTANCE, Activator.PLUGIN_ID));

		setDescription(Messages.WorkspaceRegisterListPagePropertyPage_description);
	}

	// ------------------------------------------------------------------------

	// Contributed by IWorkbenchPreferencePage
	@Override
	public void init(IWorkbench workbench) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.WorkspaceRegisterPagePage.init()");
		}
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	protected void createFieldEditors() {

		FieldEditor executable = new FileFieldEditor(PersistentPreferences.REGISTER_LIST,
				Messages.RegisterListPage_file_label, getFieldEditorParent());
		addField(executable);

	}

	// ------------------------------------------------------------------------
}
