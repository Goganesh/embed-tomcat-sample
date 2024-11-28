<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Chat</title>
</head>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
<link rel="stylesheet" href="/css/app.css"/>

<div class="container">
    <div class="row clearfix">
        <div class="col-lg-12">
            <div class="card chat-app">
                <div class="chat">
                    <div class="chat-history">
                        <ul id="chat" class="m-b-0">
                            <c:forEach var="message" items="${messages}">
                                <c:if test="${message.userId == userSessionId}">
                                    <li class="clearfix">
                                        <div class="message other-message float-right">${message.time} ${message.userId} : ${message.text}</div>
                                    </li>
                                </c:if>
                                <c:if test="${message.userId != userSessionId}">
                                    <li class="clearfix">
                                        <div class="message my-message">${message.time} ${message.userId} : ${message.text}</div>
                                    </li>
                                </c:if>
                            </c:forEach>
                        </ul>
                    </div>
                    <div class="chat-message clearfix">
                        <div class="input-group mb-3">
                            <input id="input-text" type="text" class="form-control" placeholder="Enter text here..."
                                   aria-label="Enter text here..." aria-describedby="basic-addon2">
                            <div class="input-group-append">
                                <button onclick="sendInputText()" class="btn btn-outline-secondary" type="button">
                                    Send
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

</div>

<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="/js/app.js"></script>
</body>

</html>