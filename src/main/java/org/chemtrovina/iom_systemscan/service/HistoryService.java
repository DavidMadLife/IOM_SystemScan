package org.chemtrovina.iom_systemscan.service;

import org.chemtrovina.iom_systemscan.dto.HistorySummaryViewModel;
import org.chemtrovina.iom_systemscan.model.History;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HistoryService {
    void addHistory(History history);

    void updateHistory(History history);

    List<History> getAllHistory();

    Optional<History> getHistoryById(int id);

    List<History> searchHistory(LocalDate date, String sapPN, String status);
    void createHistoryForScannedMakePN(String makerPN, String employeeId, String scanCode);
    void deleteById(int id);

    List<HistorySummaryViewModel> getSummaryBySapPN();
    boolean isValidMakerPN(String makerPN);

    List<History> searchHistory(String invoiceNo, String maker, String makerPN, String sapPN, LocalDate date);

    boolean isScanning(String scanCode, String makerPN);
}
