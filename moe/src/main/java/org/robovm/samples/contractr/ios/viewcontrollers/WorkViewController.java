package org.robovm.samples.contractr.ios.viewcontrollers;

import com.intel.moe.natj.general.Pointer;
import com.intel.moe.natj.general.ann.Owned;
import com.intel.moe.natj.general.ann.RegisterOnStartup;
import com.intel.moe.natj.objc.ObjCRuntime;
import com.intel.moe.natj.objc.ann.ObjCClassName;
import com.intel.moe.natj.objc.ann.Property;
import com.intel.moe.natj.objc.ann.Selector;

import net.engio.mbassy.listener.Handler;

import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;
import org.robovm.samples.contractr.core.TaskModel.WorkStartedEvent;
import org.robovm.samples.contractr.core.TaskModel.WorkStoppedEvent;
import org.robovm.samples.contractr.ios.IOSColors;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ios.c.Globals;
import ios.uikit.UIButton;
import ios.uikit.UIColor;
import ios.uikit.UILabel;
import ios.uikit.UIView;
import ios.uikit.enums.UIControlState;

@com.intel.moe.natj.general.ann.Runtime(ObjCRuntime.class)
@ObjCClassName("WorkViewController")
@RegisterOnStartup
public class WorkViewController extends InjectedViewController {

    @Inject
    ClientModel clientModel;
    @Inject
    TaskModel taskModel;

    private boolean showing = true;

    @Owned
    @Selector("alloc")
    public static native WorkViewController alloc();

    protected WorkViewController(Pointer peer) {
        super(peer);
    }

    @Handler
    public void workStarted(WorkStartedEvent event) {
        Globals.dispatch_async(Globals.dispatch_get_main_queue(), new Globals.Block_dispatch_async() {
                    @Override
                    public void call_dispatch_async() {
                        updateUIComponents();
                    }
                }
        );
    }

    @Handler
    public void workStopped(WorkStoppedEvent event) {
        Globals.dispatch_async(Globals.dispatch_get_main_queue(), new Globals.Block_dispatch_async() {
                    @Override
                    public void call_dispatch_async() {
                        updateUIComponents();
                        tick(); // Resets timer to 00:00:00
                    }
                }
        );
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        taskModel.subscribe(this);
        showing = true;
        updateUIComponents();
        tick();
    }

    @Override
    public void viewWillDisappear(boolean animated) {
        taskModel.unsubscribe(this);
        showing = false;
        super.viewWillDisappear(animated);
    }

    @Selector("startStopClicked")
    public void startStopClicked(UIButton sender) {
        Task workingTask = taskModel.getWorkingTask();
        if (workingTask == null) {
            performSegueWithIdentifierSender("selectTaskSegue", this);
        } else {
            taskModel.stopWork();
        }
    }

    private void updateUIComponents() {
        Task task = taskModel.getWorkingTask();
        UIColor startStopColor = null;
        String startStopTitle = null;
        String currentTaskText = null;
        if (task == null) {
            startStopTitle = "Start work";
            startStopColor = IOSColors.START_WORK;
            currentTaskText = "None";
            currentClientLabel().setHidden(true);
            currentClientColorView().setHidden(true);
        } else {
            startStopTitle = "Stop work";
            startStopColor = IOSColors.STOP_WORK;
            currentTaskText = task.getTitle();
            currentClientLabel().setText(task.getClient().getName());
            currentClientLabel().setHidden(false);
            currentClientColorView().setBackgroundColor(
                    IOSColors.getClientColor(clientModel.indexOf(task.getClient())));
            currentClientColorView().setHidden(false);
        }
        startStopButton().setTitleForState(startStopTitle, UIControlState.Normal);
        startStopButton().setBackgroundColor(startStopColor);
        currentTaskLabel().setText(currentTaskText);
    }

    private void tick() {
        if (!showing) {
            return;
        }
        Task task = taskModel.getWorkingTask();
        if (task != null) {
            timerLabel().setText(task.getTimeElapsed());
            earnedLabel().setText(task.getAmountEarned(Locale.US));
            Globals.dispatch_after(TimeUnit.SECONDS.toNanos(1), Globals.dispatch_get_main_queue(), new Globals.Block_dispatch_after() {
                @Override
                public void call_dispatch_after() {
                    tick();
                }
            });

        } else {
            timerLabel().setText("00:00:00");
            earnedLabel().setText(NumberFormat.getCurrencyInstance(Locale.US).format(0));
        }
    }

    @Selector("startStopButton")
    @Property
    public native UIButton startStopButton();

    @Selector("currentClientLabel")
    @Property
    public native UILabel currentClientLabel();

    @Selector("currentClientColorView")
    @Property
    public native UIView currentClientColorView();

    @Selector("currentTaskLabel")
    @Property
    public native UILabel currentTaskLabel();

    @Selector("earnedLabel")
    @Property
    public native UILabel earnedLabel();

    @Selector("timerLabel")
    @Property
    public native UILabel timerLabel();
}
