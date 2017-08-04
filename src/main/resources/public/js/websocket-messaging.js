/**
 * Created by olatunji on 8/1/17.
 */

var CONFIG={};
$.get('js/config.json', function(response) {
    CONFIG = response;
}, "json");

//Establish the WebSocket connection and set up event handlers
var chatWebSocket = null;
function openWebSocket(){
    if(chatWebSocket == null || (chatWebSocket != null && chatWebSocket.readyState === WebSocket.CLOSED)){
        chatWebSocket =  new WebSocket(CONFIG.socket.uri.base + CONFIG.socket.uri.chat);
        chatWebSocket.onmessage = function (msg) {
            updateChat(msg);
        };
        chatWebSocket.onclose = function () {
            alert("WebSocket connection closed")
        };
    }
}

//Send a message if it's not empty, then clear the input field
function sendWebsocketMessage(message, senderToken) {
    if (!$.isBlank(message)) {
        var msg = {
            type: "chat",
            msg: message.trim(),
            token: senderToken,
            date: Date.now()
        };
        chatWebSocket.send(JSON.stringify(msg));
        $("#chat-box #message").val("");
    }/*else{
        var data = {};
        data.text = "please enter a message!";
        var uri = CONFIG.web.uri.base+"/messages/text";
        $.post(uri, JSON.stringify(data), function(response) {
            console.log(response);
            $('#chat-box .messages').html(response.message);
        }, "json");
    }*/
}

function closeWebSocket(chatWebSocket){
    chatWebSocket.onclose = function () {}; // To close gracefully, first disable onclose handler
    chatWebSocket.close();
    chatWebSocket = null;
    console.log("chatWebSocket: "+chatWebSocket);
}

//Update the chat-panel, and the list of connected users
function updateChat(msg) {
    var data = JSON.parse(msg.data);
    var chatMessages = $('#chat-box .messages');
    chatMessages.append(data.message);
    chatMessages.animate({ scrollTop: chatMessages.prop('scrollHeight') }, 300);


    //insert("chat", data.message);

    /*id("user-list").innerHTML = "";
    data.userList.forEach(function (user) {
        insert("user-list", "<li>" + user + "</li>");
    });*/

    //id("user-in-session").innerHTML = "<li>" + data.userInSession + "</li>";
}
