package ilg.gnumcueclipse.debug.gdbjtag.openocd.dsf;

import org.eclipse.cdt.core.IAddress;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.dsf.datamodel.DMContexts;
import org.eclipse.cdt.dsf.debug.service.IRunControl;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.service.GDBMemory_7_6;
import org.eclipse.cdt.dsf.gdb.service.IGDBProcesses;
import org.eclipse.cdt.dsf.mi.service.IMIContainerDMContext;
import org.eclipse.cdt.dsf.mi.service.IMIExecutionDMContext;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.MemoryByte;

@SuppressWarnings("restriction")
public class SiFiveMemory extends GDBMemory_7_6 {
	

	public SiFiveMemory(DsfSession session) {
		super(session);
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.cdt.dsf.debug.service.IMemory#getMemory(org.eclipse.cdt.dsf.datamodel.IDMContext, org.eclipse.cdt.core.IAddress, long, int, org.eclipse.cdt.dsf.concurrent.DataRequestMonitor)
     */
	@Override
    public void getMemory(IMemoryDMContext memoryDMC, IAddress address, long offset,
    		int wordSize, int wordCount, DataRequestMonitor<MemoryByte[]> drm)
	{
        if (memoryDMC == null) {
            drm.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, INTERNAL_ERROR, "Unknown context type", null)); //$NON-NLS-1$);
            drm.done();            
            return;
        }

    	if (wordSize < 1) {
    		drm.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, NOT_SUPPORTED, "Word size not supported (< 1)", null)); //$NON-NLS-1$
    		drm.done();
    		return;
    	}

    	if (wordCount < 0) {
    		drm.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, IDsfStatusConstants.INTERNAL_ERROR, "Invalid word count (< 0)", null)); //$NON-NLS-1$
    		drm.done();
    		return;
    	}

    	boolean proceed = false;
    	IMIContainerDMContext containerCtx = DMContexts.getAncestorOfType(memoryDMC, IMIContainerDMContext.class);
		if(containerCtx != null) {
			IGDBProcesses procService = getServicesTracker().getService(IGDBProcesses.class);
			IRunControl runControl = getServicesTracker().getService(IRunControl.class);

			if (procService != null && runControl != null) {
				IMIExecutionDMContext[] execCtxs = procService.getExecutionContexts(containerCtx);
				// Return any thread, as long as it is suspended.  This will allow GDB to read the memory
				// and it will be for the process we care about (since we choose a thread within it).
				if (execCtxs != null && execCtxs.length > 0) {
					for (IMIExecutionDMContext execCtx : execCtxs) {
						if (runControl.isSuspended(execCtx)) {
							proceed = true;
						}
					}
				}
			}
		}
		if (!proceed) {
    		//drm.setStatus(new Status(IStatus.CANCEL, GdbPlugin.PLUGIN_ID, IDsfStatusConstants.REQUEST_FAILED, "Cannot read memory while running", null)); //$NON-NLS-1$
    		drm.done();
    		return;
		}
    	
    	getMemoryCache(memoryDMC).getMemory(memoryDMC, address.add(offset), wordSize, wordCount, drm);
	}
}
