package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;
import com.amplifyframework.core.model.ModelReference;
import com.amplifyframework.core.model.LoadedModelReferenceImpl;
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

/** This is an auto generated class representing the UserContact type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "UserContacts", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "apiKey", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
public final class UserContact implements Model {
  public static final UserContactPath rootPath = new UserContactPath("root", false, null);
  public static final QueryField ID = field("UserContact", "id");
  public static final QueryField USER = field("UserContact", "userId");
  public static final QueryField CONTACT = field("UserContact", "contactId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "userId", targetNames = {"userId"}, type = User.class) ModelReference<User> user;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "contactId", targetNames = {"contactId"}, type = User.class) ModelReference<User> contact;
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
  
  public ModelReference<User> getUser() {
      return user;
  }
  
  public ModelReference<User> getContact() {
      return contact;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private UserContact(String id, ModelReference<User> user, ModelReference<User> contact) {
    this.id = id;
    this.user = user;
    this.contact = contact;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      UserContact userContact = (UserContact) obj;
      return ObjectsCompat.equals(getId(), userContact.getId()) &&
              ObjectsCompat.equals(getUser(), userContact.getUser()) &&
              ObjectsCompat.equals(getContact(), userContact.getContact()) &&
              ObjectsCompat.equals(getCreatedAt(), userContact.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), userContact.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getUser())
      .append(getContact())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("UserContact {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("user=" + String.valueOf(getUser()) + ", ")
      .append("contact=" + String.valueOf(getContact()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static BuildStep builder() {
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
  public static UserContact justId(String id) {
    return new UserContact(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      user,
      contact);
  }
  public interface BuildStep {
    UserContact build();
    BuildStep id(String id);
    BuildStep user(User user);
    BuildStep contact(User contact);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private ModelReference<User> user;
    private ModelReference<User> contact;
    public Builder() {
      
    }
    
    private Builder(String id, ModelReference<User> user, ModelReference<User> contact) {
      this.id = id;
      this.user = user;
      this.contact = contact;
    }
    
    @Override
     public UserContact build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new UserContact(
          id,
          user,
          contact);
    }
    
    @Override
     public BuildStep user(User user) {
        this.user = new LoadedModelReferenceImpl<>(user);
        return this;
    }
    
    @Override
     public BuildStep contact(User contact) {
        this.contact = new LoadedModelReferenceImpl<>(contact);
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
    private CopyOfBuilder(String id, ModelReference<User> user, ModelReference<User> contact) {
      super(id, user, contact);
      
    }
    
    @Override
     public CopyOfBuilder user(User user) {
      return (CopyOfBuilder) super.user(user);
    }
    
    @Override
     public CopyOfBuilder contact(User contact) {
      return (CopyOfBuilder) super.contact(contact);
    }
  }
  

  public static class UserContactIdentifier extends ModelIdentifier<UserContact> {
    private static final long serialVersionUID = 1L;
    public UserContactIdentifier(String id) {
      super(id);
    }
  }
  
}
