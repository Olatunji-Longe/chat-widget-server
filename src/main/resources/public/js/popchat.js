/**
 * Created by olatunji on 8/2/17.
 */
$(function(){
    $("#chat-popup-btn").click(function () {
        openWebSocket();
        $('#chat-box').addClass('popup-box-on');
        $("#chat-popup-btn").hide();
    });

    $("#hide-chat-popup").click(function () {
        $('#chat-box').removeClass('popup-box-on');
        $("#chat-popup-btn").show();
    });

    $("#dismiss-chat-popup").click(function () {
        closeWebSocket(chatWebSocket);
        $("#chat-box #message").val("");
        $("#chat-box .messages").html("");
        $('#chat-box').removeClass('popup-box-on');
        $("#chat-popup-btn").show();
    });

    $(document).on('click', '#send', function(e){
        sendWebsocketMessage($('#message').val(), $('.sender-id').attr('token'));
    });

    $(document).on('keypress', '#message', function(e){
        var keycode = e.keyCode || e.which;
        if(keycode == '13') {
            e.preventDefault();
            //var msg = $(this).val().replace("\n", "");
            sendWebsocketMessage($(this).val());
        }
    });
});



