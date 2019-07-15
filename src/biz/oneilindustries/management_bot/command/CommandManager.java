package biz.oneilindustries.management_bot.command;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private List<Command> commands;

    public CommandManager() {
        commands = new ArrayList<>();
        commands.add(new AddUser());
        commands.add(new RemoveUser());
        commands.add(new Register());
        commands.add(new RemoveService());
        commands.add(new AddRole());
    }

    public Command isCommand(String name) {

        for (Command command : this.commands) {
            if (command.getName().equalsIgnoreCase(name)) {
                return command;
            }
        }
        return null;
    }

    public String generateHelp(String role) {
        StringBuilder helpMessage = new StringBuilder();

        for (Command command : this.commands) {
            if (command.getRequiredRole().equals(role)) {
                helpMessage.append(command.getHelp());
                helpMessage.append(" :\n\t");
                helpMessage.append(command.getArgs());
                helpMessage.append("\n");
            }
        }
        return helpMessage.toString();
    }
}
