package biz.oneilindustries.management_bot.command;

import biz.oneilindustries.management_bot.dao.UserDAO;
import biz.oneilindustries.management_bot.dao.UserDAOImpl;
import biz.oneilindustries.management_bot.hibrenate.entity.User;
import biz.oneilindustries.management_bot.hibrenate.entity.UserNames;
import biz.oneilindustries.management_bot.hibrenate.entity.UserRoles;
import biz.oneilindustries.management_bot.ranks.Rank;

public class AddUser extends Command {

    public AddUser() {
        this.name = "!adduser";
        this.help = "Adds new users to Oneil Industries services";
        this.args = "!adduser steamid role<zarp/oneil/member>";
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

        User checkIfUserExists = userDAO.getUser(enteredSteamID);

        if (checkIfUserExists != null) return "This user already exists";

        //Creating the new user object and relevant objects
        UserRoles userRoles = new UserRoles(enteredRole);
        User user = new User(args[1],userNameDetails[0],"Not Registered", new UserNames());
        user.addUserRole(userRoles);
        userRoles.setUserID(user);

        //Saves the user to sql
        userDAO.saveUser(user);
        userDAO.close();

        return "User has been added to Oneil Indsutries";
    }
}
