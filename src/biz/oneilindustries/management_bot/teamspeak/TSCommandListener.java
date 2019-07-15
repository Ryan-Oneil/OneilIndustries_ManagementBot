package biz.oneilindustries.management_bot.teamspeak;

import biz.oneilindustries.management_bot.ranks.Rank;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;

public class TSCommandListener extends TS3EventAdapter {

    private TS3Api api;
    private int clientId;
    private static final String PREFIX = "!";

    public TSCommandListener(TS3Api api) {
        this.api = api;
        this.clientId = api.whoAmI().getId();
        if (Rank.getTeamspeakServerRoles() == null) Rank.setTeamspeakServerRoles(api.getServerGroups());
    }

    @Override
    public void onTextMessage(TextMessageEvent messageRecieved) {
        // Only react to channel messages not sent by the query itself
        if (messageRecieved.getTargetMode() == TextMessageTargetMode.CHANNEL && messageRecieved.getInvokerId() != clientId) {
            String message = messageRecieved.getMessage();

            //Processes command given by user
            if (message.startsWith(PREFIX)) {
                new TeamspeakCommandEvent(messageRecieved,api).processEvent();
            }
        }
    }

}
