package com.randing.api.repository;

import com.randing.api.entity.Debt;
import com.randing.api.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(exported = false)
public interface DebtRepository extends JpaRepository<Debt, Long> {

    @Query("SELECT d FROM Debt d WHERE d.payer = ?1 OR d.receiver = ?1")
    List<Debt> findAllByPersonParticipating(Person person);

}
