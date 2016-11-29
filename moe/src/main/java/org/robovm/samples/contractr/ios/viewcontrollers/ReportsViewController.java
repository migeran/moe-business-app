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
import org.robovm.samples.contractr.ios.IOSColors;
import org.robovm.samples.contractr.ios.views.PieChartView;
import org.robovm.samples.contractr.ios.views.PieChartView.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import apple.NSObject;
import apple.c.Globals;
import apple.foundation.NSIndexPath;
import apple.uikit.UIColor;
import apple.uikit.UILabel;
import apple.uikit.UISegmentedControl;
import apple.uikit.UITableView;
import apple.uikit.UITableViewCell;
import apple.uikit.UIView;
import apple.uikit.protocol.UITableViewDataSource;

@org.moe.natj.general.ann.Runtime(ObjCRuntime.class)
@ObjCClassName("ReportsViewController")
@RegisterOnStartup
public class ReportsViewController extends InjectedViewController implements UITableViewDataSource {

    @Inject
    ClientModel clientModel;

    private boolean showing = true;

    @Owned
    @Selector("alloc")
    public static native ReportsViewController alloc();

    protected ReportsViewController(Pointer peer) {
        super(peer);
    }

    @Override
    public UITableViewCell tableViewCellForRowAtIndexPath(UITableView tableView, NSIndexPath indexPath) {
        boolean showAmount = typeSegmentedControl().selectedSegmentIndex() == 0;
        ReportsClientCell cell = (ReportsClientCell) tableView.dequeueReusableCellWithIdentifier("cell");
        Client client = clientModel.get((int) indexPath.row());
        cell.colorView().setBackgroundColor(IOSColors.getClientColor(clientModel.indexOf(client)));
        cell.nameLabel().setText(client.getName());
        if (showAmount) {
            cell.valueLabel().setText(clientModel.getTotalAmountEarned(client, Locale.US));
        } else {
            cell.valueLabel().setText(clientModel.getTotalTimeElapsed(client));
        }
        return cell;
    }

    @Override
    public long tableViewNumberOfRowsInSection(UITableView uiTableView, @NInt long l) {
        return clientModel.count();
    }

    @Override
    public long numberOfSectionsInTableView(UITableView tableView) {
        return 1;
    }

    @org.moe.natj.general.ann.Runtime(ObjCRuntime.class)
    @ObjCClassName("ReportsClientCell")
    @RegisterOnStartup
    public static class ReportsClientCell extends UITableViewCell {

        public static native ReportsClientCell alloc();

        protected ReportsClientCell(Pointer peer) {
            super(peer);
        }

        @Selector("colorView")
        @Property
        public native UIView colorView();

        @Selector("nameLabel")
        @Property
        public native UILabel nameLabel();

        @Selector("valueLabel")
        @Property
        public native UILabel valueLabel();
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        clientsTableView().setDataSource(this);
    }

    @Override
    public void viewWillAppear(boolean b) {
        super.viewWillAppear(b);

        showing = true;
        loop();
    }

    @Override
    public void viewWillDisappear(boolean b) {
        showing = false;
        super.viewWillDisappear(b);
    }

    @Selector("chartTypeChanged")
    public void chartTypeChanged(NSObject sender) {
        updatePieChart();
        clientsTableView().reloadData();
    }

    private void loop() {
        if (showing) {
            updatePieChart();
            clientsTableView().reloadData();
            Globals.dispatch_after(TimeUnit.SECONDS.toNanos(1), Globals.dispatch_get_main_queue(), new Globals.Block_dispatch_after() {
                @Override
                public void call_dispatch_after() {
                    loop();
                }
            });
        }
    }

    private void updatePieChart() {
        boolean showAmount = typeSegmentedControl().selectedSegmentIndex() == 0;

        List<Component> components = new ArrayList<>();
        for (int i = 0; i < clientModel.count(); i++) {
            Client client = clientModel.get(i);
            UIColor color = IOSColors.getClientColor(i);
            if (showAmount) {
                components.add(new Component(color,
                        clientModel.getTotalAmountEarned(client).doubleValue()));
            } else {
                components.add(new Component(color,
                        clientModel.getTotalSecondsElapsed(client)));
            }
        }
        pieChart().setComponents(components);
    }

    @Selector("pieChart")
    @Property
    public native PieChartView pieChart();

    @Selector("clientsTableView")
    @Property
    public native UITableView clientsTableView();

    @Selector("typeSegmentedControl")
    @Property
    public native UISegmentedControl typeSegmentedControl();

}
