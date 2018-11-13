/*
 * 
 */
package com.example.wallet.impl;

import com.example.wallet.api.WalletService;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;

/**
 * The module that binds the WalletService so that it can be served.
 */
public class WalletModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    bindService(WalletService.class, WalletServiceImpl.class);

    bind(RepositoryTemplate.class).to(WalletRepository.class);
  }

  @Provides
  @Singleton
  public MongoCollection mongoCollection(){
    return MongoClients.create("mongodb://localhost")
            .getDatabase("example")
            .getCollection("wallets");
  }
}
