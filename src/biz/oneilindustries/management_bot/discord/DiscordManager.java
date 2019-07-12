package biz.oneilindustries.management_bot.discord;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;

public class DiscordManager {

    private GuildController guildController;

    public DiscordManager(GuildController guildController) {
        this.guildController = guildController;
    }

    public void addUserRole(Member member, String roleName) {

        Ranks rank = new Ranks(guildController.getGuild().getRoles());

        Role role = rank.getRequiredRole(roleName);

        if (role == null) {
            return;
        }
        this.guildController.addRolesToMember(member,role).queue();
    }
}
