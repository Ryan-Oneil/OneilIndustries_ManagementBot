package biz.oneilindustries.management_bot.discord;

import biz.oneilindustries.management_bot.hibrenate.entity.User;
import biz.oneilindustries.management_bot.ranks.Rank;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.List;

public class DiscordManager {

    private GuildController guildController;

    public DiscordManager(GuildController guildController) {
        this.guildController = guildController;
    }

    public void addUserRole(Member member, String roleName) {
        Role role = Rank.getRequiredDiscordRole(roleName);

        if (role == null) {
            return;
        }
        this.guildController.addRolesToMember(member,role).queue();
    }

    public void addUserRole(Member member, List<Role> roles) {
        this.guildController.addRolesToMember(member,roles).queue();
    }

    public void removeUserRole(User user, List<Role> roles) {
        //Gets member object by user's unique id
        Member memberToRemoveRoles = getUsernameByID(user.getUserNames().getDiscordUID());

        this.guildController.removeRolesFromMember(memberToRemoveRoles,roles).queue();
    }

    public Member getUsernameByID(String uuid) {
        return this.guildController.getGuild().getMemberById(uuid);
    }
}
