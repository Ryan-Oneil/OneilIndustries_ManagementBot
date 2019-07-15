package biz.oneilindustries.management_bot.teamspeak;

import biz.oneilindustries.management_bot.hibrenate.entity.User;
import biz.oneilindustries.management_bot.ranks.Rank;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;

import java.util.List;

public class TeamspeakManager {

    private TS3Api api;

    public TeamspeakManager(TS3Api api) {
        this.api = api;
    }

    public void addMemberRole(String roleName, int clientID) {
        ServerGroup serverGroup = Rank.getRequiredTeamspeakRole(roleName);

        api.addClientToServerGroup(serverGroup.getId(), clientID);
    }

    public void removeMemberRole(String roleName, User user) {
        ServerGroup serverGroup = Rank.getRequiredTeamspeakRole(roleName);

        //Gets users client database ID
        int clientDatabaseID = getMemberDatabaseID(user.getUserNames().getTeamspeakUID());

        try {
            api.removeClientFromServerGroup(serverGroup.getId(), clientDatabaseID);
        } catch (TS3CommandFailedException e) {
            e.printStackTrace();
        }
    }

    public int getMemberDatabaseID(String clientID) {
        return api.getDatabaseClientByUId(clientID).getDatabaseId();
    }

    public List<ServerGroup> getServerGroupsByUUID(String uuid) {
        return api.getServerGroupsByClientId(getMemberDatabaseID(uuid));
    }
}
