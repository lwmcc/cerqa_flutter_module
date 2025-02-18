package com.amplifyframework.datastore.generated.model;


import androidx.core.util.ObjectsCompat;

import java.util.Objects;
import java.util.List;

/** This is an auto generated class representing the UsersConnection type in your schema. */
public final class UsersConnection {
  private final List<Users> items;
  private final String nextToken;
  public List<Users> getItems() {
      return items;
  }
  
  public String getNextToken() {
      return nextToken;
  }
  
  private UsersConnection(List<Users> items, String nextToken) {
    this.items = items;
    this.nextToken = nextToken;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      UsersConnection usersConnection = (UsersConnection) obj;
      return ObjectsCompat.equals(getItems(), usersConnection.getItems()) &&
              ObjectsCompat.equals(getNextToken(), usersConnection.getNextToken());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getItems())
      .append(getNextToken())
      .toString()
      .hashCode();
  }
  
  public static BuildStep builder() {
      return new Builder();
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(items,
      nextToken);
  }
  public interface BuildStep {
    UsersConnection build();
    BuildStep items(List<Users> items);
    BuildStep nextToken(String nextToken);
  }
  

  public static class Builder implements BuildStep {
    private List<Users> items;
    private String nextToken;
    public Builder() {
      
    }
    
    private Builder(List<Users> items, String nextToken) {
      this.items = items;
      this.nextToken = nextToken;
    }
    
    @Override
     public UsersConnection build() {
        
        return new UsersConnection(
          items,
          nextToken);
    }
    
    @Override
     public BuildStep items(List<Users> items) {
        this.items = items;
        return this;
    }
    
    @Override
     public BuildStep nextToken(String nextToken) {
        this.nextToken = nextToken;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(List<Users> items, String nextToken) {
      super(items, nextToken);
      
    }
    
    @Override
     public CopyOfBuilder items(List<Users> items) {
      return (CopyOfBuilder) super.items(items);
    }
    
    @Override
     public CopyOfBuilder nextToken(String nextToken) {
      return (CopyOfBuilder) super.nextToken(nextToken);
    }
  }
  
}
