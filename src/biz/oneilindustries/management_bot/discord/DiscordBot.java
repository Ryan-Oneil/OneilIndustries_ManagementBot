package biz.oneilindustries.management_bot.discord;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.managers.GuildController;

import javax.security.auth.login.LoginException;

public class DiscordBot {

    private static JDA jda;

    private DiscordBot() {
    }

    public static void start(String botToken) {
        try {
            jda = new JDABuilder(botToken).build();
            jda.addEventListener(new CommandListener());
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static GuildController getGuildController() {
        return jda.getGuildById("367725161052372993").getController();
    }
}
