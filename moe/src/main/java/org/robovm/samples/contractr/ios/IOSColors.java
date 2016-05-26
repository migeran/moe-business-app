package org.robovm.samples.contractr.ios;

import org.robovm.samples.contractr.core.ui.Colors;

import ios.uikit.UIColor;

/**
 * Colors used by ContractR.
 */
public class IOSColors {
    public static final UIColor MAIN = toUIColor(Colors.MAIN);
    public static final UIColor START_WORK = toUIColor(Colors.START_WORK);
    public static final UIColor STOP_WORK = toUIColor(Colors.STOP_WORK);
    private static final UIColor[] CLIENTS = {
            toUIColor(Colors.CLIENT1),
            toUIColor(Colors.CLIENT2),
            toUIColor(Colors.CLIENT3),
            toUIColor(Colors.CLIENT4),
            toUIColor(Colors.CLIENT5),
            toUIColor(Colors.CLIENT6),
            toUIColor(Colors.CLIENT7),
            toUIColor(Colors.CLIENT8),
            toUIColor(Colors.CLIENT9),
    };

    public static final UIColor getClientColor(int index) {
        if (index < 0 || index >= CLIENTS.length) {
            return UIColor.grayColor();
        }
        return CLIENTS[index];
    }

    private static UIColor toUIColor(Colors c) {
        return UIColor.colorWithRedGreenBlueAlpha(c.getRed(), c.getGreen(), c.getBlue(), 1.0);
    }
}
