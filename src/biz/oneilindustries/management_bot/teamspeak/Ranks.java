package biz.oneilindustries.management_bot.teamspeak;

import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;

import java.util.ArrayList;
import java.util.List;

public class Ranks {

    private List<ServerGroup> serverRoles;
    private static List<String> approvedRoles;

    static {
        approvedRoles = new ArrayList<>();
        approvedRoles.add("zarp");
        approvedRoles.add("oneil");
        approvedRoles.add("member");
    }

    public Ranks(List<ServerGroup> serverRoles) {
        this.serverRoles = serverRoles;
    }

    public ServerGroup getRequiredRole(String roleName) {

        //Checks if nothing was passed or if someone tries giving a non approved role
        if (serverRoles == null || serverRoles.isEmpty() || !approvedRoles.contains(roleName)) {
            return null;
        }

        for (ServerGroup serverGroup : serverRoles) {
            if (serverGroup.getName().equalsIgnoreCase(roleName)) {
                return serverGroup;
            }
        }
        return null;
    }

    public static boolean isApprovedRole(String roleName) {
        return approvedRoles.contains(roleName);
    }
}