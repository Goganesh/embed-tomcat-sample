package com.example.web;

import com.example.model.UserMessage;
import com.example.repository.UserMessageRepository;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/chat"}, name = "ChatServlet")
public class ChatServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        try {
            var userMessageRepository = new UserMessageRepository();
            var messages = userMessageRepository.findAll()
                    .stream()
                    .sorted(Comparator.comparing(UserMessage::getText))
                    .collect(Collectors.toList());

            req.setAttribute("messages", messages);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        var jsessionid =
                Arrays.stream(req.getCookies())
                        .filter(cookie -> cookie.getName().equals("JSESSIONID"))
                        .findFirst();

        req.setAttribute("userSessionId", jsessionid.get().getValue());

        req.getRequestDispatcher("/jsp/chat.jsp").forward(req, resp);
    }

}
