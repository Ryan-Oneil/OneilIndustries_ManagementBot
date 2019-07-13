package biz.oneilindustries.management_bot.teamspeak;

import biz.oneilindustries.management_bot.hibrenate.entity.User;
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

    public void removeMemberRole(String roleName, User user) {
        Ranks rank = new Ranks(api.getServerGroups());

        ServerGroup serverGroup = rank.getRequiredRole(roleName);

        //Gets users client database ID
        int clientDatabaseID = getMemberDatabaseID(user.getUserNames().getTeamspeakUID());

        api.removeClientFromServerGroup(serverGroup.getId(),clientDatabaseID);
    }

    public int getMemberDatabaseID(String clientID) {
        return api.getDatabaseClientByUId(clientID).getDatabaseId();
    }
}
