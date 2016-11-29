package org.robovm.samples.contractr.ios.viewcontrollers;

import org.moe.natj.general.Pointer;
import org.moe.natj.objc.ann.ObjCClassName;

import org.robovm.samples.contractr.ios.ContractRApp;

import apple.uikit.UIViewController;

/**
 * Abstract {@link UIViewController} which handles dependency injection using Dagger.
 */
@ObjCClassName("InjectedViewController")
public abstract class InjectedViewController extends UIViewController {

    public InjectedViewController(Pointer peer) {
        super(peer);
        ContractRApp.getComponent().inject(this);
    }

}
