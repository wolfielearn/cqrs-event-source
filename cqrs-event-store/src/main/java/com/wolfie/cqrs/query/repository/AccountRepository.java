package com.wolfie.cqrs.query.repository;

import com.wolfie.cqrs.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
