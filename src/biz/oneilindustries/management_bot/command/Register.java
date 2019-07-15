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
import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class Register extends Command {

    public Register() {
        this.name = "!register";
        this.help = "Registers as a new user to Oneil Industries services";
        this.args = "!register steamid";
        this.requiredRole = "user";
        this.argsAmount = 1;
        this.requiresSteamID = true;
        this.steamArgIndex = 1;
    }

    @Override
    public String executeCommand(String[] args, String[] userNameDetails) {
        String service = userNameDetails[userNameDetails.length - 1];

        UserDAO userDAO = new UserDAOImpl();
        User user = userDAO.getUser(args[this.steamArgIndex]);

        //Checks if the user is authorised to register
        if (user == null) return "Not a member of Oneil Industries";

        if (!userDAO.checkIfUUIDExists(userNameDetails[1])) {
            return "This " + service + " account is already registered";
        }

        //Checks to ensure the user isn't already registered
        if (service.equals("discord")) {
            if (user.getUserNames().getDiscordName() != null) {
                return "Already registered";
            }
            discord(user,userNameDetails);
        }else if (service.equals("teamspeak")) {
            if (user.getUserNames().getTeamspeakName() != null) {
                return "Already registered";
            }
            teamspeak(user,userNameDetails);
        }

        //Updates SQL record for user
        user.setStatus("Registered");
        userDAO.saveUser(user);

        userDAO.close();

        return ("Welcome to Oneil Industries " + userNameDetails[0] + "!");
    }

    public void discord(User user, String[] userDetails) {
        //Sets the user's discord names and unique id to store in the database
        user.getUserNames().setDiscordName(userDetails[0]);
        user.getUserNames().setDiscordUID(userDetails[1]);

        //Adds relevant roles to the user on discord
        DiscordManager discordManager = new DiscordManager(DiscordBot.getGuildController());

        //Adds each relevant role to list
        List<Role> roles = new ArrayList<>();
        for (UserRoles userRoles: user.getUserRoles()) {
            roles.add(Rank.getRequiredDiscordRole(userRoles.getRoleName()));
        }
        //Calls the discord manager to update user's roles
        discordManager.addUserRole(discordManager.getUsernameByID(userDetails[1]),roles);
    }

    public void teamspeak(User user, String[] userDetails) {
        user.getUserNames().setTeamspeakName(userDetails[0]);
        user.getUserNames().setTeamspeakUID(userDetails[1]);

        TeamspeakManager teamspeakManager = new TeamspeakManager(TSBot.getApi());

        //Gets users client database ID
        int clientDatabaseID = teamspeakManager.getMemberDatabaseID(user.getUserNames().getTeamspeakUID());

        for (UserRoles userRoles: user.getUserRoles()) {
            teamspeakManager.addMemberRole(userRoles.getRoleName(),clientDatabaseID);
        }
    }
}
