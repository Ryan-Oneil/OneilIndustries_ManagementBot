package biz.oneilindustries.management_bot.teamspeak;

import biz.oneilindustries.management_bot.OfficerRanks;
import biz.oneilindustries.management_bot.dao.UserDAO;
import biz.oneilindustries.management_bot.dao.UserDAOImpl;
import biz.oneilindustries.management_bot.discord.DiscordBot;
import biz.oneilindustries.management_bot.discord.DiscordManager;
import biz.oneilindustries.management_bot.hibrenate.entity.User;
import biz.oneilindustries.management_bot.hibrenate.entity.UserNames;
import biz.oneilindustries.management_bot.hibrenate.entity.UserRoles;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import net.dv8tion.jda.core.managers.GuildController;

public class CommandListener extends TS3EventAdapter {

    private TS3Api api;
    private int clientId;

    private static final String USER_COMMANDS = "!register steamid  : Used to register your account on Oneil Industries";
    private static final String OFFICER_COMMANDS = "!adduser steamid role(zarp/oneil/normal) \n" +
            "!remove steamid : Removes all given roles";
    private static final String STEAM_ID_REGEX = "^STEAM_[0-5]:[01]:\\d+$";

    private UserDAO userDAO;

    public CommandListener(TS3Api api) {
        this.api = api;
        this.clientId = api.whoAmI().getId();
    }

    @Override
    public void onTextMessage(TextMessageEvent message) {
        // Only react to channel messages not sent by the query itself
        if (message.getTargetMode() == TextMessageTargetMode.CHANNEL && message.getInvokerId() != clientId) {
            String messageContent = message.getMessage().toLowerCase();

            if (messageContent.contains("!help")) {
                displayHelp(message);
            }else if (messageContent.contains("!register")) {
                register(message);
            }else if (messageContent.contains("!adduser")) {
                addUser(message);
            }else if (messageContent.contains("!remove")) {
                removeUser(message);
            }
        }
    }

    private void displayHelp(TextMessageEvent message) {
        String helpMessage = USER_COMMANDS;

        if(OfficerRanks.isAuthorisedUserTS(api.getServerGroupsByClient(api.getClientByUId(message.getInvokerUniqueId())))) {
            helpMessage += "\n" + OFFICER_COMMANDS;
        }
        api.sendChannelMessage(helpMessage);
    }

    private void register(TextMessageEvent message) {

        String[] commands = message.getMessage().split(" ");

        if (commands.length < 2) {
            api.sendChannelMessage("Please enter a steamid !register steamid");
            return;
        }

        String enteredSteamID = commands[1];

        //Verifies a correct steamID was provided
        if (!verifySteamID(enteredSteamID)) return;

        userDAO = new UserDAOImpl();
        User user = userDAO.getUser(enteredSteamID);

        //Checks if the user exists
        if (verifyMembership(user)) return;

        //Checks to see if the user has already registered on teamspeak
        if (user.getUserNames().getTeamspeakName() != null) {
            api.sendChannelMessage("You are already registered");
            return;
        }

        user.getUserNames().setTeamspeakName(message.getInvokerName());
        user.getUserNames().setTeamspeakUID(message.getInvokerUniqueId());
        user.setStatus("Registered");

        userDAO.saveUser(user);

        TeamspeakManager teamspeakManager = new TeamspeakManager(this.api);

        //Gets users client database ID
        int clientDatabaseID = teamspeakManager.getMemberDatabaseID(user.getUserNames().getTeamspeakUID());

        for (UserRoles userRoles: user.getUserRoles()) {
            teamspeakManager.addMemberRole(userRoles.getRoleName(),clientDatabaseID);
        }

        userDAO.close();

        api.sendChannelMessage("Welcome to Oneil Industries!");
    }

    private void addUser(TextMessageEvent message) {

        if (!checkIfAuthorised(message)) return;

        String[] commands = message.getMessage().split(" ");

        //Ensures the command parameters are there
        if (commands.length < 3) {
            api.sendChannelMessage("Usage: !adduser steamid zarp/oneil/normal");
            return;
        }

        String enteredSteamID = commands[1];

        //Verifies a correct steamID was provided
        if (!verifySteamID(enteredSteamID)) return;

        String enteredRole = commands[2];

        if (!Ranks.isApprovedRole(enteredRole)) {
            api.sendChannelMessage("Non approved role. Refer to !help");
            return;
        }

        userDAO = new UserDAOImpl();

        User checkIfUserExists = userDAO.getUser(enteredSteamID);

        //Checks if the user exists
        if (!verifyMembership(checkIfUserExists)) return;

        //Creating the new user object and relevant objects
        UserRoles userRoles1 = new UserRoles(commands[2]);
        User user = new User(enteredSteamID,message.getInvokerName(),"Not Registered", new UserNames());
        user.addUserRole(userRoles1);
        userRoles1.setUserID(user);

        //Saves the user to sql
        userDAO.saveUser(user);
        userDAO.close();
    }

    private void removeUser(TextMessageEvent message) {

        if (!checkIfAuthorised(message)) return;

        String[] commands = message.getMessage().split(" ");

        //Checks the amount of command parameters entered
        if (commands.length < 2) {
            api.sendChannelMessage("Usage: !remove steamid");
            return;
        }

        String enteredSteamID = commands[1];

        //Verifies a correct steamID was provided
        if (!verifySteamID(enteredSteamID)) return;

        userDAO = new UserDAOImpl();

        User user = userDAO.getUser(enteredSteamID);

        //Checks if the user exists
        if (verifyMembership(user)) return;

        //Adds relevant roles to the user on discord
        TeamspeakManager teamspeakManager = new TeamspeakManager(api);

        //Removes all roles given
        for (UserRoles userRoles : user.getUserRoles()) {
            teamspeakManager.removeMemberRole(userRoles.getRoleName(), user);
        }

        //Remove roles from discord if user is registered
        if (user.getUserNames().getDiscordName() != null) {
            GuildController guildController = DiscordBot.getGuildController();

            if (guildController != null) {
                DiscordManager discordManager = new DiscordManager(guildController);

                for (UserRoles userRoles: user.getUserRoles()) {
                    discordManager.removeUserRole(user, userRoles.getRoleName());
                }
            } else {
                api.sendChannelMessage("Discord services are offline");
            }
        }
        api.sendChannelMessage("User has been removed from oneil industries");

        //Update sql database
        userDAO.deleteUser(user);
    }

    //Method that verifies steamid through regex
    private boolean verifySteamID(String enteredSteamID) {
        boolean check = enteredSteamID.matches(STEAM_ID_REGEX);

        if (!check) {
            api.sendChannelMessage("Not a valid steamID");
        }
        return check;
    }

    //Method checks if user exists
    private boolean verifyMembership(User user) {
        boolean check = user == null;

        if (check) {
            api.sendChannelMessage("Not a member of Oneil Industries");
        }
        return check;
    }

    private boolean checkIfAuthorised(TextMessageEvent message) {
        boolean check = OfficerRanks.isAuthorisedUserTS(api.getServerGroupsByClient(api.getClientByUId(message.getInvokerUniqueId())));

        if (!check) {
            api.sendChannelMessage("Not authorised");
        }
        return check;
    }
}
