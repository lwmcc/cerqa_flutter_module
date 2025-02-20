package com.amplifyframework.datastore.generated.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.model.ModelPath;
import com.amplifyframework.core.model.PropertyPath;

/** This is an auto generated class representing the ModelPath for the UserGroup type in your schema. */
public final class UserGroupPath extends ModelPath<UserGroup> {
  private UserPath user;
  private GroupPath group;
  UserGroupPath(@NonNull String name, @NonNull Boolean isCollection, @Nullable PropertyPath parent) {
    super(name, isCollection, parent, UserGroup.class);
  }
  
  public synchronized UserPath getUser() {
    if (user == null) {
      user = new UserPath("user", false, this);
    }
    return user;
  }
  
  public synchronized GroupPath getGroup() {
    if (group == null) {
      group = new GroupPath("group", false, this);
    }
    return group;
  }
}
