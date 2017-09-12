package com.quadbaze.cws.controllers.socket.core;

import com.quadbaze.cws.utils.Attributes;
import com.quadbaze.cws.utils.HtmlUtil;
import com.quadbaze.cws.utils.PropUtil;
import com.quadbaze.cws.utils.ResourceLoader;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by olatunji on 8/1/17.
 */
public class Chat {

    // this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    private final Map<String, Session> sessionUsernamesMap = new ConcurrentHashMap<>();
    private int nextUserNumber = 1; //Used for creating the next username

    public Map<String, Session> getSessionUsernamesMap() {
        return sessionUsernamesMap;
    }

    private int getNextUserNumber() {
        return nextUserNumber++;
    }

    public String getUsername(Session userSession){
        String username;
        username = userSession.getUpgradeRequest().getHeader("username");
        if(StringUtils.isBlank(username)) {
            //username = UUID.fromString(String.valueOf(new Date().getTime()+UUID.randomUUID().toString())).toString();
            username = "Anonymous-" + getNextUserNumber();
        }
        return username;
    }

    //Sends a message to user in session's view, along with current username
    public void pushMessage(Session userSession, String sender, String message) throws Exception {
        if(userSession.getUpgradeRequest().getHeader("is_welcome").equals(Boolean.TRUE.toString())){
            message = PropUtil.getProperty(ResourceLoader.PROP_FILE,"message.welcome");
        }
        userSession.getRemote().sendString(String.valueOf(new JSONObject()
                .put("message", getSimpleMessageHtml(sender, message))
                .put("userInSession", getUsername(userSession))
        ));
        userSession.getUpgradeRequest().setHeader("is_welcome", Boolean.FALSE.toString());
    }

    public void broadcastMessage(String sender, String message) throws Exception {
        for(String username : sessionUsernamesMap.keySet()){
            Session userSession = sessionUsernamesMap.get(username);
            userSession.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("message", getSimpleMessageHtml(sender, message))
                    .put("userList", sessionUsernamesMap.keySet())
            ));
        }
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    public static String getSimpleMessageHtml(String sender, String message) throws Exception {
        String chatTemplatePath = "template/chat.html";
        Attributes attr = Attributes.INSTANCE
                .assign("@sender", sender)
                .assign("@message", message)
                .assign("@side", getSide(sender))

                .assign("@timestamp", new SimpleDateFormat("HH:mm a").format(new Date()));
        return HtmlUtil.toHtml(chatTemplatePath, attr);
    }

    private static String getSide(String sender){
        return sender.toUpperCase().equals("server".toUpperCase()) ? "left" : "right";
    }

}
