package com.example;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.example.web.ContextListener;
import com.example.web.CookieProcessorWrapper;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.JarResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.flywaydb.core.Flyway;

public class Main {

    private final static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {
        Config config = ConfigFactory.load();
        Config server = config.getConfig("server");
        Config app = config.getConfig("app");
        Config dataSource = config.getConfig("data-source");
        migrateDb(dataSource);

        Tomcat tomcat = new Tomcat();

        Context ctx = prepareTomcat(tomcat, server, app);
        prepareWebResource(ctx);
        prepareJndiResource(ctx, dataSource);

        tomcat.start();

        tomcat.getServer().await();

    }

    private static Context prepareTomcat(Tomcat tomcat, Config server, Config app) throws ServletException {
        String appBase = ".";
        tomcat.setPort(server.getInt("port"));
        tomcat.setHostname(server.getString("host"));
        tomcat.getHost().setAppBase(appBase);
        tomcat.enableNaming();

        return tomcat.addWebapp(app.getString("context-path"), appBase);
    }

    private static void prepareJndiResource(Context ctx, Config dataSource) {
        ContextResource resource = new ContextResource();
        resource.setName("jdbc/db");
        resource.setAuth("Container");
        resource.setType("javax.sql.DataSource");
        resource.setProperty("driverClassName", dataSource.getString("driver-class-name"));
        resource.setProperty("url", dataSource.getString("url"));
        resource.setProperty("username", dataSource.getString("username"));
        resource.setProperty("password", dataSource.getString("password"));

        ctx.getNamingResources().addResource(resource);
    }

    private static void prepareWebResource(Context ctx) {
        WebResourceRoot resources = new StandardRoot(ctx);

        WebResourceSet wrs = null;

        URL main = Main.class.getResource("Main.class");
        if ("jar".equalsIgnoreCase(main.getProtocol())) {
            String path = main.getPath();
            wrs = new JarResourceSet(resources, "/WEB-INF/classes",
                    path.substring(path.indexOf(':') + 1, path.indexOf('!')), "/");
        } else if ("file".equalsIgnoreCase(main.getProtocol())) {
            Path path = Paths.get(main.getFile()).getParent().getParent();
            wrs = new DirResourceSet(resources, "/WEB-INF/classes", path.toFile().getAbsolutePath(), "/");
        } else {
            throw new IllegalStateException("Main class is not stored in a jar file or file system.");
        }
        resources.addPreResources(wrs);
        ctx.setResources(resources);

        ctx.addApplicationListener(ContextListener.class.getName());
        ctx.setCookieProcessor(new CookieProcessorWrapper());
    }

    private static void migrateDb(Config dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource.getString("url"), dataSource.getString("username"),
                dataSource.getString("password"));
        flyway.migrate();
    }
}
