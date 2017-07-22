package com.github.bjoernpetersen.jmusicbot.provider;

import com.github.bjoernpetersen.jmusicbot.IdPlugin;
import com.github.bjoernpetersen.jmusicbot.InitStateWriter;
import com.github.bjoernpetersen.jmusicbot.InitializationException;
import com.github.bjoernpetersen.jmusicbot.NamedPlugin;
import com.github.bjoernpetersen.jmusicbot.PlaybackFactoryManager;
import com.github.bjoernpetersen.jmusicbot.Song;
import com.github.bjoernpetersen.jmusicbot.playback.PlaybackFactory;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public interface Provider extends NamedPlugin, IdPlugin {

  Set<Class<? extends PlaybackFactory>> getPlaybackDependencies();

  /**
   * Gets the base class for this provider. The base class should define relevant methods for {@link
   * Suggester} implementations depending on this provider.
   *
   * This provider must implement the returned base class.
   *
   * @return a class
   */
  @Nonnull
  Class<? extends Provider> getBaseClass();

  /**
   * Signals to the provider that it can start doing work.
   *
   * @param manager a PlaybackFactoryManager to obtain PlaybackFactory instances from
   * @param initStateWriter a writer for initialization state messages
   * @throws InitializationException If any errors occur (for example due to invalid log-in
   * credentials)
   * @throws InterruptedException if the thread is interrupted while initializing
   */
  void initialize(@Nonnull InitStateWriter initStateWriter, @Nonnull PlaybackFactoryManager manager)
      throws InitializationException, InterruptedException;

  /**
   * Searches for songs based on the given search query.
   *
   * @param query a search query
   * @return a list of songs
   */
  @Nonnull
  List<Song> search(@Nonnull String query);

  /**
   * Looks up a song by its ID.
   *
   * @param id the song ID
   * @return the song with the specified ID
   * @throws NoSuchSongException if the ID is invalid
   */
  @Nonnull
  Song lookup(@Nonnull String id) throws NoSuchSongException;

  @Deprecated
  @Nonnull
  @Override
  default String getName() {
    // new implementing classes should not be forced to override this method
    return getClass().getSimpleName();
  }
}
