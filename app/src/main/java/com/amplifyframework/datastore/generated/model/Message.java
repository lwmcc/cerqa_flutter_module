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

/** This is an auto generated class representing the Message type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Messages", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "apiKey", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
public final class Message implements Model {
  public static final MessagePath rootPath = new MessagePath("root", false, null);
  public static final QueryField ID = field("Message", "id");
  public static final QueryField SENDER_ID = field("Message", "senderId");
  public static final QueryField CONTENT = field("Message", "content");
  public static final QueryField CHANNEL = field("Message", "channelId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="ID", isRequired = true) String senderId;
  private final @ModelField(targetType="String", isRequired = true) String content;
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
  
  public String getSenderId() {
      return senderId;
  }
  
  public String getContent() {
      return content;
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
  
  private Message(String id, String senderId, String content, ModelReference<Channel> channel) {
    this.id = id;
    this.senderId = senderId;
    this.content = content;
    this.channel = channel;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Message message = (Message) obj;
      return ObjectsCompat.equals(getId(), message.getId()) &&
              ObjectsCompat.equals(getSenderId(), message.getSenderId()) &&
              ObjectsCompat.equals(getContent(), message.getContent()) &&
              ObjectsCompat.equals(getChannel(), message.getChannel()) &&
              ObjectsCompat.equals(getCreatedAt(), message.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), message.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getSenderId())
      .append(getContent())
      .append(getChannel())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Message {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("senderId=" + String.valueOf(getSenderId()) + ", ")
      .append("content=" + String.valueOf(getContent()) + ", ")
      .append("channel=" + String.valueOf(getChannel()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static SenderIdStep builder() {
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
  public static Message justId(String id) {
    return new Message(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      senderId,
      content,
      channel);
  }
  public interface SenderIdStep {
    ContentStep senderId(String senderId);
  }
  

  public interface ContentStep {
    BuildStep content(String content);
  }
  

  public interface BuildStep {
    Message build();
    BuildStep id(String id);
    BuildStep channel(Channel channel);
  }
  

  public static class Builder implements SenderIdStep, ContentStep, BuildStep {
    private String id;
    private String senderId;
    private String content;
    private ModelReference<Channel> channel;
    public Builder() {
      
    }
    
    private Builder(String id, String senderId, String content, ModelReference<Channel> channel) {
      this.id = id;
      this.senderId = senderId;
      this.content = content;
      this.channel = channel;
    }
    
    @Override
     public Message build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Message(
          id,
          senderId,
          content,
          channel);
    }
    
    @Override
     public ContentStep senderId(String senderId) {
        Objects.requireNonNull(senderId);
        this.senderId = senderId;
        return this;
    }
    
    @Override
     public BuildStep content(String content) {
        Objects.requireNonNull(content);
        this.content = content;
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
    private CopyOfBuilder(String id, String senderId, String content, ModelReference<Channel> channel) {
      super(id, senderId, content, channel);
      Objects.requireNonNull(senderId);
      Objects.requireNonNull(content);
    }
    
    @Override
     public CopyOfBuilder senderId(String senderId) {
      return (CopyOfBuilder) super.senderId(senderId);
    }
    
    @Override
     public CopyOfBuilder content(String content) {
      return (CopyOfBuilder) super.content(content);
    }
    
    @Override
     public CopyOfBuilder channel(Channel channel) {
      return (CopyOfBuilder) super.channel(channel);
    }
  }
  

  public static class MessageIdentifier extends ModelIdentifier<Message> {
    private static final long serialVersionUID = 1L;
    public MessageIdentifier(String id) {
      super(id);
    }
  }
  
}
