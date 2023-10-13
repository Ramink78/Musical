package rk.musical.player

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import rk.musical.R

@UnstableApi
class MusicalNotificationProvider(
    context: Context
) : DefaultMediaNotificationProvider(context) {
    override fun getMediaButtons(
        session: MediaSession,
        playerCommands: Player.Commands,
        customLayout: ImmutableList<CommandButton>,
        showPauseButton: Boolean
    ): ImmutableList<CommandButton> {
        return getCommands(showPauseButton, playerCommands)
    }

    private fun getCommands(
        showPauseButton: Boolean,
        playerCommands: Player.Commands
    ): ImmutableList<CommandButton> {
        val commands = mutableListOf<CommandButton>()
        val play =
            CommandButton.Builder()
                .setPlayerCommand(Player.COMMAND_PLAY_PAUSE)
                .setIconResId(
                    if (showPauseButton) {
                        R.drawable.round_pause_circle_24
                    } else {
                        R.drawable.round_play_circle_filled_24
                    }
                )
                .build()
        if (playerCommands.containsAny(
                Player.COMMAND_SEEK_TO_NEXT,
                Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
            )
        ) {
            val next =
                CommandButton.Builder()
                    .setPlayerCommand(Player.COMMAND_SEEK_TO_NEXT)
                    .setIconResId(R.drawable.round_skip_next_24)
                    .build()
            commands.add(next)
        }

        if (playerCommands.containsAny(
                Player.COMMAND_SEEK_TO_PREVIOUS,
                Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
            )
        ) {
            val previous =
                CommandButton.Builder()
                    .setPlayerCommand(Player.COMMAND_SEEK_TO_PREVIOUS)
                    .setIconResId(R.drawable.round_skip_previous_24)
                    .build()
            commands.add(previous)
        }

        commands.add(play)
        return ImmutableList.copyOf(commands)
    }
}
