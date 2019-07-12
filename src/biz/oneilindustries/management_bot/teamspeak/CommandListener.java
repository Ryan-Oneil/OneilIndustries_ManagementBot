package biz.oneilindustries.management_bot.teamspeak;

import biz.oneilindustries.management_bot.OfficerRanks;
import biz.oneilindustries.management_bot.dao.UserDAO;
import biz.oneilindustries.management_bot.dao.UserDAOImpl;
import biz.oneilindustries.management_bot.hibrenate.entity.User;
import biz.oneilindustries.management_bot.hibrenate.entity.UserNames;
import biz.oneilindustries.management_bot.hibrenate.entity.UserRoles;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;

public class CommandListener extends TS3EventAdapter {

    private TS3Api api;
    private int clientId;

    private static final String USER_COMMANDS = "!register steamid  : Used to register your account on Oneil Industries";
    private static final String OFFICER_COMMANDS = "!adduser steamid role(zarp/oneil/normal)";

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

        userDAO = new UserDAOImpl();
        User user = userDAO.getUser(enteredSteamID);

        if (user == null) {
            api.sendChannelMessage("You are a member of Oneil Industries");
            return;
        }

        if (user.getUserNames().getTeamspeakName() != null) {
            api.sendChannelMessage("You are already registered");
            return;
        }

        user.getUserNames().setTeamspeakName(message.getInvokerName());
        user.setStatus("Registered");

        userDAO.saveUser(user);

        TeamspeakManager teamspeakManager = new TeamspeakManager(this.api);

        for (UserRoles userRoles: user.getUserRoles()) {
            message.getInvokerUniqueId();
            teamspeakManager.addMemberRole(userRoles.getRoleName(),api.getClientByUId(message.getInvokerUniqueId()).getDatabaseId());
        }

        userDAO.close();

        api.sendChannelMessage("Welcome to Oneil Industries!");
    }

    private void addUser(TextMessageEvent message) {
        userDAO = new UserDAOImpl();

        if (!OfficerRanks.isAuthorisedUserTS(api.getServerGroupsByClient(api.getClientByUId(message.getInvokerUniqueId())))) {
            api.sendChannelMessage("Not authorised");
            return;
        }

        String[] commands = message.getMessage().split(" ");

        //Ensures the command parameters are there
        if (commands.length < 3) {
            api.sendChannelMessage("Usage: !adduser steamid zarp/oneil/normal");
            return;
        }

        //Creating the new user object and relevant objects
        UserRoles userRoles1 = new UserRoles(commands[2]);
        User user = new User(commands[1],message.getInvokerName(),"Not Registered", new UserNames());
        user.addUserRole(userRoles1);
        userRoles1.setUserID(user);

        //Saves the user to sql
        userDAO.saveUser(user);
        userDAO.close();
    }
}
