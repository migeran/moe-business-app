package org.robovm.samples.contractr.ios.viewcontrollers;

import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.NInt;
import org.moe.natj.general.ann.Owned;
import org.moe.natj.general.ann.RegisterOnStartup;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCClassName;
import org.moe.natj.objc.ann.Property;
import org.moe.natj.objc.ann.Selector;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;

import javax.inject.Inject;

import apple.foundation.NSIndexPath;
import apple.uikit.UILabel;
import apple.uikit.UITableView;
import apple.uikit.UITableViewCell;

@org.moe.natj.general.ann.Runtime(ObjCRuntime.class)
@ObjCClassName("SelectTaskViewController")
@RegisterOnStartup
public class SelectTaskViewController extends InjectedTableViewController {

    @Inject
    ClientModel clientModel;
    @Inject
    TaskModel taskModel;

    @Owned
    @Selector("alloc")
    public static native SelectTaskViewController alloc();

    protected SelectTaskViewController(Pointer peer) {
        super(peer);
    }

    @org.moe.natj.general.ann.Runtime(ObjCRuntime.class)
    @ObjCClassName("SelectTaskCell")
    @RegisterOnStartup
    public static class SelectTaskCell extends UITableViewCell {

        @Owned
        @Selector("alloc")
        public static native SelectTaskCell alloc();

        protected SelectTaskCell(Pointer peer) {
            super(peer);
        }

        @Selector("titleLabel")
        @Property
        public native UILabel titleLabel();

        @Selector("timeElapsedLabel")
        @Property
        public native UILabel timeElapsedLabel();

    }

    @Selector("cancel")
    private void cancel(Object sender) {
        dismissViewControllerAnimatedCompletion(true, null);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        tableView().reloadData();
    }

    private Task getTaskForRow(NSIndexPath indexPath) {
        Client client = clientModel.get((int) indexPath.section());
        return taskModel.getForClient(client, true).get((int) indexPath.row());
    }

    @Override
    public void tableViewDidSelectRowAtIndexPath(UITableView tableView, NSIndexPath indexPath) {
        taskModel.startWork(getTaskForRow(indexPath));
        dismissViewControllerAnimatedCompletion(true, null);
    }

    @Override
    public UITableViewCell tableViewCellForRowAtIndexPath(UITableView tableView, NSIndexPath indexPath) {
        SelectTaskCell cell = (SelectTaskCell) tableView.dequeueReusableCellWithIdentifier("cell");
        Task task = getTaskForRow(indexPath);
        cell.titleLabel().setText(task.getTitle());
        cell.timeElapsedLabel().setText(task.getTimeElapsed());
        return cell;
    }

    @Override
    public String tableViewTitleForHeaderInSection(UITableView tableView, @NInt long section) {
        Client client = clientModel.get((int) section);
        return client.getName();
    }

    @Override
    public long numberOfSectionsInTableView(UITableView tableView) {
        return clientModel.count();
    }

    @Override
    public long tableViewNumberOfRowsInSection(UITableView tableView, @NInt long section) {
        Client client = clientModel.get((int) section);
        return taskModel.getForClient(client, true).size();
    }
}
