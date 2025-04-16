package com.amplifyframework.datastore.generated.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.model.ModelPath;
import com.amplifyframework.core.model.PropertyPath;

/** This is an auto generated class representing the ModelPath for the Contact type in your schema. */
public final class ContactPath extends ModelPath<Contact> {
  private UserContactPath users;
  ContactPath(@NonNull String name, @NonNull Boolean isCollection, @Nullable PropertyPath parent) {
    super(name, isCollection, parent, Contact.class);
  }
  
  public synchronized UserContactPath getUsers() {
    if (users == null) {
      users = new UserContactPath("users", true, this);
    }
    return users;
  }
}
