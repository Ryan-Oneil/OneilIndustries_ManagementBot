package biz.oneilindustries.management_bot.discord;

import net.dv8tion.jda.core.entities.Role;

import java.util.List;

public class Ranks {

    private List<Role> serverRoles;

    public Ranks(List<Role> ranks) {
        this.serverRoles = ranks;
    }

    public Role getRequiredRole(String roleName) {

        if (serverRoles == null || serverRoles.isEmpty()) {
            return null;
        }

        for (Role role : serverRoles) {
            if (role.getName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        return null;
    }
}
