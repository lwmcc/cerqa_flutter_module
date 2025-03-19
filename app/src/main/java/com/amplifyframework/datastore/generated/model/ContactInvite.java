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

/** This is an auto generated class representing the ContactInvite type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "ContactInvites", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "apiKey", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
public final class ContactInvite implements Model {
  public static final ContactInvitePath rootPath = new ContactInvitePath("root", false, null);
  public static final QueryField ID = field("ContactInvite", "id");
  public static final QueryField SUCCESS = field("ContactInvite", "success");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Boolean") Boolean success;
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
  
  public Boolean getSuccess() {
      return success;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private ContactInvite(String id, Boolean success) {
    this.id = id;
    this.success = success;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      ContactInvite contactInvite = (ContactInvite) obj;
      return ObjectsCompat.equals(getId(), contactInvite.getId()) &&
              ObjectsCompat.equals(getSuccess(), contactInvite.getSuccess()) &&
              ObjectsCompat.equals(getCreatedAt(), contactInvite.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), contactInvite.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getSuccess())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("ContactInvite {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("success=" + String.valueOf(getSuccess()) + ", ")
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
  public static ContactInvite justId(String id) {
    return new ContactInvite(
      id,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      success);
  }
  public interface BuildStep {
    ContactInvite build();
    BuildStep id(String id);
    BuildStep success(Boolean success);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private Boolean success;
    public Builder() {
      
    }
    
    private Builder(String id, Boolean success) {
      this.id = id;
      this.success = success;
    }
    
    @Override
     public ContactInvite build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new ContactInvite(
          id,
          success);
    }
    
    @Override
     public BuildStep success(Boolean success) {
        this.success = success;
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
    private CopyOfBuilder(String id, Boolean success) {
      super(id, success);
      
    }
    
    @Override
     public CopyOfBuilder success(Boolean success) {
      return (CopyOfBuilder) super.success(success);
    }
  }
  

  public static class ContactInviteIdentifier extends ModelIdentifier<ContactInvite> {
    private static final long serialVersionUID = 1L;
    public ContactInviteIdentifier(String id) {
      super(id);
    }
  }
  
}
