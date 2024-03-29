package biz.oneilindustries.management_bot.command;

import biz.oneilindustries.management_bot.dao.UserDAO;
import biz.oneilindustries.management_bot.dao.UserDAOImpl;
import biz.oneilindustries.management_bot.discord.DiscordBot;
import biz.oneilindustries.management_bot.discord.DiscordManager;
import biz.oneilindustries.management_bot.hibrenate.entity.User;
import biz.oneilindustries.management_bot.hibrenate.entity.UserRoles;
import biz.oneilindustries.management_bot.ranks.Rank;
import biz.oneilindustries.management_bot.teamspeak.TSBot;
import biz.oneilindustries.management_bot.teamspeak.TeamspeakManager;
import com.github.theholywaffle.teamspeak3.TS3Api;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.ArrayList;
import java.util.List;

public class RemoveUser extends Command{

    public RemoveUser() {
        this.name = "!removeuser";
        this.help = "Removes a users from all Oneil Industries services";
        this.args = "!removeuser steamid";
        this.requiredRole = "officer";
        this.argsAmount = 1;
        this.requiresSteamID = true;
        this.steamArgIndex = 1;
    }

    @Override
    public String executeCommand(String[] args, String[] userNameDetails) {
        UserDAO userDAO = new UserDAOImpl();

        User user = userDAO.getUser(args[steamArgIndex]);

        //Checks if the user exists
        if (user == null) return "Not a member of Oneil Industries";

        if (user.getUserNames().getDiscordUID() != null) {
            removeFromDiscord(user);
        }

        //Teamspeak role removals if user has registered on teamspeak
        if (user.getUserNames().getTeamspeakUID() != null) {
            removeFromTeamspeak(user);
        }
        //Update sql database
        userDAO.deleteUser(user);

        return "User has been removed from oneil industries";
    }

    protected void removeFromDiscord(User user) {
        //Adds relevant roles to the user on discord
        GuildController guildController = DiscordBot.getGuildController();

        if (guildController != null) {
            DiscordManager discordManager = new DiscordManager(DiscordBot.getGuildController().getGuild().getController());

            ///Adds each relevant role to list
            List<Role> roles = new ArrayList<>();
            for (UserRoles userRoles: user.getUserRoles()) {
                roles.add(Rank.getRequiredDiscordRole(userRoles.getRoleName()));
            }
            discordManager.removeUserRole(user,roles);
        }
    }

    protected void removeFromTeamspeak(User user) {
        TS3Api ts3Api = TSBot.getApi();
        if (ts3Api != null) {
            TeamspeakManager teamspeakManager = new TeamspeakManager(ts3Api);
            for (UserRoles userRoles : user.getUserRoles()) {
                teamspeakManager.removeMemberRole(userRoles.getRoleName(), user);
            }
        }
    }
}