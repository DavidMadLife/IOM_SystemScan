package org.chemtrovina.iom_systemscan.service;

import org.chemtrovina.iom_systemscan.model.History;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HistoryService {
    void addHistory(History history);

    void updateHistory(History history);

    List<History> getAllHistory();

    Optional<History> getHistoryById(Long id);

    List<History> searchHistory(LocalDate date, String sapPN, String status);
    void createHistoryForScannedMakePN(String makerPN, String employeeId);
}
