package org.robovm.samples.contractr.ios.viewcontrollers;

import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.NInt;
import org.moe.natj.general.ann.Owned;
import org.moe.natj.general.ann.RegisterOnStartup;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCClassName;
import org.moe.natj.objc.ann.Selector;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;

import javax.inject.Inject;

import apple.foundation.NSArray;
import apple.foundation.NSAttributedString;
import apple.foundation.NSDictionary;
import apple.foundation.NSIndexPath;
import apple.foundation.NSMutableDictionary;
import apple.foundation.NSNumber;
import apple.uikit.UITableView;
import apple.uikit.UITableViewCell;
import apple.uikit.c.UIKit;
import apple.uikit.enums.NSUnderlineStyle;
import apple.uikit.enums.UITableViewCellAccessoryType;
import apple.uikit.enums.UITableViewCellStyle;
import apple.uikit.enums.UITableViewRowAnimation;

@org.moe.natj.general.ann.Runtime(ObjCRuntime.class)
@ObjCClassName("TasksViewController")
@RegisterOnStartup
public class TasksViewController extends ListViewController {

    private static final NSDictionary<String, Object> strikeThroughAttrs;

    static {
        strikeThroughAttrs = (NSDictionary<String, Object>) NSMutableDictionary.dictionary();
        strikeThroughAttrs.put(UIKit.NSStrikethroughStyleAttributeName(), NSNumber.numberWithInteger(NSUnderlineStyle.StyleSingle));
    }

    @Inject
    ClientModel clientModel;
    @Inject
    TaskModel taskModel;

    @Owned
    @Selector("alloc")
    public static native TasksViewController alloc();

    protected TasksViewController(Pointer peer) {
        super(peer);
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        navigationItem().setTitle("Tasks");
    }

    @Override
    protected void onAdd() {
        clientModel.selectClient(null);
        taskModel.selectTask(null);
        performSegueWithIdentifierSender("editTaskSegue", this);
    }

    @Override
    protected void onEdit(int section, int row) {
        Client client = clientModel.get(section);
        Task task = taskModel.getForClient(client, false).get(row);
        clientModel.selectClient(client);
        taskModel.selectTask(task);
        performSegueWithIdentifierSender("editTaskSegue", this);
    }

    @Override
    protected void onDelete(int section, int row) {
        Client client = clientModel.get(section);
        Task task = taskModel.getForClient(client, false).get(row);
        taskModel.delete(task);
        tableView().deleteRowsAtIndexPathsWithRowAnimation(
                (NSArray<? extends NSIndexPath>) NSArray.arrayWithObject(NSIndexPath.indexPathForRowInSection(row, section)),
                UITableViewRowAnimation.Automatic);
    }

    @Override
    public UITableViewCell tableViewCellForRowAtIndexPath(UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell = tableView.dequeueReusableCellWithIdentifier("cell");
        if (cell == null) {
            cell = UITableViewCell.alloc().initWithStyleReuseIdentifier(UITableViewCellStyle.Value1, "cell");
            cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
        }
        Client client = clientModel.get((int) indexPath.section());
        Task task = taskModel.getForClient(client, false).get((int) indexPath.row());
        String title = task.getTitle();
        if (task.isFinished()) {
            NSAttributedString attributedTitle = NSAttributedString.alloc().initWithStringAttributes(title, strikeThroughAttrs);
            cell.textLabel().setAttributedText(attributedTitle);
        } else {
            cell.textLabel().setText(title);
        }
        cell.detailTextLabel().setText(task.getNotes());
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
        return taskModel.getForClient(client, false).size();
    }

}
