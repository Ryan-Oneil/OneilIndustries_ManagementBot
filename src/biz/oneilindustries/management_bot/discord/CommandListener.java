package biz.oneilindustries.management_bot.discord;

import biz.oneilindustries.management_bot.OfficerRanks;
import biz.oneilindustries.management_bot.dao.UserDAO;
import biz.oneilindustries.management_bot.dao.UserDAOImpl;
import biz.oneilindustries.management_bot.hibrenate.entity.User;
import biz.oneilindustries.management_bot.hibrenate.entity.UserNames;
import biz.oneilindustries.management_bot.hibrenate.entity.UserRoles;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public class CommandListener extends ListenerAdapter {

    private static final String USER_COMMANDS = "!register steamid  : Used to register your account on Oneil Industries";
    private static final String OFFICER_COMMANDS = "!adduser steamid role(zarp/oneil/normal)";
    private UserDAO userDAO;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) throws NullPointerException {

        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();

        String content = message.getContentRaw();

        if (content.toLowerCase().contains("!register")) {
            registerUser(message);
        }else if (content.toLowerCase().contains("!adduser")) {
            addNewUser(message);
        }else if (content.toLowerCase().contains("!help")) {
            displayHelp(message);
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

        userDAO = new UserDAOImpl();
        User user = userDAO.getUser(enteredSteamID);

        //Checks if the user is authorised to register
        if (user == null) {
            messageReceived.getChannel().sendMessage("Not a member of Oneil Industries").queue();
            return;
        }

        //Checks to ensure the user isn't already registered
        if (user.getUserNames().getDiscordName() != null) {
            messageReceived.getChannel().sendMessage("Already registered").queue();
            return;
        }

        //Updates SQL record for user
        user.getUserNames().setDiscordName(messageReceived.getAuthor().getName());
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
        if (!OfficerRanks.isAuthorisedUser(messageReceived.getMember().getRoles())) {
            messageReceived.getChannel().sendMessage("Not authorised").queue();
            return;
        }

        String commandContent = messageReceived.getContentRaw();

        String[] commands = commandContent.split(" ");

        //Ensures the command parameters are there
        if (commands.length < 3) {
            messageReceived.getChannel().sendMessage("Usage: !adduser steamid zarp/oneil/normal").queue();
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
}
