package biz.oneilindustries.management_bot.discord;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

    private static final String PREFIX = "!";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) throws NullPointerException {

        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();

        //Processes command given by user
        if (message.startsWith(PREFIX)) {
            new DiscordCommandEvent(event).processEvent();
        }
    }

}
