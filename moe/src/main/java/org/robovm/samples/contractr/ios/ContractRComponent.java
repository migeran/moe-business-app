package org.robovm.samples.contractr.ios;

import org.robovm.samples.contractr.ios.viewcontrollers.ClientsViewController;
import org.robovm.samples.contractr.ios.viewcontrollers.EditClientViewController;
import org.robovm.samples.contractr.ios.viewcontrollers.EditTaskViewController;
import org.robovm.samples.contractr.ios.viewcontrollers.ReportsViewController;
import org.robovm.samples.contractr.ios.viewcontrollers.SelectTaskViewController;
import org.robovm.samples.contractr.ios.viewcontrollers.TasksViewController;
import org.robovm.samples.contractr.ios.viewcontrollers.WorkViewController;

import java.lang.reflect.Method;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger {@link Component}. This needs an {@code inject(...)} for each type of object that will be dependency injected.
 */
@Singleton
@Component(modules = ContractRModule.class)
public interface ContractRComponent {
    void inject(ClientsViewController controller);

    void inject(EditClientViewController controller);

    void inject(EditTaskViewController controller);

    void inject(ReportsViewController controller);

    void inject(SelectTaskViewController controller);

    void inject(TasksViewController controller);

    void inject(WorkViewController controller);

    default void inject(Object o) {
        try {
            for (Class<?> c = o.getClass(); c != Object.class; c = c.getSuperclass()) {
                try {
                    Method m = ContractRComponent.class.getMethod("inject", c);
                    m.invoke(this, o);
                    return;
                } catch (NoSuchMethodException e) {
                    // Just try the next ancestor class
                }
            }
        } catch (Throwable t) {
            throw new Error("Failed to inject dependencies into a " + o.getClass().getName(), t);
        }
        // If we end up here it means no inject(...) method was found.
        throw new Error("Failed to inject dependencies into a " + o.getClass().getName()
                + ". No " + ContractRComponent.class.getName() + ".inject(...) method found for "
                + "it or any of its ancestor classes.");
    }
}
