package biz.oneilindustries.management_bot.command;

import biz.oneilindustries.management_bot.ranks.Rank;

import java.util.List;

public abstract class Command {

    protected String name = null;
    protected String help = null;
    protected String requiredRole = null;
    protected String args = null;
    protected int argsAmount = 0;
    protected boolean requiresSteamID = false;
    //Starting command is 0, 1 would be first arg
    protected int steamArgIndex = 0;
    private static final String STEAM_ID = "^STEAM_[0-5]:[01]:\\d+$";

    public String run(List<String> invokerRoles, String[] args, String[] userNameDetails) {

        if (!hasPermission(this.getRequiredRole(),invokerRoles)) {
            return "You don't have the required role";
        }

        //Checks to ensure the correct amount of arguments were passed
        if ((args.length - 1) < this.getArgsAmount()) {
            return this.getArgs();
        }

        if (this.isSteamIDRequired() && !args[this.getSteamArgIndex()].matches(STEAM_ID)) {
            return "Invalid steamID";
        }

        return executeCommand(args, userNameDetails);
    }

    public abstract String executeCommand(String[] args, String[] userNameDetails);

    private boolean hasPermission(String roleReguired, List<String> invokerRoles) {
        if (!roleReguired.equals("user")) {
            return Rank.isOfficer(invokerRoles);
        }
        return true;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequiredRole() {
        return requiredRole;
    }

    public String getArgs() {
        return args;
    }

    public int getArgsAmount() {
        return argsAmount;
    }

    public boolean isSteamIDRequired() {
        return this.requiresSteamID;
    }

    public int getSteamArgIndex() {
        return steamArgIndex;
    }

    public String getHelp() {
        return help;
    }
}
