package net.bjoernpetersen.musicbot.spi.player

import net.bjoernpetersen.musicbot.api.player.PlayerState
import net.bjoernpetersen.musicbot.api.player.StopState

typealias PlayerStateListener = (oldState: PlayerState, newState: PlayerState) -> Unit

interface Player {

    val state: PlayerState

    /**
     * Start auto-playing.
     */
    fun start()

    /**
     * Adds a [PlayerStateListener] which will be called every time the [state] changes.
     */
    fun addListener(listener: PlayerStateListener)

    /**
     * Removes a [PlayerStateListener] previously registered with [addListener].
     */
    fun removeListener(listener: PlayerStateListener)

    /**
     * Pauses the player.
     *
     * If the player is not currently playing anything, nothing will be done.
     *
     * This method blocks until the playback is paused.
     */
    suspend fun pause()

    /**
     * Resumes the playback.
     *
     * If the player is not currently pausing, nothing will be done.
     *
     * This method blocks until the playback is resumed.
     */
    suspend fun play()

    /**
     * Plays the next song.
     *
     * This method will play the next song from the queue.
     * If the queue is empty, the next suggested song from the primary suggester will be used.
     * If there is no primary suggester, the player will transition into the [StopState].
     *
     * This method blocks until either a new song is playing or the StopState is reached.
     */
    suspend fun next()

    suspend fun close()
}
