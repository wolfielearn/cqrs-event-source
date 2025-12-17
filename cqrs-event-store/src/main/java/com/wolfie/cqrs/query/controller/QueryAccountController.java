package com.wolfie.cqrs.query.controller;


import com.wolfie.cqrs.commonapi.queries.GetAccountByIdQuery;
import com.wolfie.cqrs.commonapi.queries.GetAllAccountsQuery;
import com.wolfie.cqrs.query.entities.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/query/account")
public class QueryAccountController {

    private QueryGateway queryGateway;

    @GetMapping("/allAccounts")
    public List<Account> accounts(){
        return queryGateway.query(new GetAllAccountsQuery(), ResponseTypes.multipleInstancesOf(Account.class)).join();
    }

    @GetMapping("/{accountId}")
    public Account getAccount(@PathVariable String accountId){
        return queryGateway.query(new GetAccountByIdQuery(accountId), ResponseTypes.instanceOf(Account.class)).join();
    }
}
