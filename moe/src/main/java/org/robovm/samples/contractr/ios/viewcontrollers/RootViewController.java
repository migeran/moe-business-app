package org.robovm.samples.contractr.ios.viewcontrollers;

import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.Owned;
import org.moe.natj.general.ann.RegisterOnStartup;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCClassName;
import org.moe.natj.objc.ann.Selector;

import apple.uikit.UITabBarController;

@org.moe.natj.general.ann.Runtime(ObjCRuntime.class)
@ObjCClassName("RootViewController")
@RegisterOnStartup
public class RootViewController extends UITabBarController {

    @Owned
    @Selector("alloc")
    public static native RootViewController alloc();

    protected RootViewController(Pointer peer) {
        super(peer);
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
    }
}
