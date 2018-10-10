package ilg.gnumcueclipse.debug.gdbjtag.dsf;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.mi.service.command.commands.MICommand;
import org.eclipse.cdt.dsf.mi.service.command.output.MIDataListRegisterNamesInfo;
import org.eclipse.cdt.dsf.mi.service.command.output.MIList;
import org.eclipse.cdt.dsf.mi.service.command.output.MIOutput;
import org.eclipse.cdt.dsf.mi.service.command.output.MIResult;
import org.eclipse.cdt.dsf.mi.service.command.output.MIValue;
import org.eclipse.core.runtime.Platform;

public class MIDataListRegisterNamesInfoEx extends MICommand<MIDataListRegisterNamesInfo>  {
    public MIDataListRegisterNamesInfoEx(IContainerDMContext ctx) {
        super(ctx, "-data-list-register-names"); //$NON-NLS-1$
    }

    public MIDataListRegisterNamesInfoEx(IContainerDMContext ctx, int [] regnos) {
        this(ctx);
        if (regnos != null && regnos.length > 0) {
            String[] array = new String[regnos.length];
            for (int i = 0; i < regnos.length; i++) {
                array[i] = Integer.toString(regnos[i]);
            }
            setParameters(array);
        }
    }
    
    @Override
    public MIDataListRegisterNamesInfo getResult(MIOutput output) {
    	RegisterMapper.addGdbRegisterList(getContext(), output);
    	
    	ArrayList<MIValue> mivalues = new ArrayList<>();
    	
    	Path installPath = null;
    	try {
			installPath = Paths.get(Platform.getInstallLocation().getURL().toURI()).resolve(Paths.get("SiFive","metadata","registerlist.txt"));
			System.out.println("Looking for register list in: " + installPath);
    		RegisterMapper.parseRegisterList(getContext(), mivalues, installPath);
    	}
    	catch (Exception e) {
    		/*
    		 * Anything goes wrong, use the default list.
    		 */
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
