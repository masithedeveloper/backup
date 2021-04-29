package com.randing.api.service;

import com.randing.api.entity.Debt;
import com.randing.api.model.DebtStatus;
import com.randing.api.repository.DebtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DebtService {

    private final DebtRepository debtRepository;
    private final UserService userService;

    public Debt saveDebt(Debt debt) {
        if (debt.getClosedAt() == null) {
            if (debt.getStatus() == DebtStatus.CONFIRMED || debt.getStatus() == DebtStatus.DECLINED) {
                debt.setClosedAt(LocalDateTime.now());
            } else {
                debt.setClosedAt(null);
            }
        }
        debt.setModifiedAt(LocalDateTime.now());
        return debtRepository.save(debt);
    }

    void saveDebts(List<Debt> debts) {
        debts.forEach(this::saveDebt);
    }

    public Optional<Debt> getDebtById(Long id) {
        return debtRepository.findById(id);
    }

    public List<Debt> getAllDebtsByUserId(Long userId) {
        return userService.getUserById(userId)
                .map(debtRepository::findAllByPersonParticipating).orElse(Collections.emptyList());
    }

    public void deleteDebt(Long id) {
        Optional<Debt> debtOptional = getDebtById(id);
        debtOptional.ifPresent(debtRepository::delete);
    }

    public BigDecimal getTotalDebtBalanceForUser(Long userId) {
        return getAllDebtsByUserId(userId).stream()
                .filter(debt -> debt.getStatus() != DebtStatus.CONFIRMED && debt.getStatus() != DebtStatus.DECLINED)
                .map(debt -> userId.equals(debt.getPayer().getId()) ? debt.getSum().negate() : debt.getSum())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Debt acceptDeclineDebt(Long debtId, boolean isAccept) {
        return getDebtById(debtId).map(debt -> {
            debt.setStatus(isAccept ? DebtStatus.ACCEPTED : DebtStatus.DECLINED);
            return saveDebt(debt);
        }).orElse(null);
    }

    public Debt payDebt(Long debtId) {
        return getDebtById(debtId).map(debt -> {
            debt.setStatus(DebtStatus.PAID);
            return saveDebt(debt);
        }).orElse(null);
    }

    public Debt confirmDebt(Long debtId) {
        return getDebtById(debtId).map(debt -> {
            debt.setStatus(DebtStatus.CONFIRMED);
            return saveDebt(debt);
        }).orElse(null);
    }
}
