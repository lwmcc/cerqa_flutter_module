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
@Index(name = "undefined", fields = {"id"})
public final class Invite implements Model {
  public static final InvitePath rootPath = new InvitePath("root", false, null);
  public static final QueryField ID = field("Invite", "id");
  public static final QueryField SENDER = field("Invite", "senderUserId");
  public static final QueryField RECEIVER = field("Invite", "receiverUserId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "senderUserId", targetNames = {"senderUserId"}, type = User.class) ModelReference<User> sender;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "receiverUserId", targetNames = {"receiverUserId"}, type = User.class) ModelReference<User> receiver;
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
  
  public ModelReference<User> getSender() {
      return sender;
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
  
  private Invite(String id, ModelReference<User> sender, ModelReference<User> receiver) {
    this.id = id;
    this.sender = sender;
    this.receiver = receiver;
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
              ObjectsCompat.equals(getSender(), invite.getSender()) &&
              ObjectsCompat.equals(getReceiver(), invite.getReceiver()) &&
              ObjectsCompat.equals(getCreatedAt(), invite.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), invite.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getSender())
      .append(getReceiver())
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
      .append("sender=" + String.valueOf(getSender()) + ", ")
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
  public static Invite justId(String id) {
    return new Invite(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      sender,
      receiver);
  }
  public interface BuildStep {
    Invite build();
    BuildStep id(String id);
    BuildStep sender(User sender);
    BuildStep receiver(User receiver);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private ModelReference<User> sender;
    private ModelReference<User> receiver;
    public Builder() {
      
    }
    
    private Builder(String id, ModelReference<User> sender, ModelReference<User> receiver) {
      this.id = id;
      this.sender = sender;
      this.receiver = receiver;
    }
    
    @Override
     public Invite build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Invite(
          id,
          sender,
          receiver);
    }
    
    @Override
     public BuildStep sender(User sender) {
        this.sender = new LoadedModelReferenceImpl<>(sender);
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
    private CopyOfBuilder(String id, ModelReference<User> sender, ModelReference<User> receiver) {
      super(id, sender, receiver);
      
    }
    
    @Override
     public CopyOfBuilder sender(User sender) {
      return (CopyOfBuilder) super.sender(sender);
    }
    
    @Override
     public CopyOfBuilder receiver(User receiver) {
      return (CopyOfBuilder) super.receiver(receiver);
    }
  }
  

  public static class InviteIdentifier extends ModelIdentifier<Invite> {
    private static final long serialVersionUID = 1L;
    public InviteIdentifier(String id) {
      super(id);
    }
  }
  
}
