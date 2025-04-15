package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.ModelIdentifier;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the AppData type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "AppData", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "iam", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
@Index(name = "undefined", fields = {"id"})
public final class AppData implements Model {
  public static final AppDataPath rootPath = new AppDataPath("root", false, null);
  public static final QueryField ID = field("AppData", "id");
  public static final QueryField TYPE = field("AppData", "type");
  public static final QueryField USER_NAME = field("AppData", "userName");
  public static final QueryField USER_ID = field("AppData", "userId");
  public static final QueryField EMAIL = field("AppData", "email");
  public static final QueryField FIRST_NAME = field("AppData", "firstName");
  public static final QueryField LAST_NAME = field("AppData", "lastName");
  public static final QueryField NAME = field("AppData", "name");
  public static final QueryField PHONE = field("AppData", "phone");
  public static final QueryField AVATAR_URI = field("AppData", "avatarUri");
  public static final QueryField CONTACTS = field("AppData", "contacts");
  public static final QueryField GROUPS = field("AppData", "groups");
  public static final QueryField VEHICLES = field("AppData", "vehicles");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String type;
  private final @ModelField(targetType="String") String userName;
  private final @ModelField(targetType="String") String userId;
  private final @ModelField(targetType="AWSEmail") String email;
  private final @ModelField(targetType="String", isRequired = true) String firstName;
  private final @ModelField(targetType="String", isRequired = true) String lastName;
  private final @ModelField(targetType="String") String name;
  private final @ModelField(targetType="AWSPhone") String phone;
  private final @ModelField(targetType="AWSURL") String avatarUri;
  private final @ModelField(targetType="AWSJSON") String contacts;
  private final @ModelField(targetType="AWSJSON") String groups;
  private final @ModelField(targetType="AWSJSON") String vehicles;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public String getType() {
      return type;
  }
  
  public String getUserName() {
      return userName;
  }
  
  public String getUserId() {
      return userId;
  }
  
  public String getEmail() {
      return email;
  }
  
  public String getFirstName() {
      return firstName;
  }
  
  public String getLastName() {
      return lastName;
  }
  
  public String getName() {
      return name;
  }
  
  public String getPhone() {
      return phone;
  }
  
  public String getAvatarUri() {
      return avatarUri;
  }
  
  public String getContacts() {
      return contacts;
  }
  
  public String getGroups() {
      return groups;
  }
  
