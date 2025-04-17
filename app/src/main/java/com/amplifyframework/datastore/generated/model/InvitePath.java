package com.amplifyframework.datastore.generated.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.model.ModelPath;
import com.amplifyframework.core.model.PropertyPath;

/** This is an auto generated class representing the ModelPath for the Invite type in your schema. */
public final class InvitePath extends ModelPath<Invite> {
  private UserPath sender;
  private UserPath receiver;
  InvitePath(@NonNull String name, @NonNull Boolean isCollection, @Nullable PropertyPath parent) {
    super(name, isCollection, parent, Invite.class);
  }
  
  public synchronized UserPath getSender() {
    if (sender == null) {
      sender = new UserPath("sender", false, this);
    }
    return sender;
  }
  
  public synchronized UserPath getReceiver() {
    if (receiver == null) {
      receiver = new UserPath("receiver", false, this);
    }
    return receiver;
  }
}
