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

/** This is an auto generated class representing the Contact type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Contacts", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "apiKey", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
public final class Contact implements Model {
  public static final ContactPath rootPath = new ContactPath("root", false, null);
  public static final QueryField ID = field("Contact", "id");
  public static final QueryField CONTACT_ID = field("Contact", "contactId");
  //public static final QueryField USER = field("Contact", "userId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="ID") String contactId;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "userId", targetNames = {"userId"}, type = User.class) ModelReference<User> user;
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
  
  public String getContactId() {
      return contactId;
  }
  
  public ModelReference<User> getUser() {
      return user;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Contact(String id, String contactId, ModelReference<User> user) {
    this.id = id;
    this.contactId = contactId;
    this.user = user;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Contact contact = (Contact) obj;
      return ObjectsCompat.equals(getId(), contact.getId()) &&
              ObjectsCompat.equals(getContactId(), contact.getContactId()) &&
              ObjectsCompat.equals(getUser(), contact.getUser()) &&
              ObjectsCompat.equals(getCreatedAt(), contact.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), contact.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getContactId())
      .append(getUser())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Contact {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("contactId=" + String.valueOf(getContactId()) + ", ")
      .append("user=" + String.valueOf(getUser()) + ", ")
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
  public static Contact justId(String id) {
    return new Contact(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      contactId,
      user);
  }
  public interface BuildStep {
    Contact build();
    BuildStep id(String id);
    BuildStep contactId(String contactId);
    BuildStep user(User user);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String contactId;
    private ModelReference<User> user;
    public Builder() {
      
    }
    
    private Builder(String id, String contactId, ModelReference<User> user) {
      this.id = id;
      this.contactId = contactId;
      this.user = user;
    }
    
    @Override
     public Contact build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Contact(
          id,
          contactId,
          user);
    }
    
    @Override
     public BuildStep contactId(String contactId) {
        this.contactId = contactId;
        return this;
    }
    
    @Override
     public BuildStep user(User user) {
        this.user = new LoadedModelReferenceImpl<>(user);
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
    private CopyOfBuilder(String id, String contactId, ModelReference<User> user) {
      super(id, contactId, user);
      
    }
    
    @Override
     public CopyOfBuilder contactId(String contactId) {
      return (CopyOfBuilder) super.contactId(contactId);
    }
    
    @Override
     public CopyOfBuilder user(User user) {
      return (CopyOfBuilder) super.user(user);
    }
  }
  

  public static class ContactIdentifier extends ModelIdentifier<Contact> {
    private static final long serialVersionUID = 1L;
    public ContactIdentifier(String id) {
      super(id);
    }
  }
  
}
