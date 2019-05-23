package me.robkpaul.www.commands;

import org.javacord.api.entity.channel.VoiceChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;

public class Split {
    private Message message;
    private User author;
    private VoiceChannel redVoice;
    private VoiceChannel blueVoice;
    public Split(Message msg, VoiceChannel rVC, VoiceChannel bVC){
        message = msg;
        author = msg.getUserAuthor().isPresent() ? msg.getUserAuthor().get() : null;
        redVoice = rVC;
        blueVoice = bVC;
    }
}
