/*
 * 
 */
package com.example.wallet.impl;

import akka.Done;
import akka.NotUsed;
import com.example.wallet.api.TransactionMessage;
import com.example.wallet.api.WalletService;
import com.example.wallet.impl.WalletCommand.Deposit;
import com.example.wallet.impl.WalletCommand.CheckBalance;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;

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
      // Ask the entity the CheckBalance command.
      return ref.ask(new CheckBalance(id));
    };
  }

  @Override
  public ServiceCall<TransactionMessage, Done> depositCrypto(String id) {
    return request -> {
      PersistentEntityRef<WalletCommand> ref = persistentEntityRegistry.refFor(WalletEntity.class, id);

      return ref.ask(new Deposit(request.cryptocurrency, request.amount));
    };
  }

}
