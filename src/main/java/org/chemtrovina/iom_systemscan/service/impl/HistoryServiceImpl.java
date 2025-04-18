package org.chemtrovina.iom_systemscan.service.impl;

import org.chemtrovina.iom_systemscan.dto.HistorySummaryViewModel;
import org.chemtrovina.iom_systemscan.model.History;
import org.chemtrovina.iom_systemscan.model.MOQ;
import org.chemtrovina.iom_systemscan.repository.base.HistoryRepository;
import org.chemtrovina.iom_systemscan.repository.base.MOQRepository;
import org.chemtrovina.iom_systemscan.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class  HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final MOQRepository moqRepository;

    @Autowired
    public HistoryServiceImpl(HistoryRepository historyRepository, MOQRepository moqRepository) {
        this.historyRepository = historyRepository;
        this.moqRepository = moqRepository;
    }

    @Override
    public void addHistory(History history) {
        historyRepository.add(history);
    }

    @Override
    public void updateHistory(History history) {
        historyRepository.update(history);
    }

    @Override
    public List<History> getAllHistory() {
        return historyRepository.findAll();
    }

    @Override
    public Optional<History> getHistoryById(int id) {
        return Optional.ofNullable(historyRepository.findById(id));
    }

    @Override
    public List<History> searchHistory(LocalDate date, String sapPN, String status) {
        return historyRepository.findAll().stream()
                .filter(history -> (date == null || history.getDate().equals(date)) &&
                        (sapPN == null || history.getSapPN().equals(sapPN)) &&
                        (status == null || history.getStatus().equals(status)))
                .toList();
    }
    @Override
    public void createHistoryForScannedMakePN(String makerPN, String employeeId, String scanCode) {
        // Tìm MOQ trong bảng MOQ theo sapPN
        MOQ moq = moqRepository.findByMakerPN(makerPN);

        if (moq != null) {
            // Lấy ngày và giờ hiện tại
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.now();

            // Tạo mới đối tượng History
            History history = new History();
            history.setMaker(moq.getMaker());
            history.setMakerPN(makerPN);
            history.setSapPN(moq.getSapPN());
            history.setQuantity(moq.getMoq());  // Lấy quantity từ moq
            history.setDate(currentDate);
            history.setTime(currentTime);
            history.setEmployeeId(employeeId);
            history.setScanCode(scanCode);
            history.setStatus("Scanned");

            // Lưu vào bảng History
            addHistory(history);
        } else {
            // Xử lý khi không tìm thấy MOQ với sapPN được scan
            System.out.println("Không tìm thấy MOQ cho Maker Part Number: " + makerPN);
        }
    }

    @Override
    public void deleteById(int id) {
        historyRepository.delete(id);
    }

    @Override
    public List<HistorySummaryViewModel> getSummaryBySapPN() {
        List<History> histories = historyRepository.findAll();

        return histories.stream()
                .collect(Collectors.groupingBy(History::getSapPN))
                .entrySet().stream()
                .map(entry -> {
                    String sapPN = entry.getKey();
                    List<History> group = entry.getValue();

                    LocalDate latestDate = group.stream()
                            .map(History::getDate)
                            .max(LocalDate::compareTo)
                            .orElse(null);

                    int reelCount = group.size();
                    int totalQuantity = group.stream().mapToInt(History::getQuantity).sum();

                    return new HistorySummaryViewModel(sapPN, latestDate, reelCount, totalQuantity);
                })
                .toList();
    }

    @Override
    public boolean isValidMakerPN(String makerPN) {
        return moqRepository.findByMakerPN(makerPN) != null;
    }

    @Override
    public List<History> searchHistory(String invoiceNo, String maker, String makerPN, String sapPN, LocalDate date) {
        return historyRepository.search(invoiceNo, maker, makerPN, sapPN, date);
    }

    @Override
    public boolean isScanning(String scanCode) {
        return historyRepository.existsByScanCode(scanCode);
    }


}
