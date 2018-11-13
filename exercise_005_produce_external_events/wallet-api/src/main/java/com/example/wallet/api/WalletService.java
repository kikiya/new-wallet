/*
 * 
 */
package com.example.wallet.api;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.broker.kafka.KafkaProperties;

import java.util.List;

import static com.lightbend.lagom.javadsl.api.Service.*;

/**
 * The wallet service interface.
 * <p>
 * This describes everything that Lagom needs to know about how to serve and
 * consume the WalletService.
 */
public interface WalletService extends Service {

  /**
   * Example: curl http://localhost:9000/api/balance/Alice
   */
  ServiceCall<NotUsed, String> balance(String walletId);

  ServiceCall<TransactionMessage, Done> depositCrypto(String walletId);

  ServiceCall<TransactionMessage, Done> send(String walletId);

  ServiceCall<NotUsed, List<Wallet>> showAllDocs();

  /**
   * This gets published to Kafka.
   */
  Topic<WalletEventPublish> walletEvents();

  @Override
  default Descriptor descriptor() {
    // @formatter:off
    return named("wallet").withCalls(
            pathCall("/api/balance/:walletId", this::balance),
            pathCall("/api/deposit/:receivingWalletId", this::depositCrypto),
            pathCall("/api/send/:receivingWalletId", this::send),
            pathCall("/api/show", this::showAllDocs)
      ).withTopics(
        topic("balance-events", this::walletEvents)
          // Kafka partitions messages, messages within the same partition will
          // be delivered in order, to ensure that all messages for the same user
          // go to the same partition (and hence are delivered in order with respect
          // to that user), we configure a partition key strategy that extracts the
          // WalletId as the partition key.
          .withProperty(KafkaProperties.partitionKeyStrategy(), WalletEventPublish::getReceivingWalletId)
      ).withAutoAcl(true);
    // @formatter:on
  }
}
