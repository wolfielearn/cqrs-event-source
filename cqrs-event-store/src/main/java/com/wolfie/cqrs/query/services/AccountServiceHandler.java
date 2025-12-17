package com.wolfie.cqrs.query.services;

import com.wolfie.cqrs.commonapi.enums.OperationType;
import com.wolfie.cqrs.commonapi.events.AccountActivatedEvent;
import com.wolfie.cqrs.commonapi.events.AccountCreatedEvent;
import com.wolfie.cqrs.commonapi.events.AccountCreditedEvent;
import com.wolfie.cqrs.commonapi.events.AccountDebitedEvent;
import com.wolfie.cqrs.commonapi.queries.GetAccountByIdQuery;
import com.wolfie.cqrs.commonapi.queries.GetAllAccountsQuery;
import com.wolfie.cqrs.query.entities.Account;
import com.wolfie.cqrs.query.entities.Operation;
import com.wolfie.cqrs.query.repository.AccountRepository;
import com.wolfie.cqrs.query.repository.OperationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServiceHandler {

    private AccountRepository accountRepository;
    private OperationRepository operationRepository;

    @EventHandler
    public void on(AccountCreatedEvent event) {
        log.info("*******************************");
        log.info("AccountCreatedEvent received ...");
       Account account =  new Account(
                event.getId(),
                event.getInitialBalance(),
                event.getStatus(),
                event.getCurrency(),null);
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountActivatedEvent event) {
        log.info("*******************************");
        log.info("AccountActivatedEvent received ...");
        Account account = accountRepository.findById(event.getId()).orElseThrow(()->  new RuntimeException("error.."));
        account.setStatus(event.getStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountDebitedEvent event) {
        log.info("*******************************");
        log.info("AccountDebitedEvent received ...");
        Account account = accountRepository.findById(event.getId()).orElseThrow(()->  new RuntimeException("error.."));
        Operation operation = new Operation();
        operation.setAmount(event.getAmount());
        operation.setCreatedAt(new Date());
        operation.setType(OperationType.DEBIT);
        operation.setAccount(account);
        operationRepository.save(operation);

        account.setBalance(account.getBalance() - event.getAmount());
        accountRepository.save(account);
    }


    @EventHandler
    public void on(AccountCreditedEvent event) {
        log.info("*******************************");
        log.info("AccountCreditedEvent received ...");
        Account account = accountRepository.findById(event.getId()).orElseThrow(()->  new RuntimeException("error.."));
        Operation operation = new Operation();
        operation.setAmount(event.getAmount());
        operation.setCreatedAt(new Date());
        operation.setType(OperationType.CREDIT);
        operation.setAccount(account);
        operationRepository.save(operation);

        account.setBalance(account.getBalance() + event.getAmount());
        accountRepository.save(account);
    }

    @QueryHandler
    public List<Account> handle(GetAllAccountsQuery query) {
        return accountRepository.findAll();
    }

    @QueryHandler
    public Account handle(GetAccountByIdQuery query) {
        return accountRepository.findById(query.getId())
                .orElseThrow(()-> new RuntimeException("Account not found with id =[ " + query.id +" ]"));
    }
}
