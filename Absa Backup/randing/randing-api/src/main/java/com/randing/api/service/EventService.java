package com.randing.api.service;

import com.randing.api.entity.Bill;
import com.randing.api.entity.Debt;
import com.randing.api.entity.Event;
import com.randing.api.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class EventService {

    private final UserService userService;
    private final DebtService debtService;
    private final BillService billService;
    private final EventRepository eventRepository;
    private final DebtDistributionService debtDistributionService;

    public Event saveEvent(Event event) {
        event.setModifiedAt(now());
        event = eventRepository.save(event);
        return event;
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public void deleteEventById(Long id) {
        Optional<Event> eventOptional = getEventById(id);
        eventOptional.ifPresent(eventRepository::delete);
    }

    public List<Event> getAllEventsByUserId(Long userId) {
        return userService.getUserById(userId)
                .map(eventRepository::findAllByPeopleContaining).orElse(Collections.emptyList());
    }

    public List<Debt> calculateDistributedDebts(Long eventId) {
        Event event = getEventById(eventId).orElseGet(Event::new);
        if (!eventId.equals(event.getId())) return null;
        List<Debt> debts = debtDistributionService.calculateDebtDistribution(event);
        debts.forEach(debt -> debt.setTitle(String.format("Debt from event \"%s\"", event.getTitle())));
        return debts;
    }

    public Event closeEventAndSaveNewDebts(Long id, String email) {
        Event event = getEventById(id).orElseGet(Event::new);
        if (event.getOwner() != null && event.getOwner().getEmail().equals(email)) {
            List<Debt> distributedDebts = calculateDistributedDebts(id);
            event.setClosedAt(now());
            if (distributedDebts != null) {
                debtService.saveDebts(distributedDebts);
            }
            event = saveEvent(event);
        }
        return event;
    }

    public Event addOrUpdateBill(Long eventId, Bill bill) {
        return removeBillFromEvent(eventId, bill.getId()).map(event -> {
            bill.setModifiedAt(now());
            event.setModifiedAt(now());
            event.getBills().add(bill);
            return saveEvent(event);
        }).orElse(null);
    }

    public void deleteBillFromEvent(Long eventId, Long billId) {
        removeBillFromEvent(eventId, billId);
        billService.deleteBillById(billId);
    }

    private Optional<Event> removeBillFromEvent(Long eventId, Long billId) {
        return getEventById(eventId).map(event -> {
            Optional<Bill> billOptional = event.getBills().stream().filter(b -> b.getId().equals(billId)).findFirst();
            billOptional.ifPresent(bill -> {
                Bill b = billOptional.get();
                event.getBills().remove(b);
                event.setModifiedAt(now());
            });
            return Optional.of(event);
        }).orElse(Optional.empty());
    }
}
