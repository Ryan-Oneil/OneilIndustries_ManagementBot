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

public class AddRole extends Command {

    public AddRole() {
        this.name = "!addrole";
        this.help = "adds a role to a users for all Oneil Industries services";
        this.args = "!addrole steamid role<zarp/oneil/member>";
        this.requiredRole = "officer";
        this.argsAmount = 2;
        this.requiresSteamID = true;
        this.steamArgIndex = 1;
    }

    @Override
    public String executeCommand(String[] args, String[] userNameDetails) {
        String enteredSteamID = args[1];
        String enteredRole = args[2];

        if (!Rank.isApprovedRole(enteredRole)) {
            return "Non approved role. Refer to !help";
        }

        UserDAO userDAO = new UserDAOImpl();

        User user = userDAO.getUser(enteredSteamID);

        if (user == null) return "This user doesn't exists";

        UserRoles userRoles = new UserRoles(enteredRole);
        if (user.getUserRoles().contains(userRoles)) return "User already has this role";

        user.addUserRole(userRoles);

        userDAO.saveUser(user);

        if (user.getUserNames().getDiscordUID() != null) {
            addRoleDiscord(user, enteredRole);
        }
        if (user.getUserNames().getTeamspeakUID() != null) {
            addRoleTeamspeak(user, enteredRole);
        }

        return "User has been given " + enteredRole;
    }

    private void addRoleDiscord(User user, String role) {
        DiscordManager discordManager = new DiscordManager(DiscordBot.getGuildController());

        discordManager.addUserRole(discordManager.getUsernameByID(user.getUserNames().getDiscordUID()),role);
    }

    private void addRoleTeamspeak(User user, String role) {
        TeamspeakManager teamspeakManager = new TeamspeakManager(TSBot.getApi());

        teamspeakManager.addMemberRole(role,teamspeakManager.getMemberDatabaseID(user.getUserNames().getTeamspeakUID()));
    }
}
