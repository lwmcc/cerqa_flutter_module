package com.amplifyframework.datastore.generated.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.model.ModelPath;
import com.amplifyframework.core.model.PropertyPath;

/** This is an auto generated class representing the ModelPath for the User type in your schema. */
public final class UserPath extends ModelPath<User> {
  private UserContactPath contacts;
  private UserContactPath asContact;
  private UserGroupPath groups;
  private InvitePath invites;
  private UserChannelPath channels;
  private ChannelPath channelsCreated;
  private MessagePath messagesSent;
  UserPath(@NonNull String name, @NonNull Boolean isCollection, @Nullable PropertyPath parent) {
    super(name, isCollection, parent, User.class);
  }
  
  public synchronized UserContactPath getContacts() {
    if (contacts == null) {
      contacts = new UserContactPath("contacts", true, this);
    }
    return contacts;
  }
  
  public synchronized UserContactPath getAsContact() {
    if (asContact == null) {
      asContact = new UserContactPath("asContact", true, this);
    }
    return asContact;
  }
  
  public synchronized UserGroupPath getGroups() {
    if (groups == null) {
      groups = new UserGroupPath("groups", true, this);
    }
    return groups;
  }
  
  public synchronized InvitePath getInvites() {
    if (invites == null) {
      invites = new InvitePath("invites", true, this);
    }
    return invites;
  }
  
  public synchronized UserChannelPath getChannels() {
    if (channels == null) {
      channels = new UserChannelPath("channels", true, this);
    }
    return channels;
  }
  
  public synchronized ChannelPath getChannelsCreated() {
    if (channelsCreated == null) {
      channelsCreated = new ChannelPath("channelsCreated", true, this);
    }
    return channelsCreated;
  }
  
  public synchronized MessagePath getMessagesSent() {
    if (messagesSent == null) {
      messagesSent = new MessagePath("messagesSent", true, this);
    }
    return messagesSent;
  }
}
