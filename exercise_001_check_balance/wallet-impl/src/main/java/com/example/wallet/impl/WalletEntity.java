/*
 * 
 */
package com.example.wallet.impl;

import com.google.common.collect.ImmutableMap;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;


public class WalletEntity extends PersistentEntity<WalletCommand, WalletEvent, WalletState> {

  /**
   * An entity can define different behaviours for different states, but it will
   * always start with an initial behaviour. This entity only has one behaviour.
   */
  @Override
  public Behavior initialBehavior(Optional<WalletState> snapshotState) {


    /*
     * Behaviour is defined using a behaviour builder. The behaviour builder
     * starts with a state, if this entity supports snapshotting (an
     * optimisation that allows the state itself to be persisted to combine many
     * events into one), then the passed in snapshotState may have a value that
     * can be used.
     *
     * Otherwise, the default state is to use the CheckBalance greeting.
     */
    //default balances
    ImmutableMap<String, BigDecimal> defaultBalanaces = ImmutableMap.<String, BigDecimal>builder().build();
    BehaviorBuilder b = newBehaviorBuilder(
        snapshotState.orElse(new WalletState(defaultBalanaces, LocalDateTime.now().toString())));

    /*
     * Command handler for the CheckBalance command.
     */
    b.setReadOnlyCommandHandler(WalletCommand.CheckBalance.class,
        // Get the greeting from the current state, and prepend it to the name
        // that we're sending
        // a greeting to, and reply with that message.
        (cmd, ctx) -> ctx.reply(entityId()+"'s wallet state is: "+ state().toString()));

    /*
     * We've defined all our behaviour, so build and return it.
     */
    return b.build();
  }


}
