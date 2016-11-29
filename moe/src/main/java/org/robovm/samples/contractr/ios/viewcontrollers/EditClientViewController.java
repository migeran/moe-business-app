package org.robovm.samples.contractr.ios.viewcontrollers;

import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.Owned;
import org.moe.natj.general.ann.RegisterOnStartup;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCClassName;
import org.moe.natj.objc.ann.Property;
import org.moe.natj.objc.ann.Selector;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.ios.views.CurrencyTextField;

import java.math.BigDecimal;

import javax.inject.Inject;

import apple.uikit.UITextField;

@org.moe.natj.general.ann.Runtime(ObjCRuntime.class)
@ObjCClassName("EditClientViewController")
@RegisterOnStartup
public class EditClientViewController extends InjectedTableViewController {

    @Inject
    ClientModel clientModel;

    @Owned
    @Selector("alloc")
    public static native EditClientViewController alloc();

    protected EditClientViewController(Pointer peer) {
        super(peer);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        Client client = clientModel.getSelectedClient();
        if (client == null) {
            navigationItem().setTitle("Add client");
            updateViewValuesWithClient(null);
        } else {
            navigationItem().setTitle("Edit client");
            updateViewValuesWithClient(client);
        }
    }

    @Selector("save")
    private void save(Object sender) {
        Client client = clientModel.getSelectedClient();
        if (client == null) {
            clientModel.save(saveViewValuesToClient(clientModel.create()));
        } else {
            clientModel.save(saveViewValuesToClient(client));
        }
        navigationController().popViewControllerAnimated(true);
    }

    @Selector("nameChanged")
    private void nameChanged(Object sender) {
        String name = nameTextField().text();
        name = name == null ? "" : name.trim();
        navigationItem().rightBarButtonItem().setEnabled(!name.isEmpty());
    }

    @Override
    public void viewWillDisappear(boolean animated) {
        nameTextField().resignFirstResponder();
        hourlyRateTextField().resignFirstResponder();

        super.viewWillDisappear(animated);
    }

    private void updateViewValuesWithClient(Client client) {
        nameTextField().setText(client == null ? "" : client.getName());
        hourlyRateTextField().setAmount(client == null ? BigDecimal.ZERO : client.getHourlyRate());
        nameChanged(null);
    }

    private Client saveViewValuesToClient(Client client) {
        String name = nameTextField().text();
        name = name == null ? "" : name.trim();

        client.setName(name);
        client.setHourlyRate(hourlyRateTextField().getAmount());

        return client;
    }

    @Selector("nameTextField")
    @Property
    public native UITextField nameTextField();

    @Selector("hourlyRateTextField")
    @Property
    public native CurrencyTextField hourlyRateTextField();
}
