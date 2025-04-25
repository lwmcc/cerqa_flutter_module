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

/** This is an auto generated class representing the Contact type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Contacts", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "apiKey", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
public final class Contact implements Model {
  public static final ContactPath rootPath = new ContactPath("root", false, null);
  public static final QueryField ID = field("Contact", "id");
  public static final QueryField CONTACT_ID = field("Contact", "contactId");
  public static final QueryField USER_NAME = field("Contact", "userName");
  public static final QueryField FIRST_NAME = field("Contact", "firstName");
  public static final QueryField AVATAR_URI = field("Contact", "avatarUri");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="ID", isRequired = true) String contactId;
  private final @ModelField(targetType="String") String userName;
  private final @ModelField(targetType="String") String firstName;
  private final @ModelField(targetType="AWSURL") String avatarUri;
  private final @ModelField(targetType="UserContact") @HasMany(associatedWith = "contact", type = UserContact.class) ModelList<UserContact> users = null;
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
  
  public String getUserName() {
      return userName;
  }
  
  public String getFirstName() {
      return firstName;
  }
  
  public String getAvatarUri() {
      return avatarUri;
  }
  
  public ModelList<UserContact> getUsers() {
      return users;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Contact(String id, String contactId, String userName, String firstName, String avatarUri) {
    this.id = id;
    this.contactId = contactId;
    this.userName = userName;
    this.firstName = firstName;
    this.avatarUri = avatarUri;
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
              ObjectsCompat.equals(getUserName(), contact.getUserName()) &&
              ObjectsCompat.equals(getFirstName(), contact.getFirstName()) &&
              ObjectsCompat.equals(getAvatarUri(), contact.getAvatarUri()) &&
              ObjectsCompat.equals(getCreatedAt(), contact.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), contact.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getContactId())
      .append(getUserName())
      .append(getFirstName())
      .append(getAvatarUri())
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
      .append("userName=" + String.valueOf(getUserName()) + ", ")
      .append("firstName=" + String.valueOf(getFirstName()) + ", ")
      .append("avatarUri=" + String.valueOf(getAvatarUri()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static ContactIdStep builder() {
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
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      contactId,
      userName,
      firstName,
      avatarUri);
  }
  public interface ContactIdStep {
    BuildStep contactId(String contactId);
  }
  

  public interface BuildStep {
    Contact build();
    BuildStep id(String id);
    BuildStep userName(String userName);
    BuildStep firstName(String firstName);
    BuildStep avatarUri(String avatarUri);
  }
  

  public static class Builder implements ContactIdStep, BuildStep {
    private String id;
    private String contactId;
    private String userName;
    private String firstName;
    private String avatarUri;
    public Builder() {
      
    }
    
    private Builder(String id, String contactId, String userName, String firstName, String avatarUri) {
      this.id = id;
      this.contactId = contactId;
      this.userName = userName;
      this.firstName = firstName;
      this.avatarUri = avatarUri;
    }
    
    @Override
     public Contact build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Contact(
          id,
          contactId,
          userName,
          firstName,
          avatarUri);
    }
    
    @Override
     public BuildStep contactId(String contactId) {
        Objects.requireNonNull(contactId);
        this.contactId = contactId;
        return this;
    }
    
    @Override
     public BuildStep userName(String userName) {
        this.userName = userName;
        return this;
    }
    
    @Override
     public BuildStep firstName(String firstName) {
        this.firstName = firstName;
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
    private CopyOfBuilder(String id, String contactId, String userName, String firstName, String avatarUri) {
      super(id, contactId, userName, firstName, avatarUri);
      Objects.requireNonNull(contactId);
    }
    
    @Override
     public CopyOfBuilder contactId(String contactId) {
      return (CopyOfBuilder) super.contactId(contactId);
    }
    
    @Override
     public CopyOfBuilder userName(String userName) {
      return (CopyOfBuilder) super.userName(userName);
    }
    
    @Override
     public CopyOfBuilder firstName(String firstName) {
      return (CopyOfBuilder) super.firstName(firstName);
    }
    
    @Override
     public CopyOfBuilder avatarUri(String avatarUri) {
      return (CopyOfBuilder) super.avatarUri(avatarUri);
    }
  }
  

  public static class ContactIdentifier extends ModelIdentifier<Contact> {
    private static final long serialVersionUID = 1L;
    public ContactIdentifier(String id) {
      super(id);
    }
  }
  
}
