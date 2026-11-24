package com.vaultify;

import com.vaultify.config.AppConfig;
import com.vaultify.config.Database;
import com.vaultify.config.SchemaRunner;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    private AppContext app;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        AppConfig config = new AppConfig();
        Database database = new Database(config);
        try {
            new SchemaRunner(database).run();
            this.app = new AppContext(database, config.appKey());
        } catch (RuntimeException e) {
            database.close();
            throw e;
        }
        event.getServletContext().setAttribute(AppContext.ATTRIBUTE, app);
        event.getServletContext().log("Vaultify initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (app != null) {
            app.database().close();
        }
    }
}
