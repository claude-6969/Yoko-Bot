package io.github.claude_6969.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.claude_6969.lavaplayer.GuildMusicManager;
import io.github.claude_6969.lavaplayer.PlayerManager;

public class StopCommand extends Command {

    public StopCommand() {
        this.name = "stop";
        this.aliases = new String[] { "dc", "disconnect" };
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(commandEvent.getGuild());
        musicManager.player.destroy();
        musicManager.scheduler.clear();
        if (commandEvent.getGuild().getAudioManager().isConnected()) {
            commandEvent.getGuild().getAudioManager().closeAudioConnection();
        }
        commandEvent.getMessage().addReaction("👍").queue();
    }
}
