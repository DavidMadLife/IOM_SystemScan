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
    @FXML private TextField scanCodeField;
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
    private String currentScanCode = null;
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

        employeeIdField.setOnAction(e -> scanCodeField.requestFocus());

        scanCodeField.setOnAction(e -> makerPNField.requestFocus());

        makerPNField.setOnAction(e -> {
            String status = statusLabel.getText();
            if (!"Duplicate".equalsIgnoreCase(status) && !"NG".equalsIgnoreCase(status)) {
                handleScan();
            }
        });


        historyTableView.setItems(recentScansList);
        disableAllButtons();
        scanButton.setDisable(false);
    }


    private void handleScan() {
        if (!validateInputs()) return;

        currentEmployeeId = employeeIdField.getText().trim();
        String rawMakerPN = makerPNField.getText().trim();
        currentScanCode = scanCodeField.getText().trim();

        String realMakerPN = historyService.extractRealMakerPN(rawMakerPN);
        if (realMakerPN == null) {
            handleNotFound();
            return;
        }

        currentMakerPN = realMakerPN; // cập nhật lại biến dùng xuyên suốt

        if (historyService.isScanning(currentScanCode, currentMakerPN)) {
            handleDuplicate();
            return;
        }

        if (isScanning && !currentMakerPN.equals(lastScannedHistory.getMakerPN())) {
            handleWrongScan();
            return;
        }

        handleFirstScan();
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

        scanCodeField.requestFocus();
        scanCodeField.selectAll();
    }

    private void handleReScan() {
        if ("Duplicate".equalsIgnoreCase(statusLabel.getText())) {
            scanCodeField.requestFocus();
            scanCodeField.selectAll();
        }

        if ("NG".equalsIgnoreCase(statusLabel.getText())) {
            makerPNField.requestFocus();
            makerPNField.selectAll();
        }


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

        statusLabel.setText("None");
        statusLabel.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
        btnContinue.setDisable(true);
        btnReScan.setDisable(true);
        scanButton.setDisable(false);
        btnAgain.setDisable(true);

    }




    private void handleAgain() {
        // Nếu cần cho scan lại cùng mã hiện tại
        employeeIdField.setText(currentEmployeeId);
        makerPNField.setText(currentMakerPN);
        scanCodeField.setText(currentScanCode);
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



    private boolean validateInputs() {
        if (employeeIdField.getText().trim().isEmpty() || makerPNField.getText().trim().isEmpty()) {
            showStatus("Missing input", "orange");
            return false;
        }
        return true;
    }

    private void handleNotFound() {
        showStatus("NOT FOUND", "gray");
        disableAllButtons();
        scanButton.setDisable(false);
        makerPNField.requestFocus();
        makerPNField.selectAll();
    }

    private void handleDuplicate() {
        showStatus("Duplicate", "yellow");
        createAndStorePendingHistory();
        disableAllButtons();
        btnAgain.setDisable(false);
        btnReScan.setDisable(false);
        scanCodeField.requestFocus();
        scanCodeField.selectAll();
    }

    private void handleWrongScan() {
        showStatus("NG", "red");
        createAndStorePendingHistory();
        disableAllButtons();
        btnReScan.setDisable(false);
        btnContinue.setDisable(false);
        makerPNField.requestFocus();
        makerPNField.selectAll();
    }

    private void handleFirstScan() {
        historyService.createHistoryForScannedMakePN(currentMakerPN, currentEmployeeId, currentScanCode);
        List<History> updated = historyService.getAllHistory();
        lastScannedHistory = updated.get(updated.size() - 1);
        updateRecentScans(lastScannedHistory);

        isScanning = true;
        showStatus("Good", "#32CD32");
        disableAllButtons();
        scanButton.setDisable(false);
        btnReScan.setDisable(false);
        scanCodeField.requestFocus();
        scanCodeField.selectAll();
    }

    private void createAndStorePendingHistory() {
        historyService.createHistoryForScannedMakePN(currentMakerPN, currentEmployeeId, currentScanCode);
        List<History> updated = historyService.getAllHistory();
        pendingHistory = updated.get(updated.size() - 1);
        updateRecentScans(pendingHistory);
    }

}
