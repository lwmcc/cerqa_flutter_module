package com.amplifyframework.datastore.generated.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.model.ModelPath;
import com.amplifyframework.core.model.PropertyPath;

/** This is an auto generated class representing the ModelPath for the UserContact type in your schema. */
public final class UserContactPath extends ModelPath<UserContact> {
  private UserPath user;
  private ContactPath contact;
  UserContactPath(@NonNull String name, @NonNull Boolean isCollection, @Nullable PropertyPath parent) {
    super(name, isCollection, parent, UserContact.class);
  }
  
  public synchronized UserPath getUser() {
    if (user == null) {
      user = new UserPath("user", false, this);
    }
    return user;
  }
  
  public synchronized ContactPath getContact() {
    if (contact == null) {
      contact = new ContactPath("contact", false, this);
    }
    return contact;
  }
}
