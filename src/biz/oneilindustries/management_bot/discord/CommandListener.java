package biz.oneilindustries.management_bot.discord;

import biz.oneilindustries.management_bot.OfficerRanks;
import biz.oneilindustries.management_bot.dao.UserDAO;
import biz.oneilindustries.management_bot.dao.UserDAOImpl;
import biz.oneilindustries.management_bot.hibrenate.entity.User;
import biz.oneilindustries.management_bot.hibrenate.entity.UserNames;
import biz.oneilindustries.management_bot.hibrenate.entity.UserRoles;
import biz.oneilindustries.management_bot.teamspeak.TSBot;
import biz.oneilindustries.management_bot.teamspeak.TeamspeakManager;
import com.github.theholywaffle.teamspeak3.TS3Api;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public class CommandListener extends ListenerAdapter {

    private static final String USER_COMMANDS = "!register steamid  : Used to register your account on Oneil Industries";
    private static final String OFFICER_COMMANDS = "!adduser steamid role(zarp/oneil/normal) \n" +
            "!remove steamid : Removes all given roles";
    private static final String STEAM_ID_REGEX = "^STEAM_[0-5]:[01]:\\d+$";
    private UserDAO userDAO;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) throws NullPointerException {

        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();

        String content = message.getContentRaw().toLowerCase();

        if (content.contains("!register")) {
            registerUser(message);
        }else if (content.contains("!adduser")) {
            addNewUser(message);
        }else if (content.contains("!help")) {
            displayHelp(message);
        }else if (content.contains("!remove")) {
            removeUser(message);
        }
    }

    private void registerUser(Message messageReceived) {
        String commandContent = messageReceived.getContentRaw();
        String[] commands = commandContent.split(" ");

        //Checks the amount of command parameters entered
        if (commands.length < 2) {
            messageReceived.getChannel().sendMessage("Usage: !register steamid").queue();
            return;
        }

        String enteredSteamID = commands[1];

        if (verifySteamID(messageReceived, enteredSteamID)) return;

        userDAO = new UserDAOImpl();
        User user = userDAO.getUser(enteredSteamID);

        //Checks if the user is authorised to register
        if (verifyMembership(messageReceived, user)) return;

        //Checks to ensure the user isn't already registered
        if (user.getUserNames().getDiscordName() != null) {
            messageReceived.getChannel().sendMessage("Already registered").queue();
            return;
        }

        //Updates SQL record for user
        user.getUserNames().setDiscordName(messageReceived.getAuthor().getName());
        user.getUserNames().setDiscordUID(messageReceived.getAuthor().getId());
        user.setStatus("Registered");
        userDAO.saveUser(user);


        //Adds relevant roles to the user on discord
        DiscordManager discordManager = new DiscordManager(messageReceived.getGuild().getController());

        for (UserRoles userRoles: user.getUserRoles()) {
            discordManager.addUserRole(messageReceived.getMember(),userRoles.getRoleName());
        }

        userDAO.close();

        messageReceived.getChannel().sendMessage("Welcome to Oneil Industries!").queue();
    }

    private void displayHelp(Message message) {
        List<Role> userRoles = message.getMember().getRoles();

        String helpMessage = USER_COMMANDS;

        if(OfficerRanks.isAuthorisedUser(userRoles)) {
            helpMessage += "\n" + OFFICER_COMMANDS;
        }
        message.getChannel().sendMessage(helpMessage).queue();
    }

    private void addNewUser(Message messageReceived) {

        userDAO = new UserDAOImpl();

        //Ensures the user is allowed to use the command
        if (checkIfAuthorised(messageReceived)) return;

        String commandContent = messageReceived.getContentRaw();

        String[] commands = commandContent.split(" ");

        //Ensures the command parameters are there
        if (commands.length < 3) {
            messageReceived.getChannel().sendMessage("Usage: !adduser steamid zarp/oneil/normal").queue();
        }

        String enteredSteamID = commands[1];

        if (verifySteamID(messageReceived, enteredSteamID)) return;

        User checkIfUserExists = userDAO.getUser(enteredSteamID);

        if (checkIfUserExists != null) {
            messageReceived.getChannel().sendMessage("This user already exists").queue();
            return;
        }

        //Creating the new user object and relevant objects
        UserRoles userRoles1 = new UserRoles(commands[2]);
        User user = new User(commands[1],messageReceived.getMember().getUser().getName(),"Not Registered", new UserNames());
        user.addUserRole(userRoles1);
        userRoles1.setUserID(user);

        //Saves the user to sql
        userDAO.saveUser(user);
        userDAO.close();
    }

    private void removeUser(Message messageReceived) {
        //Ensures the user is allowed to use the command
        if (checkIfAuthorised(messageReceived)) return;

        String commandContent = messageReceived.getContentRaw();
        String[] commands = commandContent.split(" ");

        userDAO = new UserDAOImpl();

        //Checks the amount of command parameters entered
        if (commands.length < 2) {
            messageReceived.getChannel().sendMessage("Usage: !remove steamid").queue();
            return;
        }

        String enteredSteamID = commands[1];

        //Verifies a correct steamID was provided
        if (verifySteamID(messageReceived, enteredSteamID)) return;

        User user = userDAO.getUser(enteredSteamID);

        //Checks if the user exists
        if (verifyMembership(messageReceived, user)) return;

        //Adds relevant roles to the user on discord
        DiscordManager discordManager = new DiscordManager(messageReceived.getGuild().getController());

        //Removes all roles given
        for (UserRoles userRoles : user.getUserRoles()) {
            discordManager.removeUserRole(user,userRoles.getRoleName());
        }

        //Teamspeak role removals if user has registered on teamspeak
        if (user.getUserNames().getTeamspeakName() != null) {
            TS3Api ts3Api = TSBot.getApi();
            if (ts3Api != null) {
                TeamspeakManager teamspeakManager = new TeamspeakManager(ts3Api);
                for (UserRoles userRoles : user.getUserRoles()) {
                    teamspeakManager.removeMemberRole(userRoles.getRoleName(), user);
                }
            } else {
                messageReceived.getChannel().sendMessage("Teamspeak services is offline").queue();
            }
        }

        messageReceived.getChannel().sendMessage("User has been removed from oneil industries").queue();

        //Update sql database
        userDAO.deleteUser(user.getId());
        userDAO.close();
    }

    //Method that verifies steamid through regex
    private boolean verifySteamID(Message messageReceived, String enteredSteamID) {
        boolean check = enteredSteamID.matches(STEAM_ID_REGEX);

        if (!check) {
            messageReceived.getChannel().sendMessage("Not a valid steamID").queue();
        }
        return check;
    }

    //Method checks if user exists
    private boolean verifyMembership(Message messageReceived, User user) {
        boolean check = user == null;

        if (check) {
            messageReceived.getChannel().sendMessage("Not a member of Oneil Industries").queue();
        }
        return check;
    }

    private boolean checkIfAuthorised(Message messageReceived) {
        boolean check = OfficerRanks.isAuthorisedUser(messageReceived.getMember().getRoles());

        if (!check) {
            messageReceived.getChannel().sendMessage("Not authorised").queue();
        }
        return check;
    }
}
