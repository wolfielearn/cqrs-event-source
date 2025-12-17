package com.wolfie.cqrs.command.aggregate;


import com.wolfie.cqrs.commonapi.command.CreateAccountCommand;
import com.wolfie.cqrs.commonapi.command.CreditAccountCommand;
import com.wolfie.cqrs.commonapi.command.DebitAccountCommand;
import com.wolfie.cqrs.commonapi.enums.AccountStatus;
import com.wolfie.cqrs.commonapi.events.AccountActivatedEvent;
import com.wolfie.cqrs.commonapi.events.AccountCreatedEvent;
import com.wolfie.cqrs.commonapi.events.AccountCreditedEvent;
import com.wolfie.cqrs.commonapi.events.AccountDebitedEvent;
import com.wolfie.cqrs.commonapi.exception.AmountNegatifException;
import com.wolfie.cqrs.commonapi.exception.BalanceNotSufficientException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private double balance;
    private String currency;
    private AccountStatus status;

    public AccountAggregate() {
        //required by AXON
    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand createAccountCommand) {

        if (createAccountCommand.getInitialBalance() < 0) throw new RuntimeException("error...");

        //OK -> then publish event
        AggregateLifecycle.apply(new AccountCreatedEvent(
                createAccountCommand.getId(),
                createAccountCommand.getInitialBalance(),
                createAccountCommand.getCurrency(),
                AccountStatus.CREATED
        ));
    }
    @EventSourcingHandler
    public void on(AccountCreatedEvent event){

        this.accountId = event.getId();
        this.balance = event.getInitialBalance();
        this.currency = event.getCurrency();
        this.status = AccountStatus.CREATED;

        AggregateLifecycle.apply(new AccountActivatedEvent(
                this.accountId = event.getId(),
                this.status = AccountStatus.ACTIVATED
        ));
    }
    @EventSourcingHandler
    public void on(AccountActivatedEvent event){
        this.accountId = event.getId();
        this.status = event.getStatus();
    }

    @CommandHandler
    public void handle(CreditAccountCommand command){
        if(command.getAmount() < 0) throw new AmountNegatifException("Amount should be positif value..");
        // If OK
        AggregateLifecycle.apply(new AccountCreditedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        ));
    }
    @EventSourcingHandler
    public void on(AccountCreditedEvent event){
        this.balance = event.getAmount();
    }

    @CommandHandler
    public void handle(DebitAccountCommand command){
        if(command.getAmount() < 0) throw new AmountNegatifException("Amount should be positif value..");
        if(this.balance < command.getAmount()) throw new BalanceNotSufficientException("Balance is not sufficient for this operation...[Current Balance="+balance+"]");
        // If OK
        AggregateLifecycle.apply(new AccountDebitedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        ));
    }
    @EventSourcingHandler
    public void on(AccountDebitedEvent event){
        this.balance -= event.getAmount();
    }
}