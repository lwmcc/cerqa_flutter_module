package com.amplifyframework.datastore.generated.model;


import androidx.core.util.ObjectsCompat;

import java.util.Objects;
import java.util.List;

/** This is an auto generated class representing the Users type in your schema. */
public final class Users {
  private final String avatar_uri;
  private final String contacts;
  private final String email;
  private final String first_name;
  private final String groups;
  private final String last_name;
  private final String name;
  private final String phone;
  private final String user_id;
  private final String vehicles;
  public String getAvatarUri() {
      return avatar_uri;
  }
  
  public String getContacts() {
      return contacts;
  }
  
  public String getEmail() {
      return email;
  }
  
  public String getFirstName() {
      return first_name;
  }
  
  public String getGroups() {
      return groups;
  }
  
  public String getLastName() {
      return last_name;
  }
  
  public String getName() {
      return name;
  }
  
  public String getPhone() {
      return phone;
  }
  
  public String getUserId() {
      return user_id;
  }
  
  public String getVehicles() {
      return vehicles;
  }
  
  private Users(String avatar_uri, String contacts, String email, String first_name, String groups, String last_name, String name, String phone, String user_id, String vehicles) {
    this.avatar_uri = avatar_uri;
    this.contacts = contacts;
    this.email = email;
    this.first_name = first_name;
    this.groups = groups;
    this.last_name = last_name;
    this.name = name;
    this.phone = phone;
    this.user_id = user_id;
    this.vehicles = vehicles;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Users users = (Users) obj;
      return ObjectsCompat.equals(getAvatarUri(), users.getAvatarUri()) &&
              ObjectsCompat.equals(getContacts(), users.getContacts()) &&
              ObjectsCompat.equals(getEmail(), users.getEmail()) &&
              ObjectsCompat.equals(getFirstName(), users.getFirstName()) &&
              ObjectsCompat.equals(getGroups(), users.getGroups()) &&
              ObjectsCompat.equals(getLastName(), users.getLastName()) &&
              ObjectsCompat.equals(getName(), users.getName()) &&
              ObjectsCompat.equals(getPhone(), users.getPhone()) &&
              ObjectsCompat.equals(getUserId(), users.getUserId()) &&
              ObjectsCompat.equals(getVehicles(), users.getVehicles());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getAvatarUri())
      .append(getContacts())
      .append(getEmail())
      .append(getFirstName())
      .append(getGroups())
      .append(getLastName())
      .append(getName())
      .append(getPhone())
      .append(getUserId())
      .append(getVehicles())
      .toString()
      .hashCode();
  }
  
  public static FirstNameStep builder() {
      return new Builder();
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(avatar_uri,
      contacts,
      email,
      first_name,
      groups,
      last_name,
      name,
      phone,
      user_id,
      vehicles);
  }
  public interface FirstNameStep {
    NameStep firstName(String firstName);
  }
  

  public interface NameStep {
    UserIdStep name(String name);
  }
  

  public interface UserIdStep {
    BuildStep userId(String userId);
  }
  

  public interface BuildStep {
    Users build();
    BuildStep avatarUri(String avatarUri);
    BuildStep contacts(String contacts);
    BuildStep email(String email);
    BuildStep groups(String groups);
    BuildStep lastName(String lastName);
    BuildStep phone(String phone);
    BuildStep vehicles(String vehicles);
  }
  

  public static class Builder implements FirstNameStep, NameStep, UserIdStep, BuildStep {
    private String first_name;
    private String name;
    private String user_id;
    private String avatar_uri;
    private String contacts;
    private String email;
    private String groups;
    private String last_name;
    private String phone;
    private String vehicles;
    public Builder() {
      
    }
    
    private Builder(String avatar_uri, String contacts, String email, String first_name, String groups, String last_name, String name, String phone, String user_id, String vehicles) {
      this.avatar_uri = avatar_uri;
      this.contacts = contacts;
      this.email = email;
      this.first_name = first_name;
      this.groups = groups;
      this.last_name = last_name;
      this.name = name;
      this.phone = phone;
      this.user_id = user_id;
      this.vehicles = vehicles;
    }
    
    @Override
     public Users build() {
        
        return new Users(
          avatar_uri,
          contacts,
          email,
          first_name,
          groups,
          last_name,
          name,
          phone,
          user_id,
          vehicles);
    }
    
    @Override
     public NameStep firstName(String firstName) {
        Objects.requireNonNull(firstName);
        this.first_name = firstName;
        return this;
    }
    
    @Override
     public UserIdStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public BuildStep userId(String userId) {
        Objects.requireNonNull(userId);
        this.user_id = userId;
        return this;
    }
    
    @Override
     public BuildStep avatarUri(String avatarUri) {
        this.avatar_uri = avatarUri;
        return this;
    }
    
    @Override
     public BuildStep contacts(String contacts) {
        this.contacts = contacts;
        return this;
    }
    
    @Override
     public BuildStep email(String email) {
        this.email = email;
        return this;
    }
    
    @Override
     public BuildStep groups(String groups) {
        this.groups = groups;
        return this;
    }
    
    @Override
     public BuildStep lastName(String lastName) {
        this.last_name = lastName;
        return this;
    }
    
    @Override
     public BuildStep phone(String phone) {
        this.phone = phone;
        return this;
    }
    
    @Override
     public BuildStep vehicles(String vehicles) {
        this.vehicles = vehicles;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String avatarUri, String contacts, String email, String firstName, String groups, String lastName, String name, String phone, String userId, String vehicles) {
      super(avatar_uri, contacts, email, first_name, groups, last_name, name, phone, user_id, vehicles);
      Objects.requireNonNull(first_name);
      Objects.requireNonNull(name);
      Objects.requireNonNull(user_id);
    }
    
    @Override
     public CopyOfBuilder firstName(String firstName) {
      return (CopyOfBuilder) super.firstName(firstName);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
    
    @Override
     public CopyOfBuilder avatarUri(String avatarUri) {
      return (CopyOfBuilder) super.avatarUri(avatarUri);
    }
    
    @Override
     public CopyOfBuilder contacts(String contacts) {
      return (CopyOfBuilder) super.contacts(contacts);
    }
    
    @Override
     public CopyOfBuilder email(String email) {
      return (CopyOfBuilder) super.email(email);
    }
    
    @Override
     public CopyOfBuilder groups(String groups) {
      return (CopyOfBuilder) super.groups(groups);
    }
    
    @Override
     public CopyOfBuilder lastName(String lastName) {
      return (CopyOfBuilder) super.lastName(lastName);
    }
    
    @Override
     public CopyOfBuilder phone(String phone) {
      return (CopyOfBuilder) super.phone(phone);
    }
    
    @Override
     public CopyOfBuilder vehicles(String vehicles) {
      return (CopyOfBuilder) super.vehicles(vehicles);
    }
  }
  
}
