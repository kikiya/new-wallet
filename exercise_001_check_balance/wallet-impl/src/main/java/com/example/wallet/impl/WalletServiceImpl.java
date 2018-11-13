/*
 * 
 */
package com.example.wallet.impl;

import akka.NotUsed;
import com.example.wallet.api.WalletService;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Implementation of the WalletService.
 */
public class WalletServiceImpl implements WalletService {

  private final PersistentEntityRegistry persistentEntityRegistry;

  @Inject
  public WalletServiceImpl(PersistentEntityRegistry persistentEntityRegistry) {
    this.persistentEntityRegistry = persistentEntityRegistry;
    persistentEntityRegistry.register(WalletEntity.class);

  }

  @Override
  public ServiceCall<NotUsed, String> balance(String id) {
    return request -> {
      // Look up the balance world entity for the given ID.
      PersistentEntityRef<WalletCommand> ref = persistentEntityRegistry.refFor(WalletEntity.class, id);

      // TODO Ask the entity the CheckBalance command.
      return completedFuture(String.format("hey [%s] what's in your wallet? HINT: ask the entity" , id));
    };
  }

}
