package biz.oneilindustries.management_bot.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;

public class TSBot {

    private String tsUsername;
    private String tsPassword;
    private String tsIPAddress;

    public TSBot(String tsUsername, String tsPassword, String tsIPAddress) {
        this.tsUsername = tsUsername;
        this.tsPassword = tsPassword;
        this.tsIPAddress = tsIPAddress;
    }

    public void start() {
        final TS3Config config = new TS3Config();
        config.setHost(this.tsIPAddress);

        final TS3Query query = new TS3Query(config);
        query.connect();

        final TS3Api api = query.getApi();
        api.login(this.tsUsername, this.tsPassword);
        api.selectVirtualServerById(1);
        api.setNickname("Oneil Management Bot");

        // Listen to chat in the channel the query is currently in
        // As we never changed the channel, this will be the default channel of the server
        api.registerEvent(TS3EventType.TEXT_CHANNEL, 0);

        // Register the event listener
        api.addTS3Listeners(new CommandListener(api));
    }

}
