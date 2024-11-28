package com.example.web;

import com.example.model.UserMessage;
import com.example.repository.UserMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.tomcat.websocket.WsSession;

import javax.naming.NamingException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatWsEndpoint {

    private static final Queue<Session> QUEUE = new ConcurrentLinkedQueue<>();

    @OnOpen
    public void onOpen(final Session session) throws IOException {
        QUEUE.add(session);
    }

    @OnMessage
    public void onMessage(final Session session, final String msg) throws IOException, NamingException, SQLException {
        var sessionId = ((WsSession) session).getHttpSessionId();

        var userMessageRepository = new UserMessageRepository();

        var message = new UserMessage();
        var id = UUID.randomUUID().toString();
        message.setId(id);
        message.setUserId(sessionId);
        message.setText(msg);
        userMessageRepository.add(message);

        var saved = userMessageRepository.findById(id);
        sendToAll(saved);
    }

    @OnClose
    public void onClose(final Session session) {
        QUEUE.remove(session);
    }

    @OnError
    public void onError(final Session session, final Throwable throwable) {
        Logger.getLogger(ChatWsEndpoint.class.getName())
                .log(Level.SEVERE, "Error, session: " + session.getId(), throwable);
        QUEUE.remove(session);
    }

    public static void sendToAll(UserMessage message) throws IOException {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ArrayList<Session> closedSessions = new ArrayList<>();
        for (Session session : QUEUE) {
            if (!session.isOpen()) {
                closedSessions.add(session);
            } else {
                session.getBasicRemote().sendText(mapper.writeValueAsString(message));
            }
        }
        QUEUE.removeAll(closedSessions);
    }

}
