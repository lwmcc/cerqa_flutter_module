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

/** This is an auto generated class representing the UserGroup type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "UserGroups", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "apiKey", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
public final class UserGroup implements Model {
  public static final UserGroupPath rootPath = new UserGroupPath("root", false, null);
  public static final QueryField ID = field("UserGroup", "id");
  public static final QueryField USER = field("UserGroup", "userId");
  public static final QueryField GROUP = field("UserGroup", "groupId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "userId", targetNames = {"userId"}, type = User.class) ModelReference<User> user;
  private final @ModelField(targetType="Group") @BelongsTo(targetName = "groupId", targetNames = {"groupId"}, type = Group.class) ModelReference<Group> group;
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
  
  public ModelReference<Group> getGroup() {
      return group;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private UserGroup(String id, ModelReference<User> user, ModelReference<Group> group) {
    this.id = id;
    this.user = user;
    this.group = group;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      UserGroup userGroup = (UserGroup) obj;
      return ObjectsCompat.equals(getId(), userGroup.getId()) &&
              ObjectsCompat.equals(getUser(), userGroup.getUser()) &&
              ObjectsCompat.equals(getGroup(), userGroup.getGroup()) &&
              ObjectsCompat.equals(getCreatedAt(), userGroup.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), userGroup.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getUser())
      .append(getGroup())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("UserGroup {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("user=" + String.valueOf(getUser()) + ", ")
      .append("group=" + String.valueOf(getGroup()) + ", ")
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
  public static UserGroup justId(String id) {
    return new UserGroup(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      user,
      group);
  }
  public interface BuildStep {
    UserGroup build();
    BuildStep id(String id);
    BuildStep user(User user);
    BuildStep group(Group group);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private ModelReference<User> user;
    private ModelReference<Group> group;
    public Builder() {
      
    }
    
    private Builder(String id, ModelReference<User> user, ModelReference<Group> group) {
      this.id = id;
      this.user = user;
      this.group = group;
    }
    
    @Override
     public UserGroup build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new UserGroup(
          id,
          user,
          group);
    }
    
    @Override
     public BuildStep user(User user) {
        this.user = new LoadedModelReferenceImpl<>(user);
        return this;
    }
    
    @Override
     public BuildStep group(Group group) {
        this.group = new LoadedModelReferenceImpl<>(group);
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
    private CopyOfBuilder(String id, ModelReference<User> user, ModelReference<Group> group) {
      super(id, user, group);
      
    }
    
    @Override
     public CopyOfBuilder user(User user) {
      return (CopyOfBuilder) super.user(user);
    }
    
    @Override
     public CopyOfBuilder group(Group group) {
      return (CopyOfBuilder) super.group(group);
    }
  }
  

  public static class UserGroupIdentifier extends ModelIdentifier<UserGroup> {
    private static final long serialVersionUID = 1L;
    public UserGroupIdentifier(String id) {
      super(id);
    }
  }
  
}
