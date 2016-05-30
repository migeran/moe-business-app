package org.robovm.samples.contractr.android;

import android.app.Application;
import android.graphics.Color;
import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.TaskModel;
import org.robovm.samples.contractr.core.service.JdbcClientManager;
import org.robovm.samples.contractr.core.service.JdbcTaskManager;
import org.robovm.samples.contractr.core.service.SingletonConnectionPool;
import roboguice.RoboGuice;

import java.io.File;

public class ContractRApplication extends Application {
    /**
     * Whether dummy data should be preloaded into new databases.
     */
    public static final boolean PRELOAD_DATA = true;

    private ClientModel clientModel;
    private TaskModel taskModel;

    @Override
    public void onCreate() {
        /*
         * Initialize the models. The SQLite database is kept in
         * The private are of the app.
         */
        try {
            Class.forName("org.sqldroid.SQLDroidDriver");
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        File dir = getFilesDir();
        File dbFile = new File(dir, "ContractR-db.sqlite");

        SingletonConnectionPool connectionPool = new SingletonConnectionPool(
                "jdbc:sqldroid:" + dbFile.getAbsolutePath());
        JdbcClientManager clientManager = new JdbcClientManager(connectionPool, PRELOAD_DATA);
        JdbcTaskManager taskManager = new JdbcTaskManager(connectionPool, PRELOAD_DATA);
        clientManager.setTaskManager(taskManager);
        taskManager.setClientManager(clientManager);
        clientModel = new ClientModel(clientManager, taskManager);
        taskModel = new TaskModel(clientModel, taskManager);

        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                Modules.override(RoboGuice.newDefaultRoboModule(this)).with(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ClientModel.class).toInstance(clientModel);
                        bind(TaskModel.class).toInstance(taskModel);
                    }
                }));

        super.onCreate();
    }
}
