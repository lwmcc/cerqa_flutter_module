package com.amplifyframework.datastore.generated.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.model.ModelPath;
import com.amplifyframework.core.model.PropertyPath;

/** This is an auto generated class representing the ModelPath for the Group type in your schema. */
public final class GroupPath extends ModelPath<Group> {
  private UserGroupPath users;
  GroupPath(@NonNull String name, @NonNull Boolean isCollection, @Nullable PropertyPath parent) {
    super(name, isCollection, parent, Group.class);
  }
  
  public synchronized UserGroupPath getUsers() {
    if (users == null) {
      users = new UserGroupPath("users", true, this);
    }
    return users;
  }
}
