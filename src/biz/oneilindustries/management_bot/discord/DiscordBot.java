package biz.oneilindustries.management_bot.discord;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;

public class DiscordBot {

    private String botToken;

    public DiscordBot(String botToken) {
        this.botToken = botToken;
    }

    public void start() {
        try {
            JDA jda = new JDABuilder(botToken).build();
            jda.addEventListener(new CommandListener());
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
