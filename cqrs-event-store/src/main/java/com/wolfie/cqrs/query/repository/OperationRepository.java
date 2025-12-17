package com.wolfie.cqrs.query.repository;

import com.wolfie.cqrs.query.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Long> {
}
