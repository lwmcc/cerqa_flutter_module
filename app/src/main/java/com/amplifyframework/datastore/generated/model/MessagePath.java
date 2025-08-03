package com.amplifyframework.datastore.generated.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.model.ModelPath;
import com.amplifyframework.core.model.PropertyPath;

/** This is an auto generated class representing the ModelPath for the Message type in your schema. */
public final class MessagePath extends ModelPath<Message> {
  private UserPath sender;
  private ChannelPath channel;
  MessagePath(@NonNull String name, @NonNull Boolean isCollection, @Nullable PropertyPath parent) {
    super(name, isCollection, parent, Message.class);
  }
  
  public synchronized UserPath getSender() {
    if (sender == null) {
      sender = new UserPath("sender", false, this);
    }
    return sender;
  }
  
  public synchronized ChannelPath getChannel() {
    if (channel == null) {
      channel = new ChannelPath("channel", false, this);
    }
    return channel;
  }
}
