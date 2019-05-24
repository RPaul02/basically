package me.robkpaul.www.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Split {

    private DiscordApi bot;
    private Message message;
    private User author;
    private ServerVoiceChannel redVoice;
    private ServerVoiceChannel blueVoice;
    private ServerVoiceChannel currentChannel;
    private Server server;

    /**
     * @param api API
     * @param msg Message
     * @param rVC Red Voice Channel
     * @param bVC Blue Voice Channel
     * @param cs Server
     */
    public Split(DiscordApi api, Message msg, ServerVoiceChannel rVC, ServerVoiceChannel bVC, Server cs){
        bot = api;
        server = cs;
        message = msg;
        author = msg.getUserAuthor().isPresent() ? msg.getUserAuthor().get() : null;
        redVoice = rVC;
        blueVoice = bVC;
        currentChannel = author!=null ? author.getConnectedVoiceChannel(server).isPresent() ? author.getConnectedVoiceChannel(server).get() : null  : null;
    }

    public void command(){
        if(author == null || currentChannel == null)
            return;

        ArrayList<User> userArrayList = new ArrayList<>(0);
        userArrayList.addAll(currentChannel.getConnectedUsers());

        String[] teams = shuffleTeams(userArrayList, redVoice, redVoice);
        String redTeam = teams[0];
        String blueTeam = teams[1];

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Teams")
                .setDescription("Teams set by the Split command.")
                .setColor(Color.black)
                .addField("Red Team", redTeam)
                .addField("Blue Team", blueTeam);
        message.getChannel().sendMessage(builder);
        System.out.println("Blue Team: " + blueTeam + " Red Team: " + redTeam);
    }

    private static String[] shuffleTeams(ArrayList<User> userArrayList, ServerVoiceChannel redChannel, ServerVoiceChannel blueChannel) {
        String[] teams = {"", ""};
        Collections.shuffle(userArrayList);

        for (int i = 0; i < userArrayList.size(); i++) {
            if (i % 2 == 0) {
                userArrayList.get(i).move(redChannel);
                teams[0] = teams[0].concat(userArrayList.get(i).getDiscriminatedName());
                if (i < userArrayList.size() - 2) {
                    teams[0] = teams[0].concat(", ");
                }
            } else {
                userArrayList.get(i).move(blueChannel);
                teams[1] = teams[1].concat(userArrayList.get(i).getDiscriminatedName().concat(", "));
                if (i < userArrayList.size() - 2) {
                    teams[1] = teams[1].concat(", ");
                }
            }

        }
        if (teams[0].equals("")) {
            teams[0] = ("Empty");
        }
        if (teams[1].equals("")) {
            teams[1] = "Empty";
        }
        return teams;
    }
}
