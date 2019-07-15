package biz.oneilindustries.management_bot.command;

import biz.oneilindustries.management_bot.dao.UserDAO;
import biz.oneilindustries.management_bot.dao.UserDAOImpl;
import biz.oneilindustries.management_bot.hibrenate.entity.User;

public class RemoveService extends Command{

    private RemoveUser removeUser = new RemoveUser();

    public RemoveService() {
        this.name = "!removeservice";
        this.help = "Removes a users access to a certain Oneil Industries services";
        this.args = "!removeservice steamid service<teamspeak/discord>";
        this.requiredRole = "officer";
        this.argsAmount = 2;
        this.requiresSteamID = true;
        this.steamArgIndex = 1;
    }

    @Override
    public String executeCommand(String[] args, String[] userNameDetails) {
        UserDAO userDAO = new UserDAOImpl();

        //Gets user from database by steamid
        User user = userDAO.getUser(args[steamArgIndex]);

        //Checks if the user exists
        if (user == null) return "Not a member of Oneil Industries";

        String service = args[2];
        String errorMessage;

        if (service.equalsIgnoreCase("discord")) {
            errorMessage = removeFromDiscord(user);
        }else if (service.equalsIgnoreCase("teamspeak")) {
            errorMessage = removeFromTeamspeak(user);
        }else {
            return "Unkown service entered. Refer to !help";
        }
        //Returns if the user never existed on the entered service
        if (errorMessage != null) {
            return errorMessage;
        }
        //Saves the updated record
        userDAO.saveUser(user);

        return "User removed from " + args[2];
    }

    private String removeFromDiscord(User user) {
        if (user.getUserNames().getDiscordName() == null) return "Not registered on Discord";

        //Calls the removeUser command remove function for discord
        removeUser.removeFromDiscord(user);

        user.getUserNames().setDiscordUID(null);
        user.getUserNames().setDiscordName(null);

        user.setStatus("Removed from discord");

        //Nulls means success
        return null;
    }

    private String removeFromTeamspeak(User user) {
        if (user.getUserNames().getTeamspeakName() == null) return "Not registered on Teamspeak";

        //Calls the removeUser command remove function for teamspeak
        removeUser.removeFromTeamspeak(user);

        user.getUserNames().setTeamspeakUID(null);
        user.getUserNames().setTeamspeakName(null);

        user.setStatus("Removed from Teamspeak");

        //Nulls means success
        return null;
    }
}
