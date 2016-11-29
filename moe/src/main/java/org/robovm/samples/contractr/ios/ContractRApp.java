package org.robovm.samples.contractr.ios;

import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.RegisterOnStartup;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.Selector;

import org.robovm.samples.contractr.ios.viewcontrollers.RootViewController;

import apple.NSObject;
import apple.coregraphics.struct.CGPoint;
import apple.coregraphics.struct.CGRect;
import apple.coregraphics.struct.CGSize;
import apple.foundation.NSArray;
import apple.foundation.NSAttributedString;
import apple.foundation.NSDictionary;
import apple.foundation.NSMutableDictionary;
import apple.uikit.UIApplication;
import apple.uikit.UIColor;
import apple.uikit.UIFont;
import apple.uikit.UIImage;
import apple.uikit.UINavigationBar;
import apple.uikit.UISegmentedControl;
import apple.uikit.UISwitch;
import apple.uikit.UITabBar;
import apple.uikit.UIViewController;
import apple.uikit.UIWindow;
import apple.uikit.c.UIKit;
import apple.uikit.enums.UIBarStyle;
import apple.uikit.protocol.UIApplicationDelegate;

import static apple.uikit.c.UIKit.NSFontAttributeName;
import static apple.uikit.c.UIKit.UIGraphicsBeginImageContextWithOptions;
import static apple.uikit.c.UIKit.UIGraphicsEndImageContext;
import static apple.uikit.c.UIKit.UIGraphicsGetImageFromCurrentImageContext;

/**
 * App entry point.
 */
@RegisterOnStartup
public class ContractRApp extends NSObject implements UIApplicationDelegate {

    @Selector("alloc")
    public static native ContractRApp alloc();

    protected ContractRApp(Pointer peer) {
        super(peer);
    }

    private UIWindow window;

    private static ContractRComponent component;

    public static ContractRComponent getComponent() {
        return component;
    }

    @Override
    public boolean applicationWillFinishLaunchingWithOptions(UIApplication application, NSDictionary<?, ?> launchOptions) {
        /* Render tab bar images from the Ionicons TTF font. */
        RootViewController rootViewController = (RootViewController) window().rootViewController();
        UIFont ioniconsFont = UIFont.fontWithNameSize("Ionicons", 30.0);
        UIImage workIconImage = createIconImage(ioniconsFont, '\uf1e1');
        UIImage reportsIconImage = createIconImage(ioniconsFont, '\uf2b5');
        UIImage clientsIconImage = createIconImage(ioniconsFont, '\uf1bf');
        UIImage tasksIconImage = createIconImage(ioniconsFont, '\uf16c');
        NSArray<? extends UIViewController> viewControllers = rootViewController.viewControllers();
        viewControllers.get(0).tabBarItem().setImage(workIconImage);
        viewControllers.get(1).tabBarItem().setImage(reportsIconImage);
        viewControllers.get(2).tabBarItem().setImage(clientsIconImage);
        viewControllers.get(3).tabBarItem().setImage(tasksIconImage);

        /* Customize the colors in the UI. */
        UITabBar appearanceTabBar = ObjCRuntime.cast(UITabBar.appearance(), UITabBar.class);
        appearanceTabBar.setTintColor(IOSColors.MAIN);
        UINavigationBar appearanceNavigationBar = ObjCRuntime.cast(UINavigationBar.appearance(), UINavigationBar.class);
        appearanceNavigationBar.setBarStyle(UIBarStyle.Black);
        appearanceNavigationBar.setBarTintColor(IOSColors.MAIN);
        appearanceNavigationBar.setTintColor(UIColor.whiteColor());
        UISwitch appearanceSwitch = ObjCRuntime.cast(UISwitch.appearance(), UISwitch.class);
        appearanceSwitch.setOnTintColor(IOSColors.MAIN);
        UISegmentedControl appearanceSegmentedControl = ObjCRuntime.cast(UISegmentedControl.appearance(), UISegmentedControl.class);
        appearanceSegmentedControl.setTintColor(IOSColors.MAIN);

        return true;
    }

    private CGRect calculateIconDrawingRect(NSAttributedString s, CGSize imageSize) {
        CGSize iconSize = s.size();
        double xOffset = (imageSize.width() - iconSize.width()) / 2.0;
        double yOffset = (imageSize.height() - iconSize.height()) / 2.0;
        return new CGRect(new CGPoint(xOffset, yOffset), new CGSize(iconSize.width(), iconSize.height()));
    }

    private UIImage createIconImage(UIFont font, char code) {
        // Create a 30x30 image on iOS 6 and 60x60 on later iOS versions.
        double side = System.getProperty("os.version").startsWith("6.") ? 30 : 60;
        CGSize imageSize = new CGSize(side, side);
        UIGraphicsBeginImageContextWithOptions(imageSize, false, 0.0);
        NSDictionary<String, Object> attributes = (NSDictionary<String, Object>) NSMutableDictionary.dictionary();
        attributes.put(NSFontAttributeName(), font);
        NSAttributedString s = NSAttributedString.alloc().initWithStringAttributes(Character.toString(code), attributes);
        s.drawInRect(calculateIconDrawingRect(s, imageSize));
        UIImage image = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
        return image;
    }

    public static void main(String[] args) {
        component = DaggerContractRComponent.builder()
                .contractRModule(new ContractRModule())
                .build();

        UIKit.UIApplicationMain(0, null, null, ContractRApp.class.getName());
    }

    @Override
    public void setWindow(UIWindow value) {
        window = value;
    }

    @Override
    public UIWindow window() {
        return window;
    }

}
