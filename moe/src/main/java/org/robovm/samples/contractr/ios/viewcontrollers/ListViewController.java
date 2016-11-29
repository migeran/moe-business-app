package org.robovm.samples.contractr.ios.viewcontrollers;

import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.NInt;
import org.moe.natj.general.ann.Owned;
import org.moe.natj.general.ann.RegisterOnStartup;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCClassName;
import org.moe.natj.objc.ann.Property;
import org.moe.natj.objc.ann.Selector;

import apple.foundation.NSArray;
import apple.foundation.NSIndexPath;
import apple.uikit.UIBarButtonItem;
import apple.uikit.UITableView;
import apple.uikit.enums.UITableViewCellEditingStyle;

/**
 * Abstract {@link ListViewController} which displays a list of objects and
 * supports adding and removing objects.
 */
@org.moe.natj.general.ann.Runtime(ObjCRuntime.class)
@ObjCClassName("ListViewController")
@RegisterOnStartup
public abstract class ListViewController extends InjectedTableViewController {

    @Owned
    @Selector("alloc")
    public static native ListViewController alloc();

    protected ListViewController(Pointer peer) {
        super(peer);
    }

    protected UIBarButtonItem doneBarButtonItem;
    protected UIBarButtonItem editBarButtonItem;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        this.doneBarButtonItem = doneBarButtonItem();
        this.editBarButtonItem = editBarButtonItem();
    }

    @Override
    public void setEditingAnimated(boolean editing, boolean animated) {
        navigationItem().setLeftBarButtonItems((NSArray<? extends UIBarButtonItem>) NSArray.alloc().init());
        if (editing) {
            navigationItem().setLeftBarButtonItem(doneBarButtonItem);
        } else {
            navigationItem().setLeftBarButtonItem(editBarButtonItem);
        }
        super.setEditingAnimated(editing, animated);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        tableView().reloadData();
        setEditing(false);
    }

    protected abstract void onAdd();

    protected abstract void onEdit(int section, int row);

    protected abstract void onDelete(int section, int row);

    @Override
    public void tableViewDidSelectRowAtIndexPath(UITableView tableView, NSIndexPath indexPath) {
        onEdit((int) indexPath.section(), (int) indexPath.row());
    }

    @Override
    public long numberOfSectionsInTableView(UITableView tableView) {
        return 1;
    }

    @Override
    public void tableViewCommitEditingStyleForRowAtIndexPath(UITableView tableView, @NInt long editingStyle,
                                                             NSIndexPath indexPath) {

        if (editingStyle == UITableViewCellEditingStyle.Delete) {
            onDelete((int) indexPath.section(), (int) indexPath.row());
        }
    }

    @Selector("add")
    public void add(UIBarButtonItem sender) {
        onAdd();
    }

    @Selector("edit")
    public void edit(UIBarButtonItem sender) {
        setEditingAnimated(true, true);
    }

    @Selector("done")
    public void done(UIBarButtonItem sender) {
        setEditingAnimated(false, true);
    }

    @Selector("editBarButtonItem")
    @Property
    public native UIBarButtonItem editBarButtonItem();

    @Selector("doneBarButtonItem")
    @Property
    public native UIBarButtonItem doneBarButtonItem();
}
