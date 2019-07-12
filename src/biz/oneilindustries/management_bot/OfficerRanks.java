package biz.oneilindustries.management_bot;

import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class OfficerRanks {

    private static ArrayList<String> authorisedOfficerRanks = new ArrayList<>();

    static {
        authorisedOfficerRanks.add("ceo");
        authorisedOfficerRanks.add("manager");
        authorisedOfficerRanks.add("coo");
        authorisedOfficerRanks.add("server admin");
    }

    private OfficerRanks() {

    }

    //Method for discord
    public static boolean isAuthorisedUser(List<Role> roles) {
        boolean isAuthorised = false;

        for (Role role: roles) {
            if (authorisedOfficerRanks.contains(role.getName().toLowerCase())) {
                isAuthorised = true;
                break;
            }
        }
        return isAuthorised;
    }

    //Method for teamspeak
    public static boolean isAuthorisedUserTS(List<ServerGroup> roles) {
        boolean isAuthorised = false;

        for (ServerGroup role: roles) {
            if (authorisedOfficerRanks.contains(role.getName().toLowerCase())) {
                isAuthorised = true;
                break;
            }
        }
        return isAuthorised;
    }
}
