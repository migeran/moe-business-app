package org.robovm.samples.contractr.ios.viewcontrollers;

import com.intel.moe.natj.general.Pointer;
import com.intel.moe.natj.objc.ann.ObjCClassName;

import org.robovm.samples.contractr.ios.ContractRApp;

import ios.uikit.UITableViewController;

/**
 * Abstract {@link UITableViewController} which handles dependency injection using Dagger.
 */
@ObjCClassName("InjectedTableViewController")
public abstract class InjectedTableViewController extends UITableViewController {

    public InjectedTableViewController(Pointer peer) {
        super(peer);
        ContractRApp.getComponent().inject(this);
    }

}
