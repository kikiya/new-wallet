/*
 * 
 */
package com.example.wallet.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;

/**
 * This interface defines all the events that the WalletEntity supports.
 * <p>
 * By convention, the events should be inner classes of the interface, which
 * makes it simple to get a complete picture of what events an entity has.
 */
public interface WalletEvent extends Jsonable, AggregateEvent<WalletEvent> {

  /**
   * Tags are used for getting and publishing streams of events. Each event
   * will have this tag, and in this case, we are partitioning the tags into
   * 4 shards, which means we can have 4 concurrent processors/publishers of
   * events.
   */
   AggregateEventShards<WalletEvent> TAG = AggregateEventTag.sharded(WalletEvent.class, 4);


  @Override
  default AggregateEventTagger<WalletEvent> aggregateTag() {
    return TAG;
  }
}
