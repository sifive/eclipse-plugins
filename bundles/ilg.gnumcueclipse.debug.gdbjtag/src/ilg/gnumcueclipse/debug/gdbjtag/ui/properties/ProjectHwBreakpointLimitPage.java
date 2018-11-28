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

package ilg.gnumcueclipse.debug.gdbjtag.ui.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;

import ilg.gnumcueclipse.core.preferences.ScopedPreferenceStoreWithoutDefaults;
import ilg.gnumcueclipse.core.ui.FieldEditorPropertyPage;
import ilg.gnumcueclipse.debug.gdbjtag.Activator;
import ilg.gnumcueclipse.debug.gdbjtag.HwBreakpointLimitFieldEditor;
import ilg.gnumcueclipse.debug.gdbjtag.preferences.PersistentPreferences;
import ilg.gnumcueclipse.debug.gdbjtag.ui.Messages;

public class ProjectHwBreakpointLimitPage extends FieldEditorPropertyPage {

	// ------------------------------------------------------------------------

	public static final String ID = "ilg.gnumcueclipse.debug.gdbjtag.projectHwBreakpointLimitPropertiesPage";

	// ------------------------------------------------------------------------

	public ProjectHwBreakpointLimitPage() {
		super(GRID);
		setDescription(PersistentPreferences.subTokens(Messages.ProjectHwBreakpointLimitPagePropertyPage_description));
	}

	// ------------------------------------------------------------------------

	protected IPreferenceStore doGetPreferenceStore() {

		Object element = getElement();
		if (element instanceof IProject) {
			return new ScopedPreferenceStoreWithoutDefaults(new ProjectScope((IProject) element), Activator.PLUGIN_ID);
		}
		return null;
	}

	@Override
	protected void createFieldEditors() {

		FieldEditor executable = new HwBreakpointLimitFieldEditor(PersistentPreferences.HW_BP_LIMIT,
				Messages.HwBreakpointLimitLabel, getFieldEditorParent());
		addField(executable);
	}

	// ------------------------------------------------------------------------
}
