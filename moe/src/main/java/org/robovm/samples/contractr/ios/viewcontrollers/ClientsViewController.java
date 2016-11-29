package org.robovm.samples.contractr.ios.viewcontrollers;

import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.Owned;
import org.moe.natj.general.ann.RegisterOnStartup;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCClassName;
import org.moe.natj.objc.ann.Selector;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;

import javax.inject.Inject;

import apple.foundation.NSArray;
import apple.foundation.NSIndexPath;
import apple.uikit.UITableView;
import apple.uikit.UITableViewCell;
import apple.uikit.enums.UITableViewCellAccessoryType;
import apple.uikit.enums.UITableViewCellStyle;
import apple.uikit.enums.UITableViewRowAnimation;

@org.moe.natj.general.ann.Runtime(ObjCRuntime.class)
@ObjCClassName("ClientsViewController")
@RegisterOnStartup
public class ClientsViewController extends ListViewController {

    @Inject
    ClientModel clientModel;

    @Owned
    @Selector("alloc")
    public static native ClientsViewController alloc();

    protected ClientsViewController(Pointer peer) {
        super(peer);
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        navigationItem().setTitle("Clients");
    }

    @Override
    protected void onAdd() {
        clientModel.selectClient(null);
        performSegueWithIdentifierSender("editClientSegue", this);
    }

    @Override
    protected void onEdit(int section, int row) {
        clientModel.selectClient(clientModel.get(row));
        performSegueWithIdentifierSender("editClientSegue", this);
    }

    @Override
    protected void onDelete(int section, int row) {
        Client client = clientModel.get(row);
        clientModel.delete(client);
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
        Client client = clientModel.get((int) indexPath.row());
        cell.textLabel().setText(client.getName());
        return cell;
    }

    @Override
    public long tableViewNumberOfRowsInSection(UITableView tableView, long section) {
        return clientModel.count();
    }
}
