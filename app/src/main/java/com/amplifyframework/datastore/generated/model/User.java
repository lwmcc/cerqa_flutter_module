package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.HasMany;
import com.amplifyframework.core.model.ModelList;
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

/** This is an auto generated class representing the User type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Users", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "apiKey", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
@Index(name = "undefined", fields = {"id"})
public final class User implements Model {
  public static final UserPath rootPath = new UserPath("root", false, null);
  public static final QueryField ID = field("User", "id");
  public static final QueryField USER_ID = field("User", "userId");
  public static final QueryField FIRST_NAME = field("User", "firstName");
  public static final QueryField LAST_NAME = field("User", "lastName");
  public static final QueryField NAME = field("User", "name");
  public static final QueryField PHONE = field("User", "phone");
  public static final QueryField USER_NAME = field("User", "userName");
  public static final QueryField EMAIL = field("User", "email");
  public static final QueryField AVATAR_URI = field("User", "avatarUri");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String userId;
  private final @ModelField(targetType="String", isRequired = true) String firstName;
  private final @ModelField(targetType="String", isRequired = true) String lastName;
  private final @ModelField(targetType="String") String name;
  private final @ModelField(targetType="String") String phone;
  private final @ModelField(targetType="String") String userName;
  private final @ModelField(targetType="AWSEmail") String email;
  private final @ModelField(targetType="AWSURL") String avatarUri;
  private final @ModelField(targetType="UserContact") @HasMany(associatedWith = "user", type = UserContact.class) ModelList<UserContact> contacts = null;
  private final @ModelField(targetType="UserContact") @HasMany(associatedWith = "contact", type = UserContact.class) ModelList<UserContact> asContact = null;
  private final @ModelField(targetType="UserGroup") @HasMany(associatedWith = "user", type = UserGroup.class) ModelList<UserGroup> groups = null;
  private final @ModelField(targetType="Invite") @HasMany(associatedWith = "user", type = Invite.class) ModelList<Invite> invites = null;
  private final @ModelField(targetType="Channel") @HasMany(associatedWith = "user", type = Channel.class) ModelList<Channel> channels = null;
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
  
  public String getUserId() {
      return userId;
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
  
  public String getUserName() {
      return userName;
  }
  
  public String getEmail() {
      return email;
  }
  
  public String getAvatarUri() {
      return avatarUri;
  }
  
  public ModelList<UserContact> getContacts() {
      return contacts;
  }
  
  public ModelList<UserContact> getAsContact() {
      return asContact;
  }
  
  public ModelList<UserGroup> getGroups() {
      return groups;
  }
  
  public ModelList<Invite> getInvites() {
      return invites;
  }
  
  public ModelList<Channel> getChannels() {
      return channels;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private User(String id, String userId, String firstName, String lastName, String name, String phone, String userName, String email, String avatarUri) {
    this.id = id;
    this.userId = userId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.name = name;
    this.phone = phone;
    this.userName = userName;
    this.email = email;
    this.avatarUri = avatarUri;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      User user = (User) obj;
      return ObjectsCompat.equals(getId(), user.getId()) &&
              ObjectsCompat.equals(getUserId(), user.getUserId()) &&
              ObjectsCompat.equals(getFirstName(), user.getFirstName()) &&
              ObjectsCompat.equals(getLastName(), user.getLastName()) &&
              ObjectsCompat.equals(getName(), user.getName()) &&
              ObjectsCompat.equals(getPhone(), user.getPhone()) &&
              ObjectsCompat.equals(getUserName(), user.getUserName()) &&
              ObjectsCompat.equals(getEmail(), user.getEmail()) &&
              ObjectsCompat.equals(getAvatarUri(), user.getAvatarUri()) &&
              ObjectsCompat.equals(getCreatedAt(), user.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), user.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getUserId())
      .append(getFirstName())
      .append(getLastName())
      .append(getName())
      .append(getPhone())
      .append(getUserName())
      .append(getEmail())
      .append(getAvatarUri())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("User {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("userId=" + String.valueOf(getUserId()) + ", ")
      .append("firstName=" + String.valueOf(getFirstName()) + ", ")
      .append("lastName=" + String.valueOf(getLastName()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("phone=" + String.valueOf(getPhone()) + ", ")
      .append("userName=" + String.valueOf(getUserName()) + ", ")
      .append("email=" + String.valueOf(getEmail()) + ", ")
      .append("avatarUri=" + String.valueOf(getAvatarUri()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static UserIdStep builder() {
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
  public static User justId(String id) {
    return new User(
      id,
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
      userId,
      firstName,
      lastName,
      name,
      phone,
      userName,
      email,
      avatarUri);
  }
  public interface UserIdStep {
    FirstNameStep userId(String userId);
  }
  

  public interface FirstNameStep {
    LastNameStep firstName(String firstName);
  }
  

  public interface LastNameStep {
    BuildStep lastName(String lastName);
  }
  

  public interface BuildStep {
    User build();
    BuildStep id(String id);
    BuildStep name(String name);
    BuildStep phone(String phone);
    BuildStep userName(String userName);
    BuildStep email(String email);
    BuildStep avatarUri(String avatarUri);
  }
  

  public static class Builder implements UserIdStep, FirstNameStep, LastNameStep, BuildStep {
    private String id;
    private String userId;
    private String firstName;
    private String lastName;
    private String name;
    private String phone;
    private String userName;
    private String email;
    private String avatarUri;
    public Builder() {
      
    }
    
    private Builder(String id, String userId, String firstName, String lastName, String name, String phone, String userName, String email, String avatarUri) {
      this.id = id;
      this.userId = userId;
      this.firstName = firstName;
      this.lastName = lastName;
      this.name = name;
      this.phone = phone;
      this.userName = userName;
      this.email = email;
      this.avatarUri = avatarUri;
    }
    
    @Override
     public User build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new User(
          id,
          userId,
          firstName,
          lastName,
          name,
          phone,
          userName,
          email,
          avatarUri);
    }
    
    @Override
     public FirstNameStep userId(String userId) {
        Objects.requireNonNull(userId);
        this.userId = userId;
        return this;
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
     public BuildStep userName(String userName) {
        this.userName = userName;
        return this;
    }
    
    @Override
     public BuildStep email(String email) {
        this.email = email;
        return this;
    }
    
    @Override
     public BuildStep avatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
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
    private CopyOfBuilder(String id, String userId, String firstName, String lastName, String name, String phone, String userName, String email, String avatarUri) {
      super(id, userId, firstName, lastName, name, phone, userName, email, avatarUri);
      Objects.requireNonNull(userId);
      Objects.requireNonNull(firstName);
      Objects.requireNonNull(lastName);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
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
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder phone(String phone) {
      return (CopyOfBuilder) super.phone(phone);
    }
    
    @Override
     public CopyOfBuilder userName(String userName) {
      return (CopyOfBuilder) super.userName(userName);
    }
    
    @Override
     public CopyOfBuilder email(String email) {
      return (CopyOfBuilder) super.email(email);
    }
    
    @Override
     public CopyOfBuilder avatarUri(String avatarUri) {
      return (CopyOfBuilder) super.avatarUri(avatarUri);
    }
  }
  

  public static class UserIdentifier extends ModelIdentifier<User> {
    private static final long serialVersionUID = 1L;
    public UserIdentifier(String id) {
      super(id);
    }
  }
  
}
