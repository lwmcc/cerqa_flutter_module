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
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "iam", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
@Index(name = "undefined", fields = {"id"})
public final class Contact implements Model {
  public static final ContactPath rootPath = new ContactPath("root", false, null);
  public static final QueryField ID = field("Contact", "id");
  public static final QueryField NAME = field("Contact", "name");
  public static final QueryField PHONE = field("Contact", "phone");
  public static final QueryField EMAIL = field("Contact", "email");
  public static final QueryField USER = field("Contact", "id");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String name;
  private final @ModelField(targetType="AWSPhone") String phone;
  private final @ModelField(targetType="AWSEmail") String email;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "id", targetNames = {"id"}, type = User.class) ModelReference<User> user;
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
  
  public String getName() {
      return name;
  }
  
  public String getPhone() {
      return phone;
  }
  
  public String getEmail() {
      return email;
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
  
  private Contact(String id, String name, String phone, String email, ModelReference<User> user) {
    this.id = id;
    this.name = name;
    this.phone = phone;
    this.email = email;
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
              ObjectsCompat.equals(getName(), contact.getName()) &&
              ObjectsCompat.equals(getPhone(), contact.getPhone()) &&
              ObjectsCompat.equals(getEmail(), contact.getEmail()) &&
              ObjectsCompat.equals(getUser(), contact.getUser()) &&
              ObjectsCompat.equals(getCreatedAt(), contact.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), contact.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getPhone())
      .append(getEmail())
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
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("phone=" + String.valueOf(getPhone()) + ", ")
      .append("email=" + String.valueOf(getEmail()) + ", ")
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
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      phone,
      email,
      user);
  }
  public interface BuildStep {
    Contact build();
    BuildStep id(String id);
    BuildStep name(String name);
    BuildStep phone(String phone);
    BuildStep email(String email);
    BuildStep user(User user);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String name;
    private String phone;
    private String email;
    private ModelReference<User> user;
    public Builder() {
      
    }
    
    private Builder(String id, String name, String phone, String email, ModelReference<User> user) {
      this.id = id;
      this.name = name;
      this.phone = phone;
      this.email = email;
      this.user = user;
    }
    
    @Override
     public Contact build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Contact(
          id,
          name,
          phone,
          email,
          user);
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
     public BuildStep email(String email) {
        this.email = email;
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
    private CopyOfBuilder(String id, String name, String phone, String email, ModelReference<User> user) {
      super(id, name, phone, email, user);
      
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
     public CopyOfBuilder email(String email) {
      return (CopyOfBuilder) super.email(email);
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
