package com.droppa.clone.droppa.enums;

public enum Role {

  USER("USER"),
  DRIVER("DRIVER"),
  ADMIN("ADMIN");
  
  private String description;

  Role(String description) {
      this.description = description;
  }

  public String description() { return description; }
}
