/*
 * 
 */
package com.example.wallet.impl;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.math.BigDecimal;

/**
 * This interface defines all the commands that the WalletEntity supports.
 * 
 * By convention, the commands should be inner classes of the interface, which
 * makes it simple to get a complete picture of what commands an entity
 * supports.
 */
public interface WalletCommand extends Jsonable {

  /**
   * A command to say balance to someone using the current greeting message.
   * <p>
   * The reply type is String, and will contain the message to say to that
   * person.
   */
  @SuppressWarnings("serial")
  @Value
  @JsonDeserialize
  final class CheckBalance implements WalletCommand, PersistentEntity.ReplyType<String> {
    public final String name;

    @JsonCreator
    public CheckBalance(String name) {
      this.name = Preconditions.checkNotNull(name, "name");
    }
  }

}
