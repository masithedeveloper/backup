package com.randing.api.repository;

import com.randing.api.entity.BillPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(exported = false)
public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {
}
