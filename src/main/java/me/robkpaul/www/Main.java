package me.robkpaul.www;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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


            if (!author.isYourself() && message.length() >= 1 && message.substring(0, 1).equalsIgnoreCase("-")) {
                //pong command
                if (message.length() >= 5 && message.substring(1, 5).equalsIgnoreCase("pong")) {
                    if (!event.getMessage().getMentionedUsers().isEmpty()) {
                        int i;
                        try {
                            i = Integer.parseInt(message.substring(6, 7));
                        } catch (NumberFormatException e) {
                            i = 1;
                        }
                        for (int j = 0; j < i; j++) {
                            event.getChannel().sendMessage(event.getMessage().getMentionedUsers().get(0).getMentionTag());
                        }
                        System.out.println("Replied to " + event.getMessageAuthor().getDisplayName() + "and pinged" + event.getMessage().getMentionedUsers().get(0).getDiscriminatedName());
                    } else {
                        event.getChannel().sendMessage("```Incorrect Format used. Format is :pong [user] it's important to note that ponging more than one person at once will not work.```");
                    }
                }
                //split teams command
                else if (message.length() >= 6 && message.substring(1, 6).equalsIgnoreCase("split")) {
                    if (author.getConnectedVoiceChannel(currentServer).isPresent()) {
                        if (author.getConnectedVoiceChannel(currentServer).get().getConnectedUsers().size() >= 1 && redOptional.isPresent() && blueOptional.isPresent()) {

                            Collection<User> userCollection = author.getConnectedVoiceChannel(currentServer).get().getConnectedUsers();
                            ArrayList<User> userArrayList = new ArrayList<>(0);
                            userArrayList.addAll(userCollection);
                            Collections.shuffle(userArrayList);

                            String[] teams = shuffleTeams(userArrayList, redOptional.get(), blueOptional.get());
                            String redTeam = teams[0];
                            String blueTeam = teams[1];


                            if(redTeam.equals("")) {
                                redTeam = "Empty";
                            }

                            if(blueTeam.equals("")) {
                                blueTeam = "Empty";
                            }

                            EmbedBuilder builder = new EmbedBuilder()
                                    .setTitle("Teams")
                                    .setDescription("Teams set by the Split command.")
                                    .setColor(Color.black)
                                    .addField("Red Team", redTeam)
                                    .addField("Blue Team", blueTeam);
                            event.getChannel().sendMessage(builder);
                            System.out.println("Blue Team: " + blueTeam + " Red Team: " + redTeam);

                        }
                    }
                    else {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setTitle("Command Error")
                                .setDescription("You need to be in a channel to use this command")
                                .setColor(Color.red);
                        event.getChannel().sendMessage(builder);
                    }

                }
                //looking for group command
                /*
                else if(message.length()>=4 && message.substring(1, 4).equalsIgnoreCase("lfg")){
                    String[] fields = message.split("-");
                    if(fields.length >= 5) {

                        //- Fields 0 = empty
                        //- Fields 1 = lfg
                        //- Fields 2 = game
                        //- Fields 3 = players
                        //- Fields 4 = time

                        EmbedBuilder builder = new EmbedBuilder()
                                .setTitle(author.getDiscriminatedName() + " is Looking for a Group")
                                .setDescription("1/" + fields[3]+"players")
                                .addInlineField("Game:", fields[2])
                                .addInlineField("Time:", fields[4])
                                .setFooter("Timezone is assumed to be PST");

                        event.getChannel().sendMessage(builder);

                    }
                    else{
                        EmbedBuilder builder = new EmbedBuilder()
                                .setTitle("Command Error")
                                .setColor(Color.red)
                                .setDescription("Correct Format is ``-lfg -game -players -time``");
                        event.getChannel().sendMessage(builder);
                    }

                }
                */
            }
        });
        api.addReactionAddListener(event -> {
            CompletableFuture<Message> msgCompFut = event.requestMessage();
            Message msg = msgCompFut.get(100, TimeUnit.MILLISECONDS);

                if(msg.getAuthor().isYourself()){
                    System.out.println("Message Content: " + msg.getContent());
            }

        });
    }


    private static String[] shuffleTeams(ArrayList<User> userArrayList, ServerVoiceChannel redChannel, ServerVoiceChannel blueChannel){
        String[] teams = {
                "",
                ""
        };
        for (int i = 0; i < userArrayList.size(); i++) {
            if (i % 2 == 0) {
                userArrayList.get(i).move(redChannel);
                teams[0] = teams[0].concat(userArrayList.get(i).getDiscriminatedName());
                if(i<userArrayList.size()-2){
                    teams[0] = teams[0].concat(", ");
                }
            } else {
                userArrayList.get(i).move(blueChannel);
                teams[1] = teams[1].concat(userArrayList.get(i).getDiscriminatedName().concat(", "));
                if(i<userArrayList.size()-2){
                    teams[1]  = teams[1].concat(", ");
                }
            }

        }
        return teams;
    }
}
