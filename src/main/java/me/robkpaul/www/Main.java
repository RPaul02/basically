package me.robkpaul.www;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        String token = "NTQ2MDE3OTM2NDUyNjE2MjAw.D1b_aA.SAyWr81MM2i0e20a9YlOc0VFuZ8"; // bot token

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        Optional<ServerVoiceChannel> redOptional = api.getServerVoiceChannelById("553428495041036299");
        Optional<ServerVoiceChannel> blueOptional = api.getServerVoiceChannelById("553428606454595600");

        api.addMessageCreateListener(event -> {

            String message = event.getMessage().getContent();

            User author = event.getMessageAuthor().asUser().get();

            Server currentServer = event.getServer().get();

            System.out.println("message received");


            //-pong 1 @user
            if (message.length() >= 1 && message.substring(0, 1).equalsIgnoreCase("-")) {
                if (message.length() >= 5 && message.substring(1, 5).equalsIgnoreCase("pong")) {
                    if (!event.getMessage().getMentionedUsers().isEmpty()) {
                        int i;
                        try {
                            i = Integer.parseInt(message.substring(6, 7));
                        }catch(NumberFormatException e){
                            i=1;
                        }
                        for(int j = 0; j<i; j++){
                            event.getChannel().sendMessage(event.getMessage().getMentionedUsers().get(0).getMentionTag());
                        }
                        System.out.println("Replied to " + event.getMessageAuthor().getDisplayName() + "and pinged" + event.getMessage().getMentionedUsers().get(0).getDiscriminatedName());
                    } else {
                        event.getChannel().sendMessage("```Incorrect Format used. Format is :pong [user] it's important to note that ponging more than one person at once will not work.```");
                    }
                }
                else if (message.length() >= 6 && message.substring(1, 6).equalsIgnoreCase("split")) {
                    if (author.getConnectedVoiceChannel(currentServer).isPresent()) {
                        if (author.getConnectedVoiceChannel(currentServer).get().getConnectedUsers().size() > 1 && redOptional.isPresent() && blueOptional.isPresent()){
                            Collection<User> userCollection = author.getConnectedVoiceChannel(currentServer).get().getConnectedUsers();
                            ArrayList<User> userArrayList = new ArrayList<>(0);
                            userArrayList.addAll(userCollection);
                            Collections.shuffle(userArrayList);
                            for(int i = 0; i < userArrayList.size(); i++){
                                if(i%2==0){
                                    userArrayList.get(i).move(redOptional.get());
                                }
                                else{
                                    userArrayList.get(i).move(blueOptional.get());
                                }
                            }

                        }
                    } else {
                        event.getChannel().sendMessage("```You have to be connected to a channel to use this command```");
                    }

                }
            }
        });
    }
}