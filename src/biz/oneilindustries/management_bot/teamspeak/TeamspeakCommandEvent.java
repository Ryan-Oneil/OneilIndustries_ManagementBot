package biz.oneilindustries.management_bot.teamspeak;

import biz.oneilindustries.management_bot.command.Command;
import biz.oneilindustries.management_bot.command.CommandManager;
import biz.oneilindustries.management_bot.ranks.Rank;
import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;

import java.util.ArrayList;

public class TeamspeakCommandEvent {

    private TextMessageEvent message;
    private TS3Api api;
    private TeamspeakManager teamspeakManager;

    public TeamspeakCommandEvent(TextMessageEvent message, TS3Api api) {
        this.message = message;
        this.api = api;
        this.teamspeakManager = new TeamspeakManager(api);
    }

    public void processEvent() {
        String[] commandDetails = message.getMessage().split(" +");

        //Create the command manager object to determine if the command entered is defined
        CommandManager commandManager = new CommandManager();
        Command command = commandManager.isCommand(commandDetails[0]);

        //Creates array of the roles of the person calling the command
        ArrayList<String> invokerRoles = new ArrayList<>();

        for (ServerGroup role : teamspeakManager.getServerGroupsByUUID(message.getInvokerUniqueId())) {
            invokerRoles.add(role.getName().toLowerCase());
        }

        //Determines if the command exists. If not displays help
        if (command != null) {
            //Holds the user's name and unique id and the service the command was called from
            String[] usernameDetails = {message.getInvokerName(), message.getInvokerUniqueId(),"teamspeak"};

            //Runs the command and gets return message if the command returns one
            String returnMessage = command.run(invokerRoles,commandDetails,usernameDetails);

            if (returnMessage != null) {
                writeTeamspeakMessage((returnMessage));
            }
        }else {
            // display help message info
            boolean isOfficer = Rank.isOfficer(invokerRoles);

            //Generate help command from existing commands
            String helpMessage = commandManager.generateHelp("user");
            if (isOfficer) {
                helpMessage += commandManager.generateHelp("officer");
            }
            writeTeamspeakMessage(helpMessage);
        }
    }

    private void writeTeamspeakMessage(String text) {
        this.api.sendChannelMessage(text);
    }
}
