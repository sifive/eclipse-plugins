package ilg.gnumcueclipse.debug.gdbjtag.dsf;

import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IStack.IFrameDMContext;
import org.eclipse.cdt.dsf.debug.service.command.ICommand;
import org.eclipse.cdt.dsf.debug.service.command.ICommandResult;
import org.eclipse.cdt.dsf.mi.service.MIFormat;
import org.eclipse.cdt.dsf.mi.service.command.commands.MICommand;
import org.eclipse.cdt.dsf.mi.service.command.commands.MIDataListRegisterValues;
import org.eclipse.cdt.dsf.mi.service.command.output.MIDataListRegisterValuesInfo;
import org.eclipse.cdt.dsf.mi.service.command.output.MIOutput;

public class MIDataListRegisterValuesEx extends MICommand<MIDataListRegisterValuesInfo> {

    int[] regnums;
    int fFmt;

    public MIDataListRegisterValuesEx(IFrameDMContext ctx, int fmt) {
        this(ctx, fmt, null);
    }

    /**
     * @since 4.3
     */
    public MIDataListRegisterValuesEx(IFrameDMContext ctx, int fmt, int [] regnos) {
    	super(ctx, "-data-list-register-values"); //$NON-NLS-1$
    	
    	/*
    	 * Turn the supplied regnos (which are viewer indexes) int a list of gdb indexes
    	 */
    	int[] newIndexes = RegisterMapper.remapViewIndexes(ctx, regnos);
    	
    	init(fmt, newIndexes);
    }

    private void init(int fmt, int [] regnos) {
        regnums = regnos;

        String format = "x"; //$NON-NLS-1$
        switch (fmt) {
            case MIFormat.NATURAL:     format = "N"; break ; //$NON-NLS-1$
            case MIFormat.RAW:         format = "r"; break ; //$NON-NLS-1$
            case MIFormat.DECIMAL:     format = "d"; break ; //$NON-NLS-1$
            case MIFormat.BINARY:      format = "t"; break ; //$NON-NLS-1$
            case MIFormat.OCTAL:       format = "o"; break ; //$NON-NLS-1$
            case MIFormat.HEXADECIMAL: format = "x"; break ; //$NON-NLS-1$
            default:                      format = "x"; break ; //$NON-NLS-1$
        }
        
        fFmt = fmt;

        setOptions(new String[]{format});

        if (regnos != null && regnos.length > 0) {
            String[] array = new String[regnos.length];
            for (int i = 0; i < regnos.length; i++) {
                array[i] = Integer.toString(regnos[i]);
            }
            setParameters(array);
        }
    }
    
    public int[] getRegList() {
        return regnums;
    }

    @Override
    public MIDataListRegisterValuesInfo getResult(MIOutput output) {
        return new MIDataListRegisterValuesInfo(output);
    }
    
    /*
     * Takes the supplied command and coalesces it with this one.
     * The result is a new third command which represent the two
     * original command.
     */
    @Override
    public MIDataListRegisterValues coalesceWith(ICommand<? extends ICommandResult> command ) {
        /*
         * Can coalesce only with other DsfMIDataListRegisterValues commands.
         */
        if (! (command instanceof  MIDataListRegisterValues) ) return null;
        
        IDMContext context = getContext();
        
        /*
         * Make sure we are coalescing over the same context
         */
        if (!command.getContext().equals(context)) {
        	return null;
        }
        
        MIDataListRegisterValuesEx  cmd = (MIDataListRegisterValuesEx) command;
        
        /*
         * If the format is different then this cannot be added to the list.
         */
        if ( fFmt != cmd.fFmt ) return null;

        int[] newregnos = new int[ regnums.length + cmd.regnums.length];
        
        /*
         * We need to add the new register #'s to the list. If one is already there
         * then do not add it twice. So copy the original list of this command.
         */
        
        for ( int idx = 0 ; idx < regnums.length ; idx ++) {
            newregnos[ idx ] = regnums[ idx ];
        }

        int curloc = regnums.length;
        
        for ( int ndx = 0 ; ndx < cmd.regnums.length; ndx ++) {
            
            int curnum = cmd.regnums[ ndx ] ;
            int ldx;
            
            /*
             * Search the current list to see if this entry is in it.
             */
            
            for ( ldx = 0 ; ldx < regnums.length; ldx ++ ) {
                if ( newregnos[ ldx ] == curnum ) {
                    break ;
                }
            }
            
            if ( ldx == regnums.length ) {
                
                /*
                 *  Since we did not find a match add it at the end of the list.
                 */
                newregnos[ curloc ] = curnum;
                curloc ++;
            }
        }
        
        /*
         * Create a final proper array set of the new combined list.
         */
        int[] finalregnums = new int[ curloc ] ;
        
        for ( int fdx = 0 ; fdx < curloc ; fdx ++ ) {
            finalregnums[ fdx ] = newregnos[ fdx ];
        }
        
        /*
         *  Now construct a new one. The format we will use is this command.
         */
        return new MIDataListRegisterValues((IFrameDMContext)getContext(), fFmt, finalregnums);
    }

}
