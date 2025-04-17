package com.amplifyframework.datastore.generated.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.model.ModelPath;
import com.amplifyframework.core.model.PropertyPath;

/** This is an auto generated class representing the ModelPath for the Contact type in your schema. */
public final class ContactPath extends ModelPath<Contact> {
  private UserPath user;
  ContactPath(@NonNull String name, @NonNull Boolean isCollection, @Nullable PropertyPath parent) {
    super(name, isCollection, parent, Contact.class);
  }
  
  public synchronized UserPath getUser() {
    if (user == null) {
      user = new UserPath("user", false, this);
    }
    return user;
  }
}
