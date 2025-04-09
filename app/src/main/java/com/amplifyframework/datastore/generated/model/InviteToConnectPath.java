package com.amplifyframework.datastore.generated.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.model.ModelPath;
import com.amplifyframework.core.model.PropertyPath;

/** This is an auto generated class representing the ModelPath for the InviteToConnect type in your schema. */
public final class InviteToConnectPath extends ModelPath<InviteToConnect> {
  private UserPath invites;
  InviteToConnectPath(@NonNull String name, @NonNull Boolean isCollection, @Nullable PropertyPath parent) {
    super(name, isCollection, parent, InviteToConnect.class);
  }
  
  public synchronized UserPath getInvites() {
    if (invites == null) {
      invites = new UserPath("invites", false, this);
    }
    return invites;
  }
}
