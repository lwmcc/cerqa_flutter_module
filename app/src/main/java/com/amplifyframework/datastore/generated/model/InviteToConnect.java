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

/** This is an auto generated class representing the InviteToConnect type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "InviteToConnects", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, provider = "apiKey", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
}, hasLazySupport = true)
public final class InviteToConnect implements Model {
  public static final InviteToConnectPath rootPath = new InviteToConnectPath("root", false, null);
  public static final QueryField ID = field("InviteToConnect", "id");
  public static final QueryField RECEIVER_USER_ID = field("InviteToConnect", "receiverUserId");
  public static final QueryField INVITES = field("InviteToConnect", "senderUserId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="ID", isRequired = true) String receiverUserId;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "senderUserId", targetNames = {"senderUserId"}, type = User.class) ModelReference<User> invites;
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
  
  public String getReceiverUserId() {
      return receiverUserId;
  }
  
  public ModelReference<User> getInvites() {
      return invites;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private InviteToConnect(String id, String receiverUserId, ModelReference<User> invites) {
    this.id = id;
    this.receiverUserId = receiverUserId;
    this.invites = invites;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      InviteToConnect inviteToConnect = (InviteToConnect) obj;
      return ObjectsCompat.equals(getId(), inviteToConnect.getId()) &&
              ObjectsCompat.equals(getReceiverUserId(), inviteToConnect.getReceiverUserId()) &&
              ObjectsCompat.equals(getInvites(), inviteToConnect.getInvites()) &&
              ObjectsCompat.equals(getCreatedAt(), inviteToConnect.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), inviteToConnect.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getReceiverUserId())
      .append(getInvites())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("InviteToConnect {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("receiverUserId=" + String.valueOf(getReceiverUserId()) + ", ")
      .append("invites=" + String.valueOf(getInvites()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static ReceiverUserIdStep builder() {
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
  public static InviteToConnect justId(String id) {
    return new InviteToConnect(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      receiverUserId,
      invites);
  }
  public interface ReceiverUserIdStep {
    BuildStep receiverUserId(String receiverUserId);
  }
  

  public interface BuildStep {
    InviteToConnect build();
    BuildStep id(String id);
    BuildStep invites(User invites);
  }
  

  public static class Builder implements ReceiverUserIdStep, BuildStep {
    private String id;
    private String receiverUserId;
    private ModelReference<User> invites;
    public Builder() {
      
    }
    
    private Builder(String id, String receiverUserId, ModelReference<User> invites) {
      this.id = id;
      this.receiverUserId = receiverUserId;
      this.invites = invites;
    }
    
    @Override
     public InviteToConnect build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new InviteToConnect(
          id,
          receiverUserId,
          invites);
    }
    
    @Override
     public BuildStep receiverUserId(String receiverUserId) {
        Objects.requireNonNull(receiverUserId);
        this.receiverUserId = receiverUserId;
        return this;
    }
    
    @Override
     public BuildStep invites(User invites) {
        this.invites = new LoadedModelReferenceImpl<>(invites);
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
    private CopyOfBuilder(String id, String receiverUserId, ModelReference<User> invites) {
      super(id, receiverUserId, invites);
      Objects.requireNonNull(receiverUserId);
    }
    
    @Override
     public CopyOfBuilder receiverUserId(String receiverUserId) {
      return (CopyOfBuilder) super.receiverUserId(receiverUserId);
    }
    
    @Override
     public CopyOfBuilder invites(User invites) {
      return (CopyOfBuilder) super.invites(invites);
    }
  }
  

  public static class InviteToConnectIdentifier extends ModelIdentifier<InviteToConnect> {
    private static final long serialVersionUID = 1L;
    public InviteToConnectIdentifier(String id) {
      super(id);
    }
  }
  
}
