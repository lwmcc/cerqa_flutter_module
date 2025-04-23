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
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="ID", isRequired = true) String contactId;
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
  
  public ModelList<UserContact> getUsers() {
      return users;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Contact(String id, String contactId) {
    this.id = id;
    this.contactId = contactId;
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
              ObjectsCompat.equals(getCreatedAt(), contact.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), contact.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getContactId())
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
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      contactId);
  }
  public interface ContactIdStep {
    BuildStep contactId(String contactId);
  }
  

  public interface BuildStep {
    Contact build();
    BuildStep id(String id);
  }
  

  public static class Builder implements ContactIdStep, BuildStep {
    private String id;
    private String contactId;
    public Builder() {
      
    }
    
    private Builder(String id, String contactId) {
      this.id = id;
      this.contactId = contactId;
    }
    
    @Override
     public Contact build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Contact(
          id,
          contactId);
    }
    
    @Override
     public BuildStep contactId(String contactId) {
        Objects.requireNonNull(contactId);
        this.contactId = contactId;
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
    private CopyOfBuilder(String id, String contactId) {
      super(id, contactId);
      Objects.requireNonNull(contactId);
    }
    
    @Override
     public CopyOfBuilder contactId(String contactId) {
      return (CopyOfBuilder) super.contactId(contactId);
    }
  }
  

  public static class ContactIdentifier extends ModelIdentifier<Contact> {
    private static final long serialVersionUID = 1L;
    public ContactIdentifier(String id) {
      super(id);
    }
  }
  
}
