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

/** This is an auto generated class representing the UserChannel type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "UserChannels", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "apiKey", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
@Index(name = "byUserChannel", fields = {"userId","channelId"})
public final class UserChannel implements Model {
  public static final UserChannelPath rootPath = new UserChannelPath("root", false, null);
  public static final QueryField ID = field("UserChannel", "id");
  public static final QueryField ROLE = field("UserChannel", "role");
  public static final QueryField USER = field("UserChannel", "userId");
  public static final QueryField IS_MUTED = field("UserChannel", "isMuted");
  public static final QueryField CHANNEL = field("UserChannel", "channelId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String role;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "userId", targetNames = {"userId"}, type = User.class) ModelReference<User> user;
  private final @ModelField(targetType="Boolean") Boolean isMuted;
  private final @ModelField(targetType="Channel") @BelongsTo(targetName = "channelId", targetNames = {"channelId"}, type = Channel.class) ModelReference<Channel> channel;
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
  
  public String getRole() {
      return role;
  }
  
  public ModelReference<User> getUser() {
      return user;
  }
  
  public Boolean getIsMuted() {
      return isMuted;
  }
  
  public ModelReference<Channel> getChannel() {
      return channel;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private UserChannel(String id, String role, ModelReference<User> user, Boolean isMuted, ModelReference<Channel> channel) {
    this.id = id;
    this.role = role;
    this.user = user;
    this.isMuted = isMuted;
    this.channel = channel;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      UserChannel userChannel = (UserChannel) obj;
      return ObjectsCompat.equals(getId(), userChannel.getId()) &&
              ObjectsCompat.equals(getRole(), userChannel.getRole()) &&
              ObjectsCompat.equals(getUser(), userChannel.getUser()) &&
              ObjectsCompat.equals(getIsMuted(), userChannel.getIsMuted()) &&
              ObjectsCompat.equals(getChannel(), userChannel.getChannel()) &&
              ObjectsCompat.equals(getCreatedAt(), userChannel.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), userChannel.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getRole())
      .append(getUser())
      .append(getIsMuted())
      .append(getChannel())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("UserChannel {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("role=" + String.valueOf(getRole()) + ", ")
      .append("user=" + String.valueOf(getUser()) + ", ")
      .append("isMuted=" + String.valueOf(getIsMuted()) + ", ")
      .append("channel=" + String.valueOf(getChannel()) + ", ")
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
  public static UserChannel justId(String id) {
    return new UserChannel(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      role,
      user,
      isMuted,
      channel);
  }
  public interface BuildStep {
    UserChannel build();
    BuildStep id(String id);
    BuildStep role(String role);
    BuildStep user(User user);
    BuildStep isMuted(Boolean isMuted);
    BuildStep channel(Channel channel);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String role;
    private ModelReference<User> user;
    private Boolean isMuted;
    private ModelReference<Channel> channel;
    public Builder() {
      
    }
    
    private Builder(String id, String role, ModelReference<User> user, Boolean isMuted, ModelReference<Channel> channel) {
      this.id = id;
      this.role = role;
      this.user = user;
      this.isMuted = isMuted;
      this.channel = channel;
    }
    
    @Override
     public UserChannel build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new UserChannel(
          id,
          role,
          user,
          isMuted,
          channel);
    }
    
    @Override
     public BuildStep role(String role) {
        this.role = role;
        return this;
    }
    
    @Override
     public BuildStep user(User user) {
        this.user = new LoadedModelReferenceImpl<>(user);
        return this;
    }
    
    @Override
     public BuildStep isMuted(Boolean isMuted) {
        this.isMuted = isMuted;
        return this;
    }
    
    @Override
     public BuildStep channel(Channel channel) {
        this.channel = new LoadedModelReferenceImpl<>(channel);
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
    private CopyOfBuilder(String id, String role, ModelReference<User> user, Boolean isMuted, ModelReference<Channel> channel) {
      super(id, role, user, isMuted, channel);
      
    }
    
    @Override
     public CopyOfBuilder role(String role) {
      return (CopyOfBuilder) super.role(role);
    }
    
    @Override
     public CopyOfBuilder user(User user) {
      return (CopyOfBuilder) super.user(user);
    }
    
    @Override
     public CopyOfBuilder isMuted(Boolean isMuted) {
      return (CopyOfBuilder) super.isMuted(isMuted);
    }
    
    @Override
     public CopyOfBuilder channel(Channel channel) {
      return (CopyOfBuilder) super.channel(channel);
    }
  }
  

  public static class UserChannelIdentifier extends ModelIdentifier<UserChannel> {
    private static final long serialVersionUID = 1L;
    public UserChannelIdentifier(String id) {
      super(id);
    }
  }
  
}
