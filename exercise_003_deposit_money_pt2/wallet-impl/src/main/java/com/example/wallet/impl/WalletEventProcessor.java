package com.example.wallet.impl;

import akka.Done;
import akka.japi.Pair;
import akka.stream.javadsl.Flow;
import com.example.wallet.api.Currency;
import com.example.wallet.api.Wallet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.Offset;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import org.pcollections.PSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class WalletEventProcessor extends ReadSideProcessor<WalletEvent> {
    private final Logger log = LoggerFactory.getLogger(WalletEventProcessor.class);


    private final WalletRepository walletRepository;

    @Inject
    public WalletEventProcessor(WalletRepository walletRepository) {

        this.walletRepository = walletRepository;

    }

    private CompletionStage<Done> processCurrencyDeposited(WalletEvent.CurrencyDeposited event) throws JsonProcessingException{
        log.info("********************** EventProcessor Currency Deposited Event: "+event);

        //TODO use the addCurrency function from repository
        return completedFuture(Done.getInstance());
    }

    private CompletionStage<Done> processNewWallet(WalletEvent.NewWalletCreated event) throws JsonProcessingException{
        log.info("********************** EventProcessor NEW WALLET event: "+event);

        //TODO use the create function from repository
        return completedFuture(Done.getInstance());
    }

    @Override
    public ReadSideHandler<WalletEvent> buildHandler() {

        return new ReadSideHandler<WalletEvent>() {

            @Override
            public Flow<Pair<WalletEvent, Offset>, Done, ?> handle() {
                return Flow.<Pair<WalletEvent, Offset>>create()
                    .mapAsync(1, eventAndOffset ->{

                        if (eventAndOffset.first() instanceof WalletEvent.CurrencyDeposited) {
                                    WalletEvent.CurrencyDeposited currencyDeposited = (WalletEvent.CurrencyDeposited) eventAndOffset.first();
                                    return processCurrencyDeposited(currencyDeposited);
                                } else if (eventAndOffset.first() instanceof WalletEvent.NewWalletCreated) {
                                    WalletEvent.NewWalletCreated newWalletCreated = (WalletEvent.NewWalletCreated) eventAndOffset.first();
                                    return processNewWallet(newWalletCreated);
                                } else
                                    return completedFuture(Done.getInstance());
                            }
                    );
            }

        };
    }

    @Override
    public PSequence<AggregateEventTag<WalletEvent>> aggregateTags() {
        return WalletEvent.TAG.allTags();
    }
}
