/*
 * 
 */
package com.example.wallet.impl;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import com.example.wallet.api.TransactionMessage;
import com.example.wallet.api.Wallet;
import com.example.wallet.api.WalletEventPublish;
import com.example.wallet.api.WalletService;
import com.example.wallet.impl.WalletCommand.Deposit;
import com.example.wallet.impl.WalletCommand.Hello;
import com.example.wallet.impl.WalletCommand.Send;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
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
      // Ask the entity the Hello command.
      return ref.ask(new Hello(id));
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


  @Override
  public Topic<WalletEventPublish> walletEvents() {
    // We want to publish all the shards of the balance event
    return TopicProducer.taggedStreamWithOffset(WalletEvent.TAG.allTags(), (tag, offset) ->

      // Load the event stream for the passed in shard tag
      persistentEntityRegistry.eventStream(tag, offset).map(eventAndOffset -> {

        // Now we want to convert from the persisted event to the published event.
        // Although these two events are currently identical, in future they may
        // change and need to evolve separately, by separating them now we save
        // a lot of potential trouble in future.
        WalletEventPublish eventToPublish;

        WalletEvent walletEvent = eventAndOffset.first();

        if (eventAndOffset.first() instanceof WalletEvent.CurrencyDeposited) {
          WalletEvent.CurrencyDeposited deposited = (WalletEvent.CurrencyDeposited) eventAndOffset.first();
          eventToPublish = new WalletEventPublish.CurrencyDeposited(
            deposited.id, deposited.currency, deposited.amount
          );
        } else if (walletEvent instanceof WalletEvent.CurrencySent) {
          WalletEvent.CurrencySent currencySent = (WalletEvent.CurrencySent)walletEvent;
          eventToPublish = new WalletEventPublish.CurrencySent(
             currencySent.receivingWalletId, currencySent.currency, currencySent.amount, currencySent.sendingWalletId
          );
        } else if (eventAndOffset.first() instanceof WalletEvent.NewWalletCreated) {
          WalletEvent.NewWalletCreated deposited = (WalletEvent.NewWalletCreated) eventAndOffset.first();
          eventToPublish = new WalletEventPublish.CurrencyDeposited(
                  deposited.id, deposited.currency, deposited.amount
          );
        } else {
          throw new IllegalArgumentException("Unhandled event " + eventAndOffset.first());
        }

        // We return a pair of the translated event, and its offset, so that
        // Lagom can track which offsets have been published.
        return Pair.create(eventToPublish, eventAndOffset.second());
      })
    );
  }
}
