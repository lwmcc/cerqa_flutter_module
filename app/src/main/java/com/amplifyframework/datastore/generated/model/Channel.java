package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.HasMany;
import com.amplifyframework.core.model.ModelList;
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

/** This is an auto generated class representing the Channel type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Channels", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "apiKey", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
public final class Channel implements Model {
  public static final ChannelPath rootPath = new ChannelPath("root", false, null);
  public static final QueryField ID = field("Channel", "id");
  public static final QueryField NAME = field("Channel", "name");
  public static final QueryField IS_GROUP = field("Channel", "isGroup");
  public static final QueryField IS_PUBLIC = field("Channel", "isPublic");
  public static final QueryField CREATOR = field("Channel", "creatorId");
  public static final QueryField RECEIVER = field("Channel", "receiverId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String name;
  private final @ModelField(targetType="Message") @HasMany(associatedWith = "channel", type = Message.class) ModelList<Message> messages = null;
  private final @ModelField(targetType="Boolean") Boolean isGroup;
  private final @ModelField(targetType="Boolean") Boolean isPublic;
  private final @ModelField(targetType="UserChannel") @HasMany(associatedWith = "channel", type = UserChannel.class) ModelList<UserChannel> channels = null;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "creatorId", targetNames = {"creatorId"}, type = User.class) ModelReference<User> creator;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "receiverId", targetNames = {"receiverId"}, type = User.class) ModelReference<User> receiver;
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
  
  public ModelList<Message> getMessages() {
      return messages;
  }
  
  public Boolean getIsGroup() {
      return isGroup;
  }
  
  public Boolean getIsPublic() {
      return isPublic;
  }
  
  public ModelList<UserChannel> getChannels() {
      return channels;
  }
  
  public ModelReference<User> getCreator() {
      return creator;
  }
  
  public ModelReference<User> getReceiver() {
      return receiver;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Channel(String id, String name, Boolean isGroup, Boolean isPublic, ModelReference<User> creator, ModelReference<User> receiver) {
    this.id = id;
    this.name = name;
    this.isGroup = isGroup;
    this.isPublic = isPublic;
    this.creator = creator;
    this.receiver = receiver;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Channel channel = (Channel) obj;
      return ObjectsCompat.equals(getId(), channel.getId()) &&
              ObjectsCompat.equals(getName(), channel.getName()) &&
              ObjectsCompat.equals(getIsGroup(), channel.getIsGroup()) &&
              ObjectsCompat.equals(getIsPublic(), channel.getIsPublic()) &&
              ObjectsCompat.equals(getCreator(), channel.getCreator()) &&
              ObjectsCompat.equals(getReceiver(), channel.getReceiver()) &&
              ObjectsCompat.equals(getCreatedAt(), channel.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), channel.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getIsGroup())
      .append(getIsPublic())
      .append(getCreator())
      .append(getReceiver())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Channel {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("isGroup=" + String.valueOf(getIsGroup()) + ", ")
      .append("isPublic=" + String.valueOf(getIsPublic()) + ", ")
      .append("creator=" + String.valueOf(getCreator()) + ", ")
      .append("receiver=" + String.valueOf(getReceiver()) + ", ")
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
  public static Channel justId(String id) {
    return new Channel(
      id,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      isGroup,
      isPublic,
      creator,
      receiver);
  }
  public interface BuildStep {
    Channel build();
    BuildStep id(String id);
    BuildStep name(String name);
    BuildStep isGroup(Boolean isGroup);
    BuildStep isPublic(Boolean isPublic);
    BuildStep creator(User creator);
    BuildStep receiver(User receiver);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String name;
    private Boolean isGroup;
    private Boolean isPublic;
    private ModelReference<User> creator;
    private ModelReference<User> receiver;
    public Builder() {
      
    }
    
    private Builder(String id, String name, Boolean isGroup, Boolean isPublic, ModelReference<User> creator, ModelReference<User> receiver) {
      this.id = id;
      this.name = name;
      this.isGroup = isGroup;
      this.isPublic = isPublic;
      this.creator = creator;
      this.receiver = receiver;
    }
    
    @Override
     public Channel build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Channel(
          id,
          name,
          isGroup,
          isPublic,
          creator,
          receiver);
    }
    
    @Override
     public BuildStep name(String name) {
        this.name = name;
        return this;
    }
    
    @Override
     public BuildStep isGroup(Boolean isGroup) {
        this.isGroup = isGroup;
        return this;
    }
    
    @Override
     public BuildStep isPublic(Boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }
    
    @Override
     public BuildStep creator(User creator) {
        this.creator = new LoadedModelReferenceImpl<>(creator);
        return this;
    }
    
    @Override
     public BuildStep receiver(User receiver) {
        this.receiver = new LoadedModelReferenceImpl<>(receiver);
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
    private CopyOfBuilder(String id, String name, Boolean isGroup, Boolean isPublic, ModelReference<User> creator, ModelReference<User> receiver) {
      super(id, name, isGroup, isPublic, creator, receiver);
      
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder isGroup(Boolean isGroup) {
      return (CopyOfBuilder) super.isGroup(isGroup);
    }
    
    @Override
     public CopyOfBuilder isPublic(Boolean isPublic) {
      return (CopyOfBuilder) super.isPublic(isPublic);
    }
    
    @Override
     public CopyOfBuilder creator(User creator) {
      return (CopyOfBuilder) super.creator(creator);
    }
    
    @Override
     public CopyOfBuilder receiver(User receiver) {
      return (CopyOfBuilder) super.receiver(receiver);
    }
  }
  

  public static class ChannelIdentifier extends ModelIdentifier<Channel> {
    private static final long serialVersionUID = 1L;
    public ChannelIdentifier(String id) {
      super(id);
    }
  }
  
}
