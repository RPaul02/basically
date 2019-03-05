package me.robkpaul.www;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {

    public static void main(String[] args) {
        String token = "NTQ2MDE3OTM2NDUyNjE2MjAw.D1b_aA.SAyWr81MM2i0e20a9YlOc0VFuZ8"; // bot token

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if (event.getMessage().getContent().substring(0, 5).equalsIgnoreCase("!pong")) { // if the content of the message contains "!pong"

                event.getChannel().sendMessage("Pong!"); // send message "Pong!"
                System.out.println("Replied to "+ event.getMessageAuthor().getDisplayName());
            }
        });

        // Print the invite url of your bot
    }

}