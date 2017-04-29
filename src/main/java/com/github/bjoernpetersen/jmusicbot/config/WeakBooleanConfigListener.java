package com.github.bjoernpetersen.jmusicbot.config;

import java.lang.ref.WeakReference;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class WeakBooleanConfigListener implements BooleanConfigListener {

  private final WeakReference<BooleanConfigListener> listener;

  public WeakBooleanConfigListener(BooleanConfigListener listener) {
    this.listener = new WeakReference<>(listener);
  }

  @Override
  public void onChange(boolean before, boolean after) {
    BooleanConfigListener listener = this.listener.get();
    if (listener != null) {
      listener.onChange(before, after);
    }
  }
}
