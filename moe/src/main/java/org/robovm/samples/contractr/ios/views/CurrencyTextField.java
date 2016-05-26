package org.robovm.samples.contractr.ios.views;

import com.intel.moe.natj.general.Pointer;
import com.intel.moe.natj.general.ann.ByValue;
import com.intel.moe.natj.general.ann.RegisterOnStartup;
import com.intel.moe.natj.objc.ObjCRuntime;
import com.intel.moe.natj.objc.ann.ObjCClassName;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import ios.foundation.NSCoder;
import ios.foundation.struct.NSRange;
import ios.uikit.UITextField;
import ios.uikit.UITextPosition;
import ios.uikit.enums.UIControlEvents;
import ios.uikit.enums.UIKeyboardType;
import ios.uikit.protocol.UITextFieldDelegate;

@com.intel.moe.natj.general.ann.Runtime(ObjCRuntime.class)
@ObjCClassName("CurrencyTextField")
@RegisterOnStartup
public class CurrencyTextField extends UITextField implements UITextFieldDelegate {

    public static native CurrencyTextField alloc();

    protected CurrencyTextField(Pointer peer) {
        super(peer);
        formatter = NumberFormat.getCurrencyInstance(Locale.US);
    }

    private final NumberFormat formatter;

    @Override
    public UITextField initWithCoder(NSCoder nsCoder) {
        UITextField textField = super.initWithCoder(nsCoder);
        setKeyboardType(UIKeyboardType.DecimalPad);
        setDelegate(this);
        return textField;
    }

    @Override
    public boolean textFieldShouldChangeCharactersInRangeReplacementString(UITextField textField, @ByValue NSRange range, String string) {

        if (string.isEmpty()
                && range.length() == 1
                && !Character.isDigit(textField.text().charAt(
                (int) range.location()))) {

            setCaretPosition(textField, (int) range.location());
            return false;
        }

        String oldText = textField.text();
        String newText = oldText.substring(0, (int) range.location())
                + string + oldText.substring((int) (range.location() + range.length()));
        textField.setText(newText);

        int distanceFromEnd = oldText.length() - (int) (range.location() + range.length());
        int caretPos = newText.length() - distanceFromEnd;
        if (caretPos >= 0 && caretPos <= newText.length()) {
            setCaretPosition(textField, caretPos);
        }

        textField.sendActionsForControlEvents(UIControlEvents.EditingChanged);
        return false;
    }

    private BigDecimal parseAmount(String s) {
        s = s.replaceAll("\\D", "");
        if (s.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(s).scaleByPowerOfTen(-formatter.getMinimumFractionDigits());
    }

    @Override
    public void setText(String v) {
        BigDecimal amount = parseAmount(v);
        super.setText(formatter.format(amount));
    }

    private static void setCaretPosition(UITextField tf, int position) {
        UITextPosition start = tf.positionFromPositionOffset(tf.beginningOfDocument(), position);
        UITextPosition end = tf.positionFromPositionOffset(start, 0);
        tf.setSelectedTextRange(tf.textRangeFromPositionToPosition(start, end));
    }

    public BigDecimal getAmount() {
        return parseAmount(text());
    }

    public void setAmount(BigDecimal amount) {
        setText(formatter.format(amount));
    }

}
