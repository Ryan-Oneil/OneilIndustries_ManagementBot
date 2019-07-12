package biz.oneilindustries.management_bot.teamspeak;

import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;

import java.util.List;

public class Ranks {

    private List<ServerGroup> serverRoles;

    public Ranks(List<ServerGroup> serverRoles) {
        this.serverRoles = serverRoles;
    }

    public ServerGroup getRequiredRole(String roleName) {

        if (serverRoles == null || serverRoles.isEmpty()) {
            return null;
        }

        for (ServerGroup serverGroup : serverRoles) {
            if (serverGroup.getName().equalsIgnoreCase(roleName)) {
                return serverGroup;
            }
        }
        return null;
    }
}
