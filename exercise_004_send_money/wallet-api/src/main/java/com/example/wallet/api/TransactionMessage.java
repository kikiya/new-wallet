package com.example.wallet.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Optional;

@Value
@JsonDeserialize
public class TransactionMessage {

    public final String cryptocurrency;
    public final BigDecimal amount;
    public final Optional<String> walletId;

    @JsonCreator
    public TransactionMessage(String cryptocurrency, BigDecimal amount, Optional<String> walletId){
        this.cryptocurrency = Preconditions.checkNotNull(cryptocurrency, "cryptocurrency cannot be empty");
        this.amount =  Preconditions.checkNotNull(amount, "amount cannot be empty");
        this.walletId = walletId;
    }

}
