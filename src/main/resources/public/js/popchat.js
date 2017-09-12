/**
 * Created by olatunji on 8/2/17.
 */
$(function(){
    $("#pwc-popup-btn").click(function () {
        openWebSocket();
        $('#pwc-chat-box').addClass('pwc-popup-box-on');
        $("#pwc-popup-btn").hide();
    });

    $("#pwc-chat-popup-hide").click(function () {
        $('#pwc-chat-box').removeClass('pwc-popup-box-on');
        $("#pwc-popup-btn").show();
    });

    $("#pwc-chat-popup-dismiss").click(function () {
        closeWebSocket(chatWebSocket);
        $("#pwc-chat-box #pwc-message").val("");
        $("#pwc-chat-box .pwc-messages").html("");
        $('#pwc-chat-box').removeClass('pwc-popup-box-on');
        $("#pwc-popup-btn").show();
    });

    $(document).on('click', '#pwc-send', function(e){
        sendWebsocketMessage($('#pwc-message').val(), $('.sender-id').attr('token'));
    });

    $(document).on('keypress', '#pwc-message', function(e){
        var keycode = e.keyCode || e.which;
        if(keycode === 13) {
            e.preventDefault();
            //var msg = $(this).val().replace("\n", "");
            sendWebsocketMessage($(this).val());
        }
    });
});



