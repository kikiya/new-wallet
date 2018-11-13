package com.example.magicmoneymint.impl;

import akka.Done;
import akka.stream.javadsl.Flow;
import com.example.wallet.api.TransactionMessage;
import com.example.wallet.api.WalletEventPublish;
import com.example.wallet.api.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class WalletStreamSubscriber {
    private final Logger log = LoggerFactory.getLogger(WalletStreamSubscriber.class);


    public final WalletService walletService;

    @Inject
    public WalletStreamSubscriber(WalletService walletService) {

        this.walletService = walletService;

        walletService.walletEvents().subscribe()
                .atLeastOnce(
                        Flow.<WalletEventPublish>create().mapAsync(1, event -> {
                            if (event instanceof WalletEventPublish.CurrencySent) {
                                WalletEventPublish.CurrencySent currencySent = (WalletEventPublish.CurrencySent)event;
                                //TODO do something nice and give back to the giver
                                log.info("be kind to someone. give back.");
                            }
                            return CompletableFuture.completedFuture(Done.getInstance());
                        })
                );
    }
}
