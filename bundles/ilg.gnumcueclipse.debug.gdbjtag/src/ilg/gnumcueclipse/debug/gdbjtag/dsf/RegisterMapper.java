package ilg.gnumcueclipse.debug.gdbjtag.dsf;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.cdt.dsf.datamodel.DMContexts;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IStack.IFrameDMContext;
import org.eclipse.cdt.dsf.mi.service.IMIContainerDMContext;
import org.eclipse.cdt.dsf.mi.service.command.output.MIConst;
import org.eclipse.cdt.dsf.mi.service.command.output.MIList;
import org.eclipse.cdt.dsf.mi.service.command.output.MIOutput;
import org.eclipse.cdt.dsf.mi.service.command.output.MIValue;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class RegisterMapper {
	
    static String[] generalRegisters = {
    		"zero",
    		"ra",
    		"sp",
    		"gp",
    		"tp",
    		"t0",
    		"t1",
    		"t2",
    		"s0",
    		"s1",
    		"a0",
    		"a1",
    		"a2",
    		"a3",
    		"a4",
    		"a5",
    		"a6",
    		"a7",
    		"s2",
    		"s3",
    		"s4",
    		"s5",
    		"s6",
    		"s7",
    		"s8",
    		"s9",
    		"s10",
    		"s11",
    		"t3",
    		"t4",
    		"t5",
    		"t6",
    		"pc"};
    
    
    static String[] machineRegisters = {
    		"mvendorid",  
    		"marchid",
    		"mimpid",	
    		"mhartid",
    		"mstatus",
    		"misa",
    		//"medeleg",
    		//"mideleg",
    		"mie",
    		"mtvec",
    		"mcounteren",
    		"mscratch",
    		"mepc",
    		"mcause",
    		"mtval",
    		"mip"
    };
    
	private static String[] userFPRegisters = {
    		"fflags",   //csr001 Floating-Point Accrued Exceptions.
    		"frm",      //csr002 Floating-Point Dynamic Rounding Mode.
    		"fcsr",		//csr003 Floating-Point Control and Status Register (frm+fflags).
    		"ft0","ft1","ft2","ft3","ft4","ft5","ft6","ft7","fs0","fs1","fa0","fa1","fa2","fa3","fa4","fa5","fa6","fa7","fs2","fs3","fs4","fs5","fs6","fs7","fs8","fs9","fs10","fs11","ft8","ft9","ft10","ft11" 
    };

    static HashMap<String, String[]> registerBlocks = new HashMap<>();
    static {
    	registerBlocks.put("generalregisters", generalRegisters);
    	registerBlocks.put("machineregisters", machineRegisters);
    	registerBlocks.put("fpuregisters", userFPRegisters);
    }
    


	static HashMap<IDMContext, HashMap<String, Integer>> contextGdbIndexMap = new HashMap<>();
	static HashMap<IDMContext, HashMap<Integer, Integer>> contextViewIndexMap = new HashMap<>();
	static HashMap<IDMContext, ArrayList<String>> contextGdbNameListMap = new HashMap<>();
	
	static HashMap<String, Integer> getContextGdbIndexMap(IDMContext ctx) {
		if (!contextGdbIndexMap.containsKey(ctx)) {
			contextGdbIndexMap.put(ctx, new HashMap<>());
		}
		
		return contextGdbIndexMap.get(ctx);
	}

	static ArrayList<String> getContextGdbNameList(IDMContext ctx) {
		if (!contextGdbNameListMap.containsKey(ctx)) {
			contextGdbNameListMap.put(ctx, new ArrayList<>());
		}
		
		return contextGdbNameListMap.get(ctx);
	}

	static HashMap<Integer, Integer> getContextViewIndexMap(final IDMContext ctx) {
		IDMContext context = DMContexts.getAncestorOfType(ctx, IMIContainerDMContext.class);
					
		if (!contextViewIndexMap.containsKey(context)) {
			contextViewIndexMap.put(context, new HashMap<>());
		}
		
		return contextViewIndexMap.get(context);
	}

	static void addGdbRegisterList(IDMContext context, MIOutput output) {
		MIList milist = (MIList) output.getMIResultRecord().getMIResults()[0].getMIValue();
    	int gdbIndex = 0;
    	HashMap<String, Integer> gdbIndexMap = getContextGdbIndexMap(context);
    	ArrayList<String> gdbNameList = getContextGdbNameList(context);
    	for (MIValue v : milist.getMIValues()) {
    		MIConst c = (MIConst) v;
    		gdbIndexMap.put(c.getCString(), gdbIndex++);
    		gdbNameList.add(c.getCString());
    	}
	}

	static void addViewerRegisterList(IDMContext context, ArrayList<MIValue> mivalues) {
		HashMap<Integer, Integer> viewIndexMap = getContextViewIndexMap(context);
		HashMap<String, Integer> gdbIndexMap = getContextGdbIndexMap(context);
		int viewIndex = 0;
		for (MIValue v : mivalues) {
			MIConst c = (MIConst) v;
			if (!gdbIndexMap.containsKey(c.getCString())) {
				System.err.println();
			}
			viewIndexMap.put(viewIndex++, gdbIndexMap.get(c.getCString()));
		}
	}

	static int[] remapViewIndexes(IFrameDMContext context, int[] regnos) {
		int[] result = new int[regnos.length];
		HashMap<Integer, Integer> viewIndexMap = getContextViewIndexMap(context);
		int index = 0;
		for (int i : regnos) {
			result[index] = viewIndexMap.get(i);
		}
		return result;
	}

	static int[] remapViewIndexes(IFrameDMContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private final static StringBuilder errors = new StringBuilder();
	private static boolean hasErrors = false;
	private static ArrayList<String> finalList;
	
    static void parseRegisterList(IDMContext context, ArrayList<MIValue> mival, Path filePath) throws IOException {
    	finalList = new ArrayList<>();
    	parseRegisterList(context, mival, filePath, 0);
    }

    private static void addRegister(String r) {
    	if (!finalList.contains(r)) {
    		finalList.add(r);
    	}
    }
    
    /*
     * WARNING: This is a recursive method (to process nested #include directives)
     */
    private static void parseRegisterList(IDMContext context, ArrayList<MIValue> mival, Path filePath, int level) throws IOException {
    	if (!Files.isReadable(filePath)) {
    		errors.append("Could not read from file: ");
    		errors.append(filePath);
    		hasErrors = true;
    		return;
    	}
    	
    	
    	List<String> lines = Files.readAllLines(filePath, Charset.defaultCharset());
    	for (String line : lines) {
    		String tmp = line.trim();
    		if (tmp.startsWith("#include")) {
    			Path incPath = Paths.get(tmp.substring("#include".length()).trim());
    			if (!incPath.isAbsolute()) {
    				incPath = filePath.getParent().resolve(incPath);
    			}
    			parseRegisterList(context, mival, incPath, level+1);
    			continue;
    		}
    		
    		if (tmp.startsWith("#")) {
    			// Skip comment lines
    			continue;
    		}
    		
    		if (tmp.indexOf(' ') > -1) {
    			tmp = tmp.substring(0, tmp.indexOf(' ')).trim();
    		}
    		
    		if (tmp.isEmpty()) {
    			continue;
    		}
    		
    		if (tmp.toLowerCase().contentEquals("gdbregisters")) {
    			ArrayList<String> gdbNameList = RegisterMapper.getContextGdbNameList(context);
    			for (String r : gdbNameList) {
    				addRegister(r);
    			}
    			continue;
    		}
    		if (RegisterMapper.registerBlocks.containsKey(tmp.toLowerCase())) {
    			String[] ra = RegisterMapper.registerBlocks.get(tmp);
    			for (String s : ra) {
    				addRegister(s);
    			}
    			continue;
    		}

    		addRegister(tmp);
    	}
    	
    	if (level > 0) {
    		return;
    	}
    	
    	/*
    	 * Dont do this until the very end.
    	 */
    	HashMap<String, Integer> existingRegisters = RegisterMapper.getContextGdbIndexMap(context);
    	
    	for (String tmp : finalList) {
    		/*
    		 * Only include the register is it exists in the known list of GDB reported registers.
    		 */
    		if (existingRegisters.containsKey(tmp)) {
    			MIConst c = new MIConst();
    			c.setCString(tmp);
    			mival.add(c);
    		}
    		else {
    			hasErrors = true;
    			errors.append(filePath.getFileName());
    			errors.append(": ");
    			errors.append(tmp);
    			errors.append("\n");
    		}
    	}
    	
    	if (hasErrors) {
    		Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					StringBuilder msg = new StringBuilder();
					msg.append("Register List File: ");
					msg.append(filePath.toString());
					msg.append("\n\nThe following register(s) do not exist and are ignored:\n\n");
					msg.append(errors.toString());
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error in register list", msg.toString());
				}
			});
    	}
	}

	static void addList(ArrayList<MIValue> mival, String[] regnames) {
    	for (String regname : regnames) {
    		MIConst c = new MIConst();
    		c.setCString(regname);
    		mival.add(c);
    	}
    }

}
