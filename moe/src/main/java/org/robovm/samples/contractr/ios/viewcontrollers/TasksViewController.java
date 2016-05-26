package org.robovm.samples.contractr.ios.viewcontrollers;

import com.intel.moe.natj.general.Pointer;
import com.intel.moe.natj.general.ann.NInt;
import com.intel.moe.natj.general.ann.Owned;
import com.intel.moe.natj.general.ann.RegisterOnStartup;
import com.intel.moe.natj.objc.ObjCRuntime;
import com.intel.moe.natj.objc.ann.ObjCClassName;
import com.intel.moe.natj.objc.ann.Selector;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;

import javax.inject.Inject;

import ios.foundation.NSArray;
import ios.foundation.NSAttributedString;
import ios.foundation.NSDictionary;
import ios.foundation.NSIndexPath;
import ios.foundation.NSMutableDictionary;
import ios.foundation.NSNumber;
import ios.uikit.UITableView;
import ios.uikit.UITableViewCell;
import ios.uikit.c.UIKit;
import ios.uikit.enums.NSUnderlineStyle;
import ios.uikit.enums.UITableViewCellAccessoryType;
import ios.uikit.enums.UITableViewCellStyle;
import ios.uikit.enums.UITableViewRowAnimation;

@com.intel.moe.natj.general.ann.Runtime(ObjCRuntime.class)
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
