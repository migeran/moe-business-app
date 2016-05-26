package org.robovm.samples.contractr.ios;

import com.intel.moe.natj.general.Pointer;
import com.intel.moe.natj.general.ann.RegisterOnStartup;
import com.intel.moe.natj.objc.ObjCRuntime;
import com.intel.moe.natj.objc.ann.Selector;

import org.robovm.samples.contractr.ios.viewcontrollers.RootViewController;

import ios.NSObject;
import ios.coregraphics.struct.CGPoint;
import ios.coregraphics.struct.CGRect;
import ios.coregraphics.struct.CGSize;
import ios.foundation.NSArray;
import ios.foundation.NSAttributedString;
import ios.foundation.NSDictionary;
import ios.foundation.NSMutableDictionary;
import ios.uikit.UIApplication;
import ios.uikit.UIColor;
import ios.uikit.UIFont;
import ios.uikit.UIImage;
import ios.uikit.UINavigationBar;
import ios.uikit.UISegmentedControl;
import ios.uikit.UISwitch;
import ios.uikit.UITabBar;
import ios.uikit.UIViewController;
import ios.uikit.UIWindow;
import ios.uikit.c.UIKit;
import ios.uikit.enums.UIBarStyle;
import ios.uikit.protocol.UIApplicationDelegate;

import static ios.uikit.c.UIKit.NSFontAttributeName;
import static ios.uikit.c.UIKit.UIGraphicsBeginImageContextWithOptions;
import static ios.uikit.c.UIKit.UIGraphicsEndImageContext;
import static ios.uikit.c.UIKit.UIGraphicsGetImageFromCurrentImageContext;

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
