package com.amplifyframework.datastore.generated.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.model.ModelPath;
import com.amplifyframework.core.model.PropertyPath;

/** This is an auto generated class representing the ModelPath for the Channel type in your schema. */
public final class ChannelPath extends ModelPath<Channel> {
  private MessagePath messages;
  private UserChannelPath channels;
  private UserPath creator;
  private UserPath receiver;
  ChannelPath(@NonNull String name, @NonNull Boolean isCollection, @Nullable PropertyPath parent) {
    super(name, isCollection, parent, Channel.class);
  }
  
  public synchronized MessagePath getMessages() {
    if (messages == null) {
      messages = new MessagePath("messages", true, this);
    }
    return messages;
  }
  
  public synchronized UserChannelPath getChannels() {
    if (channels == null) {
      channels = new UserChannelPath("channels", true, this);
    }
    return channels;
  }
  
  public synchronized UserPath getCreator() {
    if (creator == null) {
      creator = new UserPath("creator", false, this);
    }
    return creator;
  }
  
  public synchronized UserPath getReceiver() {
    if (receiver == null) {
      receiver = new UserPath("receiver", false, this);
    }
    return receiver;
  }
}
