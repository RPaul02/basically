package me.robkpaul.www;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {

    public static void main(String[] args) {
        String token = "NTQ2MDE3OTM2NDUyNjE2MjAw.D1b_aA.SAyWr81MM2i0e20a9YlOc0VFuZ8"; // bot token

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            String message = event.getMessage().getContent();
            if (message.length() >= 1 && message.substring(0, 1).equalsIgnoreCase(":")) {

                if(message.substring(1,5).equals("pong")) {
                    if (!event.getMessage().getMentionedUsers().isEmpty()) {
                        event.getChannel().sendMessage(event.getMessage().getMentionedUsers().get(0).getMentionTag());
                        System.out.println("Replied to " + event.getMessageAuthor().getDisplayName() + "and pinged" + event.getMessage().getMentionedUsers().get(0).getDiscriminatedName());
                    } else {
                        event.getChannel().sendMessage("```Incorrect Format used. Format is :pong [user] it's important to also not that pinging more than one person will not work.```");
                    }
                }
            }

        });
    }
}