package org.chemtrovina.iom_systemscan.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.chemtrovina.iom_systemscan.config.DataSourceConfig;
import org.chemtrovina.iom_systemscan.dto.HistorySummaryViewModel;
import org.chemtrovina.iom_systemscan.model.History;
import org.chemtrovina.iom_systemscan.repository.base.HistoryRepository;
import org.chemtrovina.iom_systemscan.repository.base.MOQRepository;
import org.chemtrovina.iom_systemscan.repository.impl.HistoryRepositoryImpl;
import org.chemtrovina.iom_systemscan.repository.impl.MOQRepositoryImpl;
import org.chemtrovina.iom_systemscan.service.HistoryService;
import org.chemtrovina.iom_systemscan.service.impl.HistoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ScannerController {

    @FXML private TextField employeeIdField;
    @FXML private TextField makerPNField;
    @FXML private Button scanButton;
    @FXML private Label statusLabel;
    @FXML private TableView<HistorySummaryViewModel> historyTableView;
    @FXML private TableColumn<HistorySummaryViewModel, String> dateColumn;
    @FXML private TableColumn<HistorySummaryViewModel, String> sapColumn;
    @FXML private TableColumn<HistorySummaryViewModel, Integer> reelColumn;
    @FXML private TableColumn<HistorySummaryViewModel, Integer> quantityColumn;
    @FXML private Button btnContinue;
    @FXML private Button btnReScan;
    @FXML private Button btnAgain;

    private String currentMakerPN = null;
    private String currentEmployeeId = null;
    private boolean isScanning = false;
    private History lastScannedHistory = null; // mã A
    private History pendingHistory = null;     // mã B

    private HistoryService historyService;
    private final ObservableList<History> historyObservableList = FXCollections.observableArrayList();
    private final ObservableList<HistorySummaryViewModel> summaryObservableList = FXCollections.observableArrayList();
    private final ObservableList<HistorySummaryViewModel> recentScans = FXCollections.observableArrayList();
    // Quản lý 2 mã gần nhất, giữ thứ tự thêm vào
    private final LinkedHashMap<String, HistorySummaryViewModel> recentScansMap = new LinkedHashMap<>();

    // Observable list dùng để hiển thị trong TableView
    private final ObservableList<HistorySummaryViewModel> recentScansList = FXCollections.observableArrayList();

    public void initialize() {

        // Setup cột table
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        sapColumn.setCellValueFactory(new PropertyValueFactory<>("sapPN"));
        reelColumn.setCellValueFactory(new PropertyValueFactory<>("reel"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        DataSource dataSource = DataSourceConfig.getDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        HistoryRepository historyRepository = new HistoryRepositoryImpl(jdbcTemplate);
        MOQRepository moqRepository = new MOQRepositoryImpl(jdbcTemplate);
        historyService = new HistoryServiceImpl(historyRepository,moqRepository);

        // Gán sự kiện cho nút Scan
        scanButton.setOnAction(event -> handleScan());

        btnContinue.setOnAction(e -> handleContinue());
        btnReScan.setOnAction(e -> handleReScan());
        btnAgain.setOnAction(e -> handleAgain());
        makerPNField.setOnAction(e -> handleScan());


        historyTableView.setItems(recentScansList);
        disableAllButtons();
        scanButton.setDisable(false);
    }

    private void loadSummaryTable() {
        List<HistorySummaryViewModel> summaries = historyService.getSummaryBySapPN();
        List<HistorySummaryViewModel> sortedSummaries = summaries.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(2) // lấy 2 mã gần nhất
                .toList();
        summaryObservableList.setAll(sortedSummaries);
        historyTableView.setItems(summaryObservableList);
    }


    private void handleScan() {
        String employeeId = employeeIdField.getText().trim();
        String makerPN = makerPNField.getText().trim();

        if (employeeId.isEmpty() || makerPN.isEmpty()) {
            statusLabel.setText("Missing input");
            statusLabel.setStyle("-fx-background-color: orange;");
            return;
        }

        if (!historyService.isValidMakerPN(makerPN)) {
            statusLabel.setText("NOT FOUND");
            statusLabel.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
            disableAllButtons();
            scanButton.setDisable(false);
            return;
        }

        // Nếu đang scan mã A mà nhập mã B
        if (isScanning && !makerPN.equals(currentMakerPN)) {
            historyService.createHistoryForScannedMakePN(makerPN, employeeId);
            List<History> updated = historyService.getAllHistory();
            pendingHistory = updated.get(updated.size() - 1);

            updateRecentScans(pendingHistory);

            statusLabel.setText("NG");
            statusLabel.setStyle("-fx-background-color: red;");
            scanButton.setDisable(true);
            btnAgain.setDisable(true);
            btnContinue.setDisable(false);
            btnReScan.setDisable(false);
            return;
        }

        // Scan mã đầu tiên
        historyService.createHistoryForScannedMakePN(makerPN, employeeId);
        List<History> updated = historyService.getAllHistory();

        lastScannedHistory = updated.get(updated.size() - 1);

        updateRecentScans(lastScannedHistory);
        currentMakerPN = makerPN;
        currentEmployeeId = employeeId;
        isScanning = true;

        statusLabel.setText("Good");
        statusLabel.setStyle("-fx-background-color: #32CD32;"); // xanh chuối
        disableAllButtons();
        scanButton.setDisable(false);

        makerPNField.clear();
    }

    private void handleContinue() {
        if (pendingHistory == null) return;

        // Nếu history cũ đã Completed → cho cập nhật lại
        if ("Scanned".equalsIgnoreCase(lastScannedHistory.getStatus())) {
            // Cập nhật current sang pendind
            lastScannedHistory = pendingHistory;
            currentMakerPN = pendingHistory.getMakerPN();
            currentEmployeeId = pendingHistory.getEmployeeId();
        }

        pendingHistory = null;
        isScanning = true;

        statusLabel.setText("Good");
        statusLabel.setStyle("-fx-background-color: #32CD32;");
        disableAllButtons();
        scanButton.setDisable(false);

        makerPNField.requestFocus();
        makerPNField.selectAll();
    }

    private void handleReScan() {
        if (pendingHistory != null) {
            historyService.deleteById(pendingHistory.getId());

            String sapPN = pendingHistory.getSapPN();
            int qty = pendingHistory.getQuantity();

            if (recentScansMap.containsKey(sapPN)) {
                HistorySummaryViewModel summary = recentScansMap.get(sapPN);

                summary.setReel(summary.getReel() - 1);
                summary.setQuantity(summary.getQuantity() - qty);

                if (summary.getReel() > 0) {
                    recentScansMap.put(sapPN, summary);
                } else {
                    recentScansMap.remove(sapPN);
                }

                recentScansList.setAll(recentScansMap.values());
            }

            pendingHistory = null;
        }

        statusLabel.setText("Good");
        statusLabel.setStyle("-fx-background-color: #32CD32;");
        btnContinue.setDisable(true);
        btnReScan.setDisable(true);
        scanButton.setDisable(false);
        btnAgain.setDisable(false);
    }




    private void handleAgain() {
        // Nếu cần cho scan lại cùng mã hiện tại
        employeeIdField.setText(currentEmployeeId);
        makerPNField.setText(currentMakerPN);
        scanButton.setDisable(false);
        statusLabel.setText("None");
        statusLabel.setStyle("-fx-background-color: #5B5B5B;");
    }

    private void showStatus(String message, String colorHex) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-background-color: " + colorHex + "; -fx-font-weight: bold;");
    }

    private void disableAllButtons() {
        scanButton.setDisable(true);
        btnAgain.setDisable(true);
        btnContinue.setDisable(true);
        btnReScan.setDisable(true);
    }

    /*private void updateHistoryTable(List<History> historyList) {
        ObservableList<History> observableHistory = FXCollections.observableArrayList(historyList);
        historyTableView.setItems(observableHistory);
    }*/


    private void updateRecentScans(History lastScannedHistory) {
        String sapPN = lastScannedHistory.getSapPN();
        int quantity = lastScannedHistory.getQuantity();

        // Kiểm tra nếu đang scan cùng mã đã có trong recentScansMap
        if (recentScansMap.containsKey(sapPN)) {
            // Nếu đã tồn tại, cộng dồn reel và quantity
            HistorySummaryViewModel existing = recentScansMap.get(sapPN);
            existing.setReel(existing.getReel() + 1);
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            // Nếu mã mới, xử lý thêm vào danh sách
            if (recentScansMap.size() >= 2) {
                // Xóa mã cũ nhất (nếu có)
                String oldestKey = recentScansMap.keySet().iterator().next();
                recentScansMap.remove(oldestKey);
            }

            // Tạo mới HistorySummaryViewModel cho mã vừa scan
            HistorySummaryViewModel newSummary = new HistorySummaryViewModel(
                    sapPN,
                    lastScannedHistory.getDate(),
                    1,
                    quantity
            );
            recentScansMap.put(sapPN, newSummary);
        }

        // Cập nhật lại danh sách hiển thị
        recentScansList.setAll(recentScansMap.values());
    }



}
