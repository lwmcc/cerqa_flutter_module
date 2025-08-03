package com.amplifyframework.datastore.generated.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.model.ModelPath;
import com.amplifyframework.core.model.PropertyPath;

/** This is an auto generated class representing the ModelPath for the UserChannel type in your schema. */
public final class UserChannelPath extends ModelPath<UserChannel> {
  private UserPath user;
  private ChannelPath channel;
  UserChannelPath(@NonNull String name, @NonNull Boolean isCollection, @Nullable PropertyPath parent) {
    super(name, isCollection, parent, UserChannel.class);
  }
  
  public synchronized UserPath getUser() {
    if (user == null) {
      user = new UserPath("user", false, this);
    }
    return user;
  }
  
  public synchronized ChannelPath getChannel() {
    if (channel == null) {
      channel = new ChannelPath("channel", false, this);
    }
    return channel;
  }
}
