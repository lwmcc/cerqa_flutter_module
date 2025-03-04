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
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "apiKey", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
public final class Group implements Model {
  public static final GroupPath rootPath = new GroupPath("root", false, null);
  public static final QueryField ID = field("Group", "id");
  public static final QueryField USER_ID = field("Group", "userId");
  public static final QueryField MEMBER_ID = field("Group", "memberId");
  public static final QueryField NAME = field("Group", "name");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String userId;
  private final @ModelField(targetType="String", isRequired = true) String memberId;
  private final @ModelField(targetType="String", isRequired = true) String name;
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
  
  public String getUserId() {
      return userId;
  }
  
  public String getMemberId() {
      return memberId;
  }
  
  public String getName() {
      return name;
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
  
  private Group(String id, String userId, String memberId, String name) {
    this.id = id;
    this.userId = userId;
    this.memberId = memberId;
    this.name = name;
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
              ObjectsCompat.equals(getUserId(), group.getUserId()) &&
              ObjectsCompat.equals(getMemberId(), group.getMemberId()) &&
              ObjectsCompat.equals(getName(), group.getName()) &&
              ObjectsCompat.equals(getCreatedAt(), group.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), group.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getUserId())
      .append(getMemberId())
      .append(getName())
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
      .append("userId=" + String.valueOf(getUserId()) + ", ")
      .append("memberId=" + String.valueOf(getMemberId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
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
      userId,
      memberId,
      name);
  }
  public interface UserIdStep {
    MemberIdStep userId(String userId);
  }
  

  public interface MemberIdStep {
    NameStep memberId(String memberId);
  }
  

  public interface NameStep {
    BuildStep name(String name);
  }
  

  public interface BuildStep {
    Group build();
    BuildStep id(String id);
  }
  

  public static class Builder implements UserIdStep, MemberIdStep, NameStep, BuildStep {
    private String id;
    private String userId;
    private String memberId;
    private String name;
    public Builder() {
      
    }
    
    private Builder(String id, String userId, String memberId, String name) {
      this.id = id;
      this.userId = userId;
      this.memberId = memberId;
      this.name = name;
    }
    
    @Override
     public Group build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Group(
          id,
          userId,
          memberId,
          name);
    }
    
    @Override
     public MemberIdStep userId(String userId) {
        Objects.requireNonNull(userId);
        this.userId = userId;
        return this;
    }
    
    @Override
     public NameStep memberId(String memberId) {
        Objects.requireNonNull(memberId);
        this.memberId = memberId;
        return this;
    }
    
    @Override
     public BuildStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
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
    private CopyOfBuilder(String id, String userId, String memberId, String name) {
      super(id, userId, memberId, name);
      Objects.requireNonNull(userId);
      Objects.requireNonNull(memberId);
      Objects.requireNonNull(name);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
    
    @Override
     public CopyOfBuilder memberId(String memberId) {
      return (CopyOfBuilder) super.memberId(memberId);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
  }
  

  public static class GroupIdentifier extends ModelIdentifier<Group> {
    private static final long serialVersionUID = 1L;
    public GroupIdentifier(String id) {
      super(id);
    }
  }
  
}
