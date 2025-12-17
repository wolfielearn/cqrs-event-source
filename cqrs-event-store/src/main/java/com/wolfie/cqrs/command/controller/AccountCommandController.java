package com.wolfie.cqrs.command.controller;

import com.wolfie.cqrs.commonapi.command.CreateAccountCommand;
import com.wolfie.cqrs.commonapi.command.CreditAccountCommand;
import com.wolfie.cqrs.commonapi.command.DebitAccountCommand;
import com.wolfie.cqrs.commonapi.dto.CreateAccountRequestDto;
import com.wolfie.cqrs.commonapi.dto.CreditAccountRequestDto;
import com.wolfie.cqrs.commonapi.dto.DebitAccountRequestDto;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;


@AllArgsConstructor
@RestController
@RequestMapping("/commands/account")
public class AccountCommandController {

    private CommandGateway commandGateway;
    private EventStore eventStore;

    @PostMapping("/create")
    public CompletableFuture<String> createAccount(@RequestBody CreateAccountRequestDto request){

        CompletableFuture<String> commandResponse =
                commandGateway.send(new CreateAccountCommand(
                                    UUID.randomUUID().toString(),
                                    request.getInitialBalance(),
                                    request.getCurrency()
                            ));

        return  commandResponse;
    }

    @PutMapping("/credit")
    public CompletableFuture<String> creditAccount(@RequestBody CreditAccountRequestDto request){

        CompletableFuture<String> commandResponse = commandGateway.send(new CreditAccountCommand(
                        request.getAccountId(),
                        request.getAmount(),
                        request.getCurrency()
                ));
        return  commandResponse;
    }

    @PutMapping("/debit")
    public CompletableFuture<String> debitAccount(@RequestBody DebitAccountRequestDto request){

        CompletableFuture<String> commandResponse = commandGateway.send(new DebitAccountCommand(
                request.getAccountId(),
                request.getAmount(),
                request.getCurrency()
        ));
        return  commandResponse;
    }

    @GetMapping("eventstore/{accountId}")
    public Stream eventstore(@PathVariable String accountId){
        return eventStore.readEvents(accountId).asStream();
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception exception){
        return new ResponseEntity<>(
               exception.getMessage(),
               HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
