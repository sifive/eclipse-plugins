package ilg.gnumcueclipse.debug.gdbjtag;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import ilg.gnumcueclipse.debug.gdbjtag.preferences.PersistentPreferences;

public class HwBreakpointLimitFieldEditor extends StringFieldEditor {
    private int minValidValue = -1;

    private int maxValidValue = Integer.MAX_VALUE;

    private static final int DEFAULT_TEXT_LIMIT = 10;

    /**
    * Creates a new integer field editor
    */
    protected HwBreakpointLimitFieldEditor() {
    }

    /**
     * Creates an integer field editor.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public HwBreakpointLimitFieldEditor(String name, String labelText, Composite parent) {
        this(name, labelText, parent, DEFAULT_TEXT_LIMIT);
    }

    /**
     * Creates an integer field editor.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     * @param textLimit the maximum number of characters in the text.
     */
    public HwBreakpointLimitFieldEditor(String name, String labelText, Composite parent,
            int textLimit) {
        init(name, labelText);
        setTextLimit(textLimit);
        setEmptyStringAllowed(false);
        setErrorMessage(JFaceResources
                .getString("IntegerFieldEditor.errorMessage"));//$NON-NLS-1$
        createControl(parent);
    }

    /**
     * Sets the range of valid values for this field.
     *
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     */
    public void setValidRange(int min, int max) {
        minValidValue = min;
        maxValidValue = max;
		setErrorMessage(JFaceResources.format("IntegerFieldEditor.errorMessageRange", //$NON-NLS-1$
				Integer.valueOf(min), Integer.valueOf(max)));
    }

    @Override
	protected boolean checkState() {

        Text text = getTextControl();

        if (text == null) {
			return false;
		}

        String numberString = text.getText().trim();
        
        if (numberString.isEmpty() || 
        		numberString.toLowerCase().equals(PersistentPreferences.HW_BP_LIMIT_UNLIMITED) ||
        		numberString.toLowerCase().equals(PersistentPreferences.HW_BP_LIMIT_NONE)) {
			clearErrorMessage();
			return true;
        }
        
        try {
            int number = Integer.valueOf(numberString).intValue();
            if (number >= minValidValue && number <= maxValidValue) {
				clearErrorMessage();
				return true;
			}

			showErrorMessage();
			return false;

        } catch (NumberFormatException e1) {
            showErrorMessage();
        }

        return false;
    }

    @Override
	protected void doLoad() {
        Text text = getTextControl();
        if (text != null) {
            text.setText(getPreferenceStore().getString(getPreferenceName()));//$NON-NLS-1$
            oldValue = text.getText();
        }

    }

    @Override
	protected void doLoadDefault() {
        Text text = getTextControl();
        if (text != null) {
            text.setText(getPreferenceStore().getDefaultString(getPreferenceName()));//$NON-NLS-1$
        }
        valueChanged();
    }

    @Override
	protected void doStore() {
        Text text = getTextControl();
        if (text != null) {
            getPreferenceStore().setValue(getPreferenceName(), text.getText().toLowerCase().trim());
        }
    }
}

