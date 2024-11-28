package com.example.web;

import javax.servlet.ServletContextEvent;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.tomcat.websocket.server.Constants;
import org.apache.tomcat.websocket.server.WsContextListener;

public class ContextListener extends WsContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        super.contextInitialized(sce);

        final ServerContainer sc =
                (ServerContainer) sce.getServletContext().getAttribute(
                        Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE);
        try {
            sc.addEndpoint(ServerEndpointConfig.Builder.create(ChatWsEndpoint.class, "/chat").build());
        } catch (final DeploymentException e) {
            throw new IllegalStateException(e);
        }
    }
}
