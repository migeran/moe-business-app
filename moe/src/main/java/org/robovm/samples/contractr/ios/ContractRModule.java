package org.robovm.samples.contractr.ios;

import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.TaskModel;
import org.robovm.samples.contractr.core.service.ClientManager;
import org.robovm.samples.contractr.core.service.ConnectionPool;
import org.robovm.samples.contractr.core.service.JdbcClientManager;
import org.robovm.samples.contractr.core.service.JdbcTaskManager;
import org.robovm.samples.contractr.core.service.SingletonConnectionPool;
import org.robovm.samples.contractr.core.service.TaskManager;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ios.foundation.NSFileManager;
import ios.foundation.NSURL;
import ios.foundation.enums.NSSearchPathDirectory;
import ios.foundation.enums.NSSearchPathDomainMask;

/**
 * Dagger {@link Module} that configures the iOS version of the ContractR app.
 */
@Module
public class ContractRModule {

    /**
     * Whether dummy data should be preloaded into new databases.
     */
    public static final boolean PRELOAD_DATA = true;

    @Provides
    @Singleton
    public ConnectionPool proivdeConnectionPool() {
        try {
            Class.forName("SQLite.JDBCDriver");
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }

        /*
         * The SQLite database is kept in
         * <Application_Home>/Documents/db.sqlite. This directory is backed up
         * by iTunes. See http://goo.gl/BWlCGN for Apple's docs on the iOS file
         * system.
         */
        File dbFile = new File(System.getenv("HOME"), "Documents/db.sqlite");
        dbFile.getParentFile().mkdirs();
        //Foundation.NSLog("Using db in file: " + dbFile.getAbsolutePath());

        if (applicationDocumentsDirectory().endsWith("data/Documents")) {
            return new SingletonConnectionPool(
                    "jdbc:sqlite:" + "/Documents/db.sqlite");
        } else {
            return new SingletonConnectionPool(
                    "jdbc:sqlite:" + dbFile.getAbsolutePath());
        }
    }

    @Provides
    @Singleton
    public ClientManager proivdeClientManager(ConnectionPool connectionPool) {
        return new JdbcClientManager(connectionPool, PRELOAD_DATA);
    }

    @Provides
    @Singleton
    public TaskManager proivdeTaskManager(ConnectionPool connectionPool, ClientManager clientManager) {
        JdbcTaskManager taskManager = new JdbcTaskManager(connectionPool, PRELOAD_DATA);
        taskManager.setClientManager((JdbcClientManager) clientManager);
        ((JdbcClientManager) clientManager).setTaskManager(taskManager);
        return taskManager;
    }

    @Provides
    @Singleton
    public ClientModel proivdeClientModel(ClientManager clientManager, TaskManager taskManager) {
        return new ClientModel(clientManager, taskManager);
    }

    @Provides
    @Singleton
    public TaskModel proivdeTaskModel(ClientModel clientModel, TaskManager taskManager) {
        return new TaskModel(clientModel, taskManager);
    }

    public String applicationDocumentsDirectory() {
        return ((NSURL) NSFileManager.defaultManager().URLsForDirectoryInDomains(NSSearchPathDirectory.DocumentDirectory, NSSearchPathDomainMask.UserDomainMask).firstObject()).fileSystemRepresentation();
    }
}
