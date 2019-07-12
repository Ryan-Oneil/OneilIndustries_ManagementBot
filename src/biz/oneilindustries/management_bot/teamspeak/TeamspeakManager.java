package biz.oneilindustries.management_bot.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;

public class TeamspeakManager {

    private TS3Api api;

    public TeamspeakManager(TS3Api api) {
        this.api = api;
    }

    public void addMemberRole(String roleName, int clientID) {
        Ranks rank = new Ranks(api.getServerGroups());

        ServerGroup serverGroup = rank.getRequiredRole(roleName);

        api.addClientToServerGroup(serverGroup.getId(), clientID);
    }
}
