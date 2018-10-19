package ilg.gnumcueclipse.debug.gdbjtag.dsf;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControlService;
import org.eclipse.cdt.dsf.mi.service.command.commands.MIDataListRegisterNames;
import org.eclipse.cdt.dsf.mi.service.command.output.MIDataListRegisterNamesInfo;
import org.eclipse.cdt.dsf.mi.service.command.output.MIList;
import org.eclipse.cdt.dsf.mi.service.command.output.MIOutput;
import org.eclipse.cdt.dsf.mi.service.command.output.MIResult;
import org.eclipse.cdt.dsf.mi.service.command.output.MIValue;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.debug.gdbjtag.Activator;
import ilg.gnumcueclipse.debug.gdbjtag.ConfigurationAttributes;
import ilg.gnumcueclipse.debug.gdbjtag.ILaunchConfigurationProvider;
import ilg.gnumcueclipse.debug.gdbjtag.preferences.PersistentPreferences;

public class MIDataListRegisterNamesEx extends MIDataListRegisterNames  {
    public MIDataListRegisterNamesEx(IContainerDMContext ctx) {
        super(ctx); //$NON-NLS-1$
    }

    public MIDataListRegisterNamesEx(IContainerDMContext ctx, int [] regnos) {
        super(ctx, regnos);
    }
    
    @Override
    public MIDataListRegisterNamesInfo getResult(MIOutput output) {
    	RegisterMapper.addGdbRegisterList(getContext(), output);
    	
    	DsfServicesTracker tracker = new DsfServicesTracker(Activator.getInstance().getBundle().getBundleContext(), getContext().getSessionId());
    	ICommandControlService fCommandControl = (ICommandControlService) tracker.getService(ICommandControlService.class);
    	String expr = "";
    	if (fCommandControl instanceof ILaunchConfigurationProvider) {
    		
    		ILaunchConfiguration launchConfiguration = ((ILaunchConfigurationProvider) fCommandControl)
    				.getLaunchConfiguration();
    		try {
    			expr = launchConfiguration.getAttribute(ConfigurationAttributes.ATTR_REGISTER_LIST_EXPR, "");
        		if (expr == null || expr.isEmpty()) {
    				expr = PersistentPreferences.getPreferenceValueForId(Activator.PLUGIN_ID, PersistentPreferences.REGISTER_LIST, "", EclipseUtils.getProjectByLaunchConfiguration(launchConfiguration));
    			}
    			expr = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(expr);
			} catch (CoreException e) {
				expr = "";
			}
    		
		}

    	
    	
    	ArrayList<MIValue> mivalues = new ArrayList<>();
    	
    	Path installPath = (expr.isEmpty())?null:Paths.get(expr);
    	try {
			System.out.println("Looking for register list in: " + installPath);
    		RegisterMapper.parseRegisterList(getContext(), mivalues, installPath);
    	}
    	catch (Exception e) {
    		/*
    		 * Anything goes wrong, use the default list.
    		 */
    		if (e.getMessage() != null) {
    			System.out.println(e.getMessage());
    		}
    		System.out.println("Using default embedded register list");
    		RegisterMapper.addList(mivalues, RegisterMapper.generalRegisters);
    	}
    	
    	RegisterMapper.addViewerRegisterList(getContext(), mivalues);
    	
    	MIList list = new MIList();
    	list.setMIResults(new MIResult[0]);
    	list.setMIValues(mivalues.toArray(new MIValue[0]));
    	
    	output.getMIResultRecord().getMIResults()[0].setMIValue(list);
    	output.getMIResultRecord().getMIResults()[0].getMIValue();
        return new MIDataListRegisterNamesInfo(output);
    }

}
