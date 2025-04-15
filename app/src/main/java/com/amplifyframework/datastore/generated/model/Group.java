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

/** This is an auto generated class representing the Group type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Groups", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "iam", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
public final class Group implements Model {
  public static final GroupPath rootPath = new GroupPath("root", false, null);
  public static final QueryField ID = field("Group", "id");
  public static final QueryField NAME = field("Group", "name");
  public static final QueryField IS_ADMIN = field("Group", "isAdmin");
  public static final QueryField MEMBERS = field("Group", "members");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="String") String isAdmin;
  private final @ModelField(targetType="AWSJSON") String members;
  private final @ModelField(targetType="UserGroup") @HasMany(associatedWith = "group", type = UserGroup.class) ModelList<UserGroup> users = null;
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
  
  public String getIsAdmin() {
      return isAdmin;
  }
  
  public String getMembers() {
      return members;
  }
  
  public ModelList<UserGroup> getUsers() {
      return users;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Group(String id, String name, String isAdmin, String members) {
    this.id = id;
    this.name = name;
    this.isAdmin = isAdmin;
    this.members = members;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Group group = (Group) obj;
      return ObjectsCompat.equals(getId(), group.getId()) &&
              ObjectsCompat.equals(getName(), group.getName()) &&
              ObjectsCompat.equals(getIsAdmin(), group.getIsAdmin()) &&
              ObjectsCompat.equals(getMembers(), group.getMembers()) &&
              ObjectsCompat.equals(getCreatedAt(), group.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), group.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getIsAdmin())
      .append(getMembers())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Group {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("isAdmin=" + String.valueOf(getIsAdmin()) + ", ")
      .append("members=" + String.valueOf(getMembers()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
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
  public static Group justId(String id) {
    return new Group(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      isAdmin,
      members);
  }
  public interface NameStep {
    BuildStep name(String name);
  }
  

  public interface BuildStep {
    Group build();
    BuildStep id(String id);
    BuildStep isAdmin(String isAdmin);
    BuildStep members(String members);
  }
  

  public static class Builder implements NameStep, BuildStep {
    private String id;
    private String name;
    private String isAdmin;
    private String members;
    public Builder() {
      
    }
    
    private Builder(String id, String name, String isAdmin, String members) {
      this.id = id;
      this.name = name;
      this.isAdmin = isAdmin;
      this.members = members;
    }
    
    @Override
     public Group build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Group(
          id,
          name,
          isAdmin,
          members);
    }
    
    @Override
     public BuildStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public BuildStep isAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
        return this;
    }
    
    @Override
     public BuildStep members(String members) {
        this.members = members;
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
    private CopyOfBuilder(String id, String name, String isAdmin, String members) {
      super(id, name, isAdmin, members);
      Objects.requireNonNull(name);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder isAdmin(String isAdmin) {
      return (CopyOfBuilder) super.isAdmin(isAdmin);
    }
    
    @Override
     public CopyOfBuilder members(String members) {
      return (CopyOfBuilder) super.members(members);
    }
  }
  

  public static class GroupIdentifier extends ModelIdentifier<Group> {
    private static final long serialVersionUID = 1L;
    public GroupIdentifier(String id) {
      super(id);
    }
  }
  
}
