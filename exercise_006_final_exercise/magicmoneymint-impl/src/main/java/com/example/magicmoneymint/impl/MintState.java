/*
 * 
 */
package com.example.magicmoneymint.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Value;

/**
 * The state for the {@link MintEntity} entity.
 */
@SuppressWarnings("serial")
@Value
@JsonDeserialize
public final class MintState implements CompressedJsonable {

  public final String message;
  public final String timestamp;

  @JsonCreator
  public MintState(String message, String timestamp) {
    this.message = Preconditions.checkNotNull(message, "message");
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
  }
}
