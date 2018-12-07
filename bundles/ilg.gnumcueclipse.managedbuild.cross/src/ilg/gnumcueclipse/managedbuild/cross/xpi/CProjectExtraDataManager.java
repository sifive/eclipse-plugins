/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.managedbuild.cross.xpi;

import java.util.Map;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;

import ilg.gnumcueclipse.debug.core.data.ICProjectExtraDataManager;
import ilg.gnumcueclipse.managedbuild.cross.Activator;

public class CProjectExtraDataManager implements ICProjectExtraDataManager {

	// ------------------------------------------------------------------------

	private static final CProjectExtraDataManager fgInstance;

	static {
		// Required via static, to simplify synchronisations
		fgInstance = new CProjectExtraDataManager();
	}

	public static CProjectExtraDataManager getInstance() {
		return fgInstance;
	}

	// ------------------------------------------------------------------------

	public CProjectExtraDataManager() {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("CProjectExtraDataManager()");
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public Map<String, String> getExtraProperties(IConfiguration config) {

//		*** We do not support packs in Freedom Studio ***
//		try {
//			CProjectPacksStorage storage = new CProjectPacksStorage(config);
//			return storage.getOptions();
//		} catch (CoreException e) {
//			;
//		}
		return null; // No extra properties
	}

	// ------------------------------------------------------------------------
}
