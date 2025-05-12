package com.amplifyframework.datastore.generated.model;


import androidx.core.util.ObjectsCompat;

import java.util.Objects;
import java.util.List;

/** This is an auto generated class representing the Message type in your schema. */
public final class Message {
  private final String content;
  private final String channelName;
  public String getContent() {
      return content;
  }
  
  public String getChannelName() {
      return channelName;
  }
  
  private Message(String content, String channelName) {
    this.content = content;
    this.channelName = channelName;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Message message = (Message) obj;
      return ObjectsCompat.equals(getContent(), message.getContent()) &&
              ObjectsCompat.equals(getChannelName(), message.getChannelName());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getContent())
      .append(getChannelName())
      .toString()
      .hashCode();
  }
  
  public static ContentStep builder() {
      return new Builder();
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(content,
      channelName);
  }
  public interface ContentStep {
    ChannelNameStep content(String content);
  }
  

  public interface ChannelNameStep {
    BuildStep channelName(String channelName);
  }
  

  public interface BuildStep {
    Message build();
  }
  

  public static class Builder implements ContentStep, ChannelNameStep, BuildStep {
    private String content;
    private String channelName;
    public Builder() {
      
    }
    
    private Builder(String content, String channelName) {
      this.content = content;
      this.channelName = channelName;
    }
    
    @Override
     public Message build() {
        
        return new Message(
          content,
          channelName);
    }
    
    @Override
     public ChannelNameStep content(String content) {
        Objects.requireNonNull(content);
        this.content = content;
        return this;
    }
    
    @Override
     public BuildStep channelName(String channelName) {
        Objects.requireNonNull(channelName);
        this.channelName = channelName;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String content, String channelName) {
      super(content, channelName);
      Objects.requireNonNull(content);
      Objects.requireNonNull(channelName);
    }
    
    @Override
     public CopyOfBuilder content(String content) {
      return (CopyOfBuilder) super.content(content);
    }
    
    @Override
     public CopyOfBuilder channelName(String channelName) {
      return (CopyOfBuilder) super.channelName(channelName);
    }
  }
  
}
