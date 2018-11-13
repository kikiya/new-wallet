/*
 * 
 */
package com.example.wallet.impl;

import akka.Done;
import akka.NotUsed;
import com.example.wallet.api.TransactionMessage;
import com.example.wallet.api.Wallet;
import com.example.wallet.api.WalletService;
import com.example.wallet.impl.WalletCommand.Deposit;
import com.example.wallet.impl.WalletCommand.CheckBalance;
import com.example.wallet.impl.WalletCommand.Send;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;

import javax.inject.Inject;
import java.util.List;

/**
 * Implementation of the WalletService.
 */
public class WalletServiceImpl implements WalletService {

  private final PersistentEntityRegistry persistentEntityRegistry;

  private final WalletRepository walletRepository;

  @Inject
  public WalletServiceImpl(PersistentEntityRegistry persistentEntityRegistry, ReadSide readSide, WalletRepository walletRepository) {
    this.persistentEntityRegistry = persistentEntityRegistry;
    persistentEntityRegistry.register(WalletEntity.class);

    readSide.register(WalletEventProcessor.class);

    this.walletRepository = walletRepository;

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

  @Override
  public ServiceCall<TransactionMessage, Done> send(String id) {
    return request -> {
      PersistentEntityRef<WalletCommand> ref = persistentEntityRegistry.refFor(WalletEntity.class, id);

      return ref.ask(new Send(request.cryptocurrency, request.amount,
              request.walletId.orElseThrow(() -> new IllegalStateException("where u sending this?"))));
    };
  }

  @Override
  public ServiceCall<NotUsed, List<Wallet>> showAllDocs() {
    return request ->
            walletRepository.showAllDocs();
  }
}
