package org.robovm.samples.contractr.ios.viewcontrollers;

import com.intel.moe.natj.general.Pointer;
import com.intel.moe.natj.general.ann.Owned;
import com.intel.moe.natj.general.ann.RegisterOnStartup;
import com.intel.moe.natj.objc.ObjCRuntime;
import com.intel.moe.natj.objc.ann.ObjCClassName;
import com.intel.moe.natj.objc.ann.Selector;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;

import javax.inject.Inject;

import ios.foundation.NSArray;
import ios.foundation.NSIndexPath;
import ios.uikit.UITableView;
import ios.uikit.UITableViewCell;
import ios.uikit.enums.UITableViewCellAccessoryType;
import ios.uikit.enums.UITableViewCellStyle;
import ios.uikit.enums.UITableViewRowAnimation;

@com.intel.moe.natj.general.ann.Runtime(ObjCRuntime.class)
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
