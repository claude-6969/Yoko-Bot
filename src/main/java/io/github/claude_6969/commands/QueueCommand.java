package io.github.claude_6969.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.claude_6969.Colors;
import io.github.claude_6969.lavaplayer.GuildMusicManager;
import io.github.claude_6969.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueCommand extends Command {

    private final Paginator.Builder builder;
    private final EventWaiter waiter;

    public QueueCommand(EventWaiter _waiter) {
        this.name = "queue";
        this.aliases = new String[] { "q", "list" };
        this.guildOnly = true;
        this.waiter = _waiter;
        this.builder = new Paginator.Builder();
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(commandEvent.getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
        AudioTrack np = musicManager.player.getPlayingTrack();
        if (queue.isEmpty()) {
            commandEvent.getChannel().sendMessage(new EmbedBuilder()
                    .setDescription("Currently Playing : [**%s**](%s) - `%s`".formatted(np.getInfo().title, np.getInfo().uri, millisToMMSS(np.getInfo().length)))
                    .setColor(Colors.Blue())
                    .build()).queue();
            return;
        }
        int pagenum = 1;
        try {
            pagenum = Integer.parseInt(commandEvent.getArgs());
        } catch (NumberFormatException ignored) {}
        List<AudioTrack> list = new ArrayList<>(queue);
        String[] songs = new String[list.size()];
        int j = 0;
        for (int i = 0; i < list.size(); i++) {
            j++;
            AudioTrack song = list.get(i);
            songs[i] = "%s. [**%s**](%s) - `%s`".formatted(j, song.getInfo().title, song.getInfo().uri, millisToMMSS(song.getInfo().length));
        }
        builder.setText("Queue for \"%s\"".formatted(commandEvent.getGuild().getName()))
                .setItems(songs)
                .setColor(Colors.Blue())
                .allowTextInput(true)
                .setColumns(1)
                .setFinalAction(n -> {
                    try {
                        n.delete().queue();
                    } catch (PermissionException ignore) {
                    }
                })
                .setItemsPerPage(10)
                .waitOnSinglePage(true)
                .useNumberedItems(false)
                .showPageNumbers(true)
                .wrapPageEnds(true)
                .setEventWaiter(this.waiter)
                .setTimeout(1, TimeUnit.MINUTES);
        builder.build().paginate(commandEvent.getChannel(), pagenum);
    }

    public static String millisToMMSS(long millis) {
         long seconds = millis / 1000;
         long s = seconds % 60;
         long m = (seconds / 60) % 60;
         return "%s:%s".formatted(m, s);
    }
}
