package org.chemtrovina.iom_systemscan.repository.base;

import org.chemtrovina.iom_systemscan.model.History;

import java.time.LocalDate;
import java.util.List;

public interface HistoryRepository extends GenericRepository<History> {
    List<History> search(String invoiceNo, String maker, String makerPN, String sapPN, LocalDate date);
}
