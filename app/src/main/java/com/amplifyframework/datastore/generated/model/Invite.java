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

/** This is an auto generated class representing the Invite type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Invites", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "apiKey", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
public final class Invite implements Model {
  public static final InvitePath rootPath = new InvitePath("root", false, null);
  public static final QueryField ID = field("Invite", "id");
  public static final QueryField SENDER_ID = field("Invite", "senderId");
  public static final QueryField RECEIVER_ID = field("Invite", "receiverId");
  public static final QueryField SENDER = field("Invite", "sender");
  public static final QueryField RECEIVER = field("Invite", "receiver");
  public static final QueryField USER = field("Invite", "inviteId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String senderId;
  private final @ModelField(targetType="String", isRequired = true) String receiverId;
  private final @ModelField(targetType="String", isRequired = true) String sender;
  private final @ModelField(targetType="String", isRequired = true) String receiver;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "inviteId", targetNames = {"inviteId"}, type = User.class) ModelReference<User> user;
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
  
  public String getReceiverId() {
      return receiverId;
  }
  
  public String getSender() {
      return sender;
  }
  
  public String getReceiver() {
      return receiver;
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
  
  private Invite(String id, String senderId, String receiverId, String sender, String receiver, ModelReference<User> user) {
    this.id = id;
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.sender = sender;
    this.receiver = receiver;
    this.user = user;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Invite invite = (Invite) obj;
      return ObjectsCompat.equals(getId(), invite.getId()) &&
              ObjectsCompat.equals(getSenderId(), invite.getSenderId()) &&
              ObjectsCompat.equals(getReceiverId(), invite.getReceiverId()) &&
              ObjectsCompat.equals(getSender(), invite.getSender()) &&
              ObjectsCompat.equals(getReceiver(), invite.getReceiver()) &&
              ObjectsCompat.equals(getUser(), invite.getUser()) &&
              ObjectsCompat.equals(getCreatedAt(), invite.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), invite.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getSenderId())
      .append(getReceiverId())
      .append(getSender())
      .append(getReceiver())
      .append(getUser())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Invite {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("senderId=" + String.valueOf(getSenderId()) + ", ")
      .append("receiverId=" + String.valueOf(getReceiverId()) + ", ")
      .append("sender=" + String.valueOf(getSender()) + ", ")
      .append("receiver=" + String.valueOf(getReceiver()) + ", ")
      .append("user=" + String.valueOf(getUser()) + ", ")
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
  public static Invite justId(String id) {
    return new Invite(
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
      senderId,
      receiverId,
      sender,
      receiver,
      user);
  }
  public interface SenderIdStep {
    ReceiverIdStep senderId(String senderId);
  }
  

  public interface ReceiverIdStep {
    SenderStep receiverId(String receiverId);
  }
  

  public interface SenderStep {
    ReceiverStep sender(String sender);
  }
  

  public interface ReceiverStep {
    BuildStep receiver(String receiver);
  }
  

  public interface BuildStep {
    Invite build();
    BuildStep id(String id);
    BuildStep user(User user);
  }
  

  public static class Builder implements SenderIdStep, ReceiverIdStep, SenderStep, ReceiverStep, BuildStep {
    private String id;
    private String senderId;
    private String receiverId;
    private String sender;
    private String receiver;
    private ModelReference<User> user;
    public Builder() {
      
    }
    
    private Builder(String id, String senderId, String receiverId, String sender, String receiver, ModelReference<User> user) {
      this.id = id;
      this.senderId = senderId;
      this.receiverId = receiverId;
      this.sender = sender;
      this.receiver = receiver;
      this.user = user;
    }
    
    @Override
     public Invite build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Invite(
          id,
          senderId,
          receiverId,
          sender,
          receiver,
          user);
    }
    
    @Override
     public ReceiverIdStep senderId(String senderId) {
        Objects.requireNonNull(senderId);
        this.senderId = senderId;
        return this;
    }
    
    @Override
     public SenderStep receiverId(String receiverId) {
        Objects.requireNonNull(receiverId);
        this.receiverId = receiverId;
        return this;
    }
    
    @Override
     public ReceiverStep sender(String sender) {
        Objects.requireNonNull(sender);
        this.sender = sender;
        return this;
    }
    
    @Override
     public BuildStep receiver(String receiver) {
        Objects.requireNonNull(receiver);
        this.receiver = receiver;
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
    private CopyOfBuilder(String id, String senderId, String receiverId, String sender, String receiver, ModelReference<User> user) {
      super(id, senderId, receiverId, sender, receiver, user);
      Objects.requireNonNull(senderId);
      Objects.requireNonNull(receiverId);
      Objects.requireNonNull(sender);
      Objects.requireNonNull(receiver);
    }
    
    @Override
     public CopyOfBuilder senderId(String senderId) {
      return (CopyOfBuilder) super.senderId(senderId);
    }
    
    @Override
     public CopyOfBuilder receiverId(String receiverId) {
      return (CopyOfBuilder) super.receiverId(receiverId);
    }
    
    @Override
     public CopyOfBuilder sender(String sender) {
      return (CopyOfBuilder) super.sender(sender);
    }
    
    @Override
     public CopyOfBuilder receiver(String receiver) {
      return (CopyOfBuilder) super.receiver(receiver);
    }
    
    @Override
     public CopyOfBuilder user(User user) {
      return (CopyOfBuilder) super.user(user);
    }
  }
  

  public static class InviteIdentifier extends ModelIdentifier<Invite> {
    private static final long serialVersionUID = 1L;
    public InviteIdentifier(String id) {
      super(id);
    }
  }
  
}
