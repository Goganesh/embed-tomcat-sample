const chatSocket = new WebSocket("ws://localhost:8080/chat");

chatSocket.onmessage = function (event) {
    const userSessionId = getCookie("JSESSIONID");
    const message = jQuery.parseJSON(event.data);

    console.log(userSessionId)
    console.log(message.userId)
    console.log(message.time)

    if (userSessionId === message.userId) {
        addMyMessage(message.userId, message.time, message.text);
    } else {
        addOtherMessage(message.userId, message.time, message.text)
    }
};

function sendInputText() {
    const text = $('#input-text').val()
    chatSocket.send(text)
    $('#input-text').val("")
}

function addMyMessage(user, time, text) {
    $('#chat').append('<li class="clearfix"><div class="message other-message float-right">' + time + " " + user + " : " + text + '</div></li>');
}

function addOtherMessage(user, time, text) {
    $('#chat').append('<li class="clearfix"><div class="message my-message">' + time + " " + user + " : " + text + '</div></li>');
}

function getCookie(name) {
    console.log(document.cookie.length)
    let cookies = document.cookie.split(';');

    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i].trim();
        if (cookie.startsWith(name + '=')) {
            return cookie.substring(name.length + 1);
        }
    }

    return null;
}