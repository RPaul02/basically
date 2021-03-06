package me.robkpaul.www;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Main {

    public static void main(String[] args) {
        String token = null;
        try (Stream<String> stream = Files.lines(Paths.get("token.txt"), StandardCharsets.UTF_8)) {
            token = stream.collect(Collectors.joining());
        } catch (IOException e) {
            e.printStackTrace();
        }

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        Optional<ServerVoiceChannel> redOptional = api.getServerVoiceChannelById("553428495041036299");
        Optional<ServerVoiceChannel> blueOptional = api.getServerVoiceChannelById("553428606454595600");

        api.addMessageCreateListener(event -> {


            String message = event.getMessage().getContent();
            String[] msgFields = message.split(" ");
            User author = event.getMessageAuthor().asUser().isPresent() ? event.getMessageAuthor().asUser().get() : null;
            Server currentServer = event.getServer().isPresent() ? event.getServer().get() : null;

            if (author != null && !author.isYourself()) {
                System.out.println("message received");
            }


            if (author != null && !author.isYourself() && message.length() >= 1 && message.substring(0, 1).equalsIgnoreCase("-")) {
                //pong command
                if (message.length() >= 5 && message.substring(1, 5).equalsIgnoreCase("pong")) {
                    if (!event.getMessage().getMentionedUsers().isEmpty()) {
                        int i;
                        String[] fields = message.split(" ");

                        try {
                            i = Integer.parseInt(fields[2]);
                        } catch (NumberFormatException e) {
                            i = 1;
                        }
                        System.out.print("Replying to " + event.getMessageAuthor().getDisplayName() + " and pinging " + event.getMessage().getMentionedUsers().get(0).getDiscriminatedName() + " " + i + " times.");
                        while (i != 0) {
                            event.getChannel().sendMessage(event.getMessage().getMentionedUsers().get(0).getMentionTag());
                            i -= 1;
                        }
                        System.out.println(" Completed.");
                    } else {
                        event.getChannel().sendMessage("```Incorrect Format used. Format is :pong [user] it's important to note that ponging more than one person at once will not work.```");
                    }
                }
                //split teams command
                else if (message.length() >= 6 && message.substring(1, 6).equalsIgnoreCase("split")) {
                    if (currentServer != null && author.getConnectedVoiceChannel(currentServer).isPresent()) {
                        if (author.getConnectedVoiceChannel(currentServer).get().getConnectedUsers().size() >= 1 && redOptional.isPresent() && blueOptional.isPresent()) {

                            Collection<User> userCollection = author.getConnectedVoiceChannel(currentServer).get().getConnectedUsers();
                            ArrayList<User> userArrayList = new ArrayList<>(0);
                            userArrayList.addAll(userCollection);

                            String[] teams = shuffleTeams(userArrayList, redOptional.get(), blueOptional.get());
                            String redTeam = teams[0];
                            String blueTeam = teams[1];

                            EmbedBuilder builder = new EmbedBuilder()
                                    .setTitle("Teams")
                                    .setDescription("Teams set by the Split command.")
                                    .setColor(Color.black)
                                    .addField("Red Team", redTeam)
                                    .addField("Blue Team", blueTeam);
                            event.getChannel().sendMessage(builder);
                            System.out.println("Blue Team: " + blueTeam + " Red Team: " + redTeam);

                        }
                    } else {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setTitle("Command Error")
                                .setDescription("You need to be in a channel to use this command")
                                .setColor(Color.red);
                        event.getChannel().sendMessage(builder);
                    }

                }

                //looking for group command
                else if (message.length() >= 4 && message.substring(1, 4).equalsIgnoreCase("lfg")) {

                    String[] fields = message.split("-");
                    if (fields.length >= 5) {

                        //- Fields 0 = empty
                        //- Fields 1 = lfg
                        //- Fields 2 = game
                        //- Fields 3 = players
                        //- Fields 4 = time

                        EmbedBuilder builder = new EmbedBuilder()
                                .setTitle(author.getDiscriminatedName() + " is Looking for a Group")
                                .setDescription("1/" + fields[3] + "players")
                                .addInlineField("Game:", fields[2])
                                .addInlineField("Time:", fields[4])
                                .setFooter("Timezone is assumed to be PST");

                        event.getChannel().sendMessage(builder);

                    } else {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setTitle("Command Error")
                                .setColor(Color.red)
                                .setDescription("Correct Format is ``-lfg -game -players -time``");
                        event.getChannel().sendMessage(builder);
                    }

                }

                //jacket command
                else if (msgFields.length == 2 && msgFields[0].equals("-jacket")) {
                    String zip = msgFields[1];
                    if (zip.length() == 5) {

                        GetRequest locRequest = Unirest.get("http://dataservice.accuweather.com/locations/v1/postalcodes/search?apikey=" + "z4ZZThcZipS7I2GGzSOAkEG8Gvb889W4" + "&q=" + zip); //TODO: put accuweather key external
                        try {
                            boolean needsJacket;
                            boolean rain;
                            String jacketWeight = "";
                            int tempF;
                            String cond;

                            String locKey = (String) locRequest.asJsonAsync().get(1, SECONDS).getBody().getArray().getJSONObject(0).get("Key");
                            GetRequest weatherRequest = Unirest.get("http://dataservice.accuweather.com/currentconditions/v1/" + locKey + "?apikey=" + "z4ZZThcZipS7I2GGzSOAkEG8Gvb889W4");
                            JSONObject weather = weatherRequest.asJsonAsync().get(1, SECONDS).getBody().getArray().getJSONObject(0);

                            rain = weather.getBoolean("HasPrecipitation");
                            needsJacket = rain;
                            tempF = weather.getJSONObject("Temperature").getJSONObject("Imperial").getInt("Value");
                            cond = weather.getString("WeatherText");

                            if (tempF <= 50) {
                                jacketWeight = "warm";
                                needsJacket = true;
                            }
                            EmbedBuilder builder = new EmbedBuilder()
                                    .setTitle(needsJacket ? "You need a " + jacketWeight + (rain ? "rain" : "") + " jacket." : "You don't need a jacket!")
                                    .setDescription("It is currently " + tempF + "°F and " + cond)
                                    .setColor(Color.blue);
                            event.getChannel().sendMessage(builder);

                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            e.printStackTrace();
                        }

                    } else {
                        EmbedBuilder builder = new EmbedBuilder()
                                .setTitle("Command Error")
                                .setColor(Color.red)
                                .setDescription("Invalid US Zip Code");
                        event.getChannel().sendMessage(builder);
                    }
                }

            }
        });
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
