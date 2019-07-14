package biz.oneilindustries.management_bot.discord;

import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class Ranks {

    private List<Role> serverRoles;
    private static List<String> approvedRoles;

    static {
        approvedRoles = new ArrayList<>();
        approvedRoles.add("zarp");
        approvedRoles.add("oneil");
        approvedRoles.add("member");
    }

    public Ranks(List<Role> ranks) {
        this.serverRoles = ranks;
    }

    public Role getRequiredRole(String roleName) {

        //Checks if nothing was passed or if someone tries giving a non approved role
        if (serverRoles == null || serverRoles.isEmpty() || !approvedRoles.contains(roleName)) {
            return null;
        }

        for (Role role : serverRoles) {
            if (role.getName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        return null;
    }

    public static boolean isApprovedRole(String roleName) {
        return approvedRoles.contains(roleName);
    }
}
