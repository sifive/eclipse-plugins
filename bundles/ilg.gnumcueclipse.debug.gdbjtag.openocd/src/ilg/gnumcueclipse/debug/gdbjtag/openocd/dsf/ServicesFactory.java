/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.openocd.dsf;

import org.eclipse.cdt.dsf.debug.service.IMemory;
import org.eclipse.cdt.dsf.mi.service.IMIBackend;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

import ilg.gnumcueclipse.debug.gdbjtag.dsf.GnuMcuDebuggerCommandsService;
import ilg.gnumcueclipse.debug.gdbjtag.dsf.GnuMcuGdbServerBackend;
import ilg.gnumcueclipse.debug.gdbjtag.dsf.GnuMcuServicesFactory;
import ilg.gnumcueclipse.debug.gdbjtag.openocd.Activator;

public class ServicesFactory extends GnuMcuServicesFactory {

	// ------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private final String fVersion;

	// ------------------------------------------------------------------------

	public ServicesFactory(String version, String mode) {
		super(version, mode);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.ServicesFactory(" + version + "," + mode + ") " + this);
		}
		fVersion = version;
	}

	// ------------------------------------------------------------------------

	@Override
	protected IMIBackend createBackendGDBService(DsfSession session, ILaunchConfiguration lc) {
		return new GdbBackend(session, lc);
	}

	@Override
	protected GnuMcuGdbServerBackend createGdbServerBackendService(DsfSession session, ILaunchConfiguration lc) {
		return new GdbServerBackend(session, lc);
	}

	@Override
	protected GnuMcuDebuggerCommandsService createDebuggerCommandsService(DsfSession session, ILaunchConfiguration lc,
			String mode) {
		return new DebuggerCommands(session, lc, mode);
	}

	@Override
	protected IMemory createMemoryService(DsfSession session) {
		return new SiFiveMemory(session);
	}
	// ------------------------------------------------------------------------
}
