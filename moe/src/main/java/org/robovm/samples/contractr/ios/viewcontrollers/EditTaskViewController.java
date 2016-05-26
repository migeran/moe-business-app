package org.robovm.samples.contractr.ios.viewcontrollers;

import com.intel.moe.natj.general.Pointer;
import com.intel.moe.natj.general.ann.NInt;
import com.intel.moe.natj.general.ann.Owned;
import com.intel.moe.natj.general.ann.RegisterOnStartup;
import com.intel.moe.natj.objc.ObjCRuntime;
import com.intel.moe.natj.objc.ann.ObjCClassName;
import com.intel.moe.natj.objc.ann.Property;
import com.intel.moe.natj.objc.ann.Selector;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;

import javax.inject.Inject;

import ios.foundation.NSIndexPath;
import ios.uikit.UIPickerView;
import ios.uikit.UISwitch;
import ios.uikit.UITableView;
import ios.uikit.UITextField;
import ios.uikit.protocol.UIPickerViewDataSource;
import ios.uikit.protocol.UIPickerViewDelegate;

@com.intel.moe.natj.general.ann.Runtime(ObjCRuntime.class)
@ObjCClassName("EditTaskViewController")
@RegisterOnStartup
public class EditTaskViewController extends InjectedTableViewController implements UIPickerViewDataSource, UIPickerViewDelegate {

    @Inject
    ClientModel clientModel;
    @Inject
    TaskModel taskModel;

    @Owned
    @Selector("alloc")
    public static native EditTaskViewController alloc();

    protected EditTaskViewController(Pointer peer) {
        super(peer);
    }

    private static final int CLIENT_PICKER_ROW = 1;
    private static final int CLIENT_PICKER_CELL_HEIGHT = 219;

    private boolean clientPickerShowing = false;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        clientPicker().setDataSource(this);
        clientPicker().setDelegate(this);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        showHideClientPicker(false);

        Task task = taskModel.getSelectedTask();
        if (task == null) {
            navigationItem().setTitle("Add task");
        } else {
            navigationItem().setTitle("Edit task");
        }
        updateViewValuesWithTask(task);
    }

    @Selector("save")
    private void save(Object sender) {
        Task task = taskModel.getSelectedTask();
        if (task == null) {
            Client client = getSelectedClient();
            taskModel.save(saveViewValuesToTask(taskModel.create(client)));
        } else {
            taskModel.save(saveViewValuesToTask(task));
        }
        navigationController().popViewControllerAnimated(true);
    }

    private Client getSelectedClient() {
        if (clientPicker().selectedRowInComponent(0) == 0) {
            return null;
        }
        return clientModel.get((int) clientPicker().selectedRowInComponent(0) - 1);
    }

    @Selector("updateSaveButtonEnabled")
    private void updateSaveButtonEnabled(Object sender) {
        Client client = getSelectedClient();
        String title = titleTextField().text();
        title = title == null ? "" : title.trim();
        boolean canSave = !title.isEmpty() && client != null;
        navigationItem().rightBarButtonItem().setEnabled(canSave);
    }

    @Selector("hideClientPicker")
    public void hideClientPicker(Object sender) {
        showHideClientPicker(false);
    }

    private void showHideClientPicker(boolean show) {
        clientPickerShowing = show;
        clientPicker().setHidden(!clientPickerShowing);
        // Calling beginUpdate() / endUpdates() animates the table view nicely.
        tableView().beginUpdates();
        tableView().endUpdates();
        if (show) {
            view().endEditing(false);
        }
    }

    protected void updateViewValuesWithTask(Task task) {
        Client client = task == null ? null : task.getClient();
        int selectedRow = 0;
        if (client != null) {
            for (int i = 0; i < clientModel.count(); i++) {
                if (clientModel.get(i).equals(client)) {
                    selectedRow = i + 1;
                    break;
                }
            }
        }
        clientPicker().selectRowInComponentAnimated(selectedRow, 0, false);
        clientTextField().setText(task == null ? "" : task.getClient().getName());
        titleTextField().setText(task == null ? "" : task.getTitle());
        notesTextField().setText(task == null ? "" : task.getNotes());
        finishedSwitch().setOn(task == null ? false : task.isFinished());
        updateSaveButtonEnabled(null);
    }

    protected Task saveViewValuesToTask(Task task) {
        String title = titleTextField().text();
        title = title == null ? "" : title.trim();
        String notes = notesTextField().text();
        notes = notes == null ? "" : notes.trim();

        Client client = getSelectedClient();
        task.setClient(client);
        task.setTitle(title);
        task.setNotes(notes);
        task.setFinished(finishedSwitch().isOn());

        return task;
    }

    @Override
    public void tableViewDidSelectRowAtIndexPath(UITableView tableView, NSIndexPath indexPath) {
        if (indexPath.row() == CLIENT_PICKER_ROW) {
            // Client picker row selected. Ignore.
        } else if (indexPath.row() == CLIENT_PICKER_ROW - 1) {
            // Client text field row selected. Show client picker.
            showHideClientPicker(true);
            tableView().deselectRowAtIndexPathAnimated(indexPath, false);
        } else {
            // Some other row selected. Hide the client picker.
            showHideClientPicker(false);
        }
    }

    @Override
    public double tableViewHeightForRowAtIndexPath(UITableView tableView, NSIndexPath indexPath) {
        if (indexPath.row() == CLIENT_PICKER_ROW) {
            return clientPickerShowing ? CLIENT_PICKER_CELL_HEIGHT : 0;
        }
        return super.tableViewHeightForRowAtIndexPath(tableView, indexPath);
    }

    @Override
    public long numberOfComponentsInPickerView(UIPickerView uiPickerView) {
        return 1;
    }

    @Override
    public long pickerViewNumberOfRowsInComponent(UIPickerView uiPickerView, @NInt long l) {
        return clientModel.count() + 1;
    }

    @Override
    public String pickerViewTitleForRowForComponent(UIPickerView pickerView, @NInt long row, @NInt long component) {
        return row == 0 ? "" : clientModel.get((int) row - 1).getName();
    }

    @Override
    public void pickerViewDidSelectRowInComponent(UIPickerView pickerView, @NInt long row, @NInt long component) {
        Client client = getSelectedClient();
        if (client != null) {
            clientTextField().setText(client.getName());
        } else {
            clientTextField().setText("");
        }
        showHideClientPicker(false);
        updateSaveButtonEnabled(null);
    }

    @Selector("clientPicker")
    @Property
    public native UIPickerView clientPicker();

    @Selector("clientTextField")
    @Property
    public native UITextField clientTextField();

    @Selector("titleTextField")
    @Property
    public native UITextField titleTextField();

    @Selector("notesTextField")
    @Property
    public native UITextField notesTextField();

    @Selector("finishedSwitch")
    @Property
    public native UISwitch finishedSwitch();
}
