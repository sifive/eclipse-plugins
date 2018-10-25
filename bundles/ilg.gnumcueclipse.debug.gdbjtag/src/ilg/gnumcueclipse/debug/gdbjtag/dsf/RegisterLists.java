package ilg.gnumcueclipse.debug.gdbjtag.dsf;

import java.util.HashMap;
import java.util.Map;

public class RegisterLists {

	private static final String OPENOCD_REGISTERS = "openocd_registers";
	private static final String FPU_REGISTERS = "fpu_registers";
	private static final String PERFMON_REGISTERS = "perfmon_registers";
	private static final String MACHINE_REGISTERS = "machine_registers";
	private static final String GENERAL_REGISTERS = "general_registers";

	static final String DEFAULT_REGISTERS = OPENOCD_REGISTERS;
	
	static String[] machineRegisters = { "mstatus", "misa", "mie", "mtvec", "mscratch", "mepc", "mcause", "mtval", "mip", "tselect", "tdata1", "tdata2",
			"tdata3", "dcsr", "dpc", "dscratch", "mcycle", "minstret", "cycle", "time", "instret", "mvendorid", "marchid", "mimpid", "mhartid", "priv" };

	static String[] generalRegisters = { "zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "s2",
			"s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6", "pc" };

	static String[] perfmonRegisters = { "mhpmevent3", "mhpmevent4", "mhpmevent5", "mhpmevent6", "mhpmevent7", "mhpmevent8", "mhpmevent9", "mhpmevent10",
			"mhpmevent11", "mhpmevent12", "mhpmevent13", "mhpmevent14", "mhpmevent15", "mhpmevent16", "mhpmevent17", "mhpmevent18", "mhpmevent19",
			"mhpmevent20", "mhpmevent21", "mhpmevent22", "mhpmevent23", "mhpmevent24", "mhpmevent25", "mhpmevent26", "mhpmevent27", "mhpmevent28",
			"mhpmevent29", "mhpmevent30", "mhpmevent31", "pmpcfg0", "pmpcfg1", "pmpcfg2", "pmpcfg3", "pmpaddr0", "pmpaddr1", "pmpaddr2", "pmpaddr3", "pmpaddr4",
			"pmpaddr5", "pmpaddr6", "pmpaddr7", "pmpaddr8", "pmpaddr9", "pmpaddr10", "pmpaddr11", "pmpaddr12", "pmpaddr13", "pmpaddr14", "pmpaddr15",
			"hpmcounter3", "mhpmcounter4", "mhpmcounter5", "mhpmcounter6", "mhpmcounter7", "mhpmcounter8", "mhpmcounter9", "mhpmcounter10", "mhpmcounter11",
			"mhpmcounter12", "mhpmcounter13", "mhpmcounter14", "mhpmcounter15", "mhpmcounter16", "mhpmcounter17", "mhpmcounter18", "mhpmcounter19",
			"mhpmcounter20", "mhpmcounter21", "mhpmcounter22", "mhpmcounter23", "mhpmcounter24", "mhpmcounter25", "mhpmcounter26", "mhpmcounter27",
			"mhpmcounter28", "mhpmcounter29", "mhpmcounter30", "mhpmcounter31", "hpmcounter3", "hpmcounter4", "hpmcounter5", "hpmcounter6", "hpmcounter7",
			"hpmcounter8", "hpmcounter9", "hpmcounter10", "hpmcounter11", "hpmcounter12", "hpmcounter13", "hpmcounter14", "hpmcounter15", "hpmcounter16",
			"hpmcounter17", "hpmcounter18", "hpmcounter19", "hpmcounter20", "hpmcounter21", "hpmcounter22", "hpmcounter23", "hpmcounter24", "hpmcounter25",
			"hpmcounter26", "hpmcounter27", "hpmcounter28", "hpmcounter29", "hpmcounter30", "hpmcounter31" };

	static String[] fpuRegisters = { "fflags", // csr001 Floating-Point Accrued Exceptions.
			"frm", // csr002 Floating-Point Dynamic Rounding Mode.
			"fcsr", // csr003 Floating-Point Control and Status Register (frm+fflags).
			"ft0", "ft1", "ft2", "ft3", "ft4", "ft5", "ft6", "ft7", "fs0", "fs1", "fa0", "fa1", "fa2", "fa3", "fa4", "fa5", "fa6", "fa7", "fs2", "fs3", "fs4",
			"fs5", "fs6", "fs7", "fs8", "fs9", "fs10", "fs11", "ft8", "ft9", "ft10", "ft11" };

	static String[] openOcdRegisters = { GENERAL_REGISTERS, MACHINE_REGISTERS, PERFMON_REGISTERS };
	
	static Map<String, String[]> registerBlocks = new HashMap<>();
	
	static {
		RegisterLists.registerBlocks.put(GENERAL_REGISTERS, RegisterLists.generalRegisters);
		RegisterLists.registerBlocks.put(MACHINE_REGISTERS, RegisterLists.machineRegisters);
		RegisterLists.registerBlocks.put(PERFMON_REGISTERS, RegisterLists.perfmonRegisters);
		RegisterLists.registerBlocks.put(FPU_REGISTERS, RegisterLists.fpuRegisters);
		RegisterLists.registerBlocks.put(OPENOCD_REGISTERS, RegisterLists.openOcdRegisters);
	}


}
