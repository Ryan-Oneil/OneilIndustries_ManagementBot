package biz.oneilindustries.management_bot.discord;

import biz.oneilindustries.management_bot.hibrenate.entity.User;
import biz.oneilindustries.management_bot.ranks.Rank;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;

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

    public void removeUserRole(User user, String roleName) {
        //Gets member object by user's unique id
        Member memberToRemoveRoles = getUsernameByID(user.getUserNames().getDiscordUID());

        Role role = Rank.getRequiredDiscordRole(roleName);

        if (role == null) {
            return;
        }
        this.guildController.removeRolesFromMember(memberToRemoveRoles,role).queue();
    }

    public Member getUsernameByID(String uuid) {
        return this.guildController.getGuild().getMemberById(uuid);
    }
}
