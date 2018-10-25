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

package ilg.gnumcueclipse.debug.gdbjtag.dsf;

import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.debug.service.IStack.IFrameDMContext;
import org.eclipse.cdt.dsf.debug.service.command.ICommand;
import org.eclipse.cdt.dsf.gdb.service.command.CommandFactory_6_8;
import org.eclipse.cdt.dsf.mi.service.command.output.MIDataListRegisterNamesInfo;
import org.eclipse.cdt.dsf.mi.service.command.output.MIDataListRegisterValuesInfo;

public class GnuMcuCommandFactory extends CommandFactory_6_8 {

	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	
	public ICommand<MIDataListRegisterNamesInfo> createMIDataListRegisterNames(IContainerDMContext ctx) {
		return new MIDataListRegisterNamesEx(ctx);
	}

	public ICommand<MIDataListRegisterNamesInfo> createMIDataListRegisterNames(IContainerDMContext ctx, int [] regnos) {
		return new MIDataListRegisterNamesEx(ctx, regnos);
	}

	@Override
	public ICommand<MIDataListRegisterValuesInfo> createMIDataListRegisterValues(IFrameDMContext ctx, int fmt,
			int[] regnos) {
		return new MIDataListRegisterValuesEx(ctx, fmt, regnos);
	}
	
	@Override
	public ICommand<MIDataListRegisterValuesInfo> createMIDataListRegisterValues(IFrameDMContext ctx, int fmt) {
		return new MIDataListRegisterValuesEx(ctx, fmt);
	}
}