  public String getVehicles() {
      return vehicles;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private AppData(String id, String type, String userName, String userId, String email, String firstName, String lastName, String name, String phone, String avatarUri, String contacts, String groups, String vehicles) {
    this.id = id;
    this.type = type;
    this.userName = userName;
    this.userId = userId;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.name = name;
    this.phone = phone;
    this.avatarUri = avatarUri;
    this.contacts = contacts;
    this.groups = groups;
    this.vehicles = vehicles;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      AppData appData = (AppData) obj;
      return ObjectsCompat.equals(getId(), appData.getId()) &&
              ObjectsCompat.equals(getType(), appData.getType()) &&
              ObjectsCompat.equals(getUserName(), appData.getUserName()) &&
              ObjectsCompat.equals(getUserId(), appData.getUserId()) &&
              ObjectsCompat.equals(getEmail(), appData.getEmail()) &&
              ObjectsCompat.equals(getFirstName(), appData.getFirstName()) &&
              ObjectsCompat.equals(getLastName(), appData.getLastName()) &&
              ObjectsCompat.equals(getName(), appData.getName()) &&
              ObjectsCompat.equals(getPhone(), appData.getPhone()) &&
              ObjectsCompat.equals(getAvatarUri(), appData.getAvatarUri()) &&
              ObjectsCompat.equals(getContacts(), appData.getContacts()) &&
              ObjectsCompat.equals(getGroups(), appData.getGroups()) &&
              ObjectsCompat.equals(getVehicles(), appData.getVehicles()) &&
              ObjectsCompat.equals(getCreatedAt(), appData.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), appData.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getType())
      .append(getUserName())
      .append(getUserId())
      .append(getEmail())
      .append(getFirstName())
      .append(getLastName())
      .append(getName())
      .append(getPhone())
      .append(getAvatarUri())
      .append(getContacts())
      .append(getGroups())
      .append(getVehicles())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("AppData {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("type=" + String.valueOf(getType()) + ", ")
      .append("userName=" + String.valueOf(getUserName()) + ", ")
      .append("userId=" + String.valueOf(getUserId()) + ", ")
      .append("email=" + String.valueOf(getEmail()) + ", ")
      .append("firstName=" + String.valueOf(getFirstName()) + ", ")
      .append("lastName=" + String.valueOf(getLastName()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("phone=" + String.valueOf(getPhone()) + ", ")
      .append("avatarUri=" + String.valueOf(getAvatarUri()) + ", ")
      .append("contacts=" + String.valueOf(getContacts()) + ", ")
      .append("groups=" + String.valueOf(getGroups()) + ", ")
      .append("vehicles=" + String.valueOf(getVehicles()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static FirstNameStep builder() {
      return new Builder();
  }
  
  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static AppData justId(String id) {
    return new AppData(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      type,
      userName,
      userId,
      email,
      firstName,
      lastName,
      name,
      phone,
      avatarUri,
      contacts,
      groups,
      vehicles);
  }
  public interface FirstNameStep {
    LastNameStep firstName(String firstName);
  }
  

  public interface LastNameStep {
    BuildStep lastName(String lastName);
  }
  

  public interface BuildStep {
    AppData build();
    BuildStep id(String id);
    BuildStep type(String type);
    BuildStep userName(String userName);
    BuildStep userId(String userId);
    BuildStep email(String email);
    BuildStep name(String name);
    BuildStep phone(String phone);
    BuildStep avatarUri(String avatarUri);
    BuildStep contacts(String contacts);
    BuildStep groups(String groups);
    BuildStep vehicles(String vehicles);
  }
  

  public static class Builder implements FirstNameStep, LastNameStep, BuildStep {
    private String id;
    private String firstName;
    private String lastName;
    private String type;
    private String userName;
    private String userId;
    private String email;
    private String name;
    private String phone;
    private String avatarUri;
    private String contacts;
    private String groups;
    private String vehicles;
    public Builder() {
      
    }
    
    private Builder(String id, String type, String userName, String userId, String email, String firstName, String lastName, String name, String phone, String avatarUri, String contacts, String groups, String vehicles) {
      this.id = id;
      this.type = type;
      this.userName = userName;
      this.userId = userId;
      this.email = email;
      this.firstName = firstName;
      this.lastName = lastName;
      this.name = name;
      this.phone = phone;
      this.avatarUri = avatarUri;
      this.contacts = contacts;
      this.groups = groups;
      this.vehicles = vehicles;
    }
    
    @Override
     public AppData build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new AppData(
          id,
          type,
          userName,
          userId,
          email,
          firstName,
          lastName,
          name,
          phone,
          avatarUri,
          contacts,
          groups,
          vehicles);
    }
    
    @Override
     public LastNameStep firstName(String firstName) {
        Objects.requireNonNull(firstName);
        this.firstName = firstName;
        return this;
    }
    
    @Override
     public BuildStep lastName(String lastName) {
        Objects.requireNonNull(lastName);
        this.lastName = lastName;
        return this;
    }
    
    @Override
     public BuildStep type(String type) {
        this.type = type;
        return this;
    }
    
    @Override
     public BuildStep userName(String userName) {
        this.userName = userName;
        return this;
    }
    
    @Override
     public BuildStep userId(String userId) {
        this.userId = userId;
        return this;
    }
    
    @Override
     public BuildStep email(String email) {
        this.email = email;
        return this;
    }
    
    @Override
     public BuildStep name(String name) {
        this.name = name;
        return this;
    }
    
    @Override
     public BuildStep phone(String phone) {
        this.phone = phone;
        return this;
    }
    
    @Override
     public BuildStep avatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
        return this;
    }
    
    @Override
     public BuildStep contacts(String contacts) {
        this.contacts = contacts;
        return this;
    }
    
    @Override
     public BuildStep groups(String groups) {
        this.groups = groups;
        return this;
    }
    
    @Override
     public BuildStep vehicles(String vehicles) {
        this.vehicles = vehicles;
        return this;
    }
    
    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String type, String userName, String userId, String email, String firstName, String lastName, String name, String phone, String avatarUri, String contacts, String groups, String vehicles) {
      super(id, type, userName, userId, email, firstName, lastName, name, phone, avatarUri, contacts, groups, vehicles);
      Objects.requireNonNull(firstName);
      Objects.requireNonNull(lastName);
    }
    
    @Override
     public CopyOfBuilder firstName(String firstName) {
      return (CopyOfBuilder) super.firstName(firstName);
    }
    
    @Override
     public CopyOfBuilder lastName(String lastName) {
      return (CopyOfBuilder) super.lastName(lastName);
    }
    
    @Override
     public CopyOfBuilder type(String type) {
      return (CopyOfBuilder) super.type(type);
    }
    
    @Override
     public CopyOfBuilder userName(String userName) {
      return (CopyOfBuilder) super.userName(userName);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
    
    @Override
     public CopyOfBuilder email(String email) {
      return (CopyOfBuilder) super.email(email);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder phone(String phone) {
      return (CopyOfBuilder) super.phone(phone);
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
     public CopyOfBuilder groups(String groups) {
      return (CopyOfBuilder) super.groups(groups);
    }
    
    @Override
     public CopyOfBuilder vehicles(String vehicles) {
      return (CopyOfBuilder) super.vehicles(vehicles);
    }
  }
  

  public static class AppDataIdentifier extends ModelIdentifier<AppData> {
    private static final long serialVersionUID = 1L;
    public AppDataIdentifier(String id) {
      super(id);
    }
  }
  
}
