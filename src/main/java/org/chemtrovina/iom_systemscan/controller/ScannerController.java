package org.chemtrovina.iom_systemscan.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.chemtrovina.iom_systemscan.model.History;
import org.chemtrovina.iom_systemscan.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ScannerController {

    @FXML private TextField employeeIdField;
    @FXML private TextField makerPNField;
    @FXML private Button scanButton;
    @FXML private Label statusLabel;
    @FXML private TableView<History> historyTableView;
    @FXML private TableColumn<History, String> dateColumn;
    @FXML private TableColumn<History, String> sapColumn;
    @FXML private TableColumn<History, Integer> reelColumn;
    @FXML private TableColumn<History, Integer> quantityColumn;

    @Autowired private HistoryService historyService;

    private ObservableList<History> historyData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Gán cột cho table
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        sapColumn.setCellValueFactory(new PropertyValueFactory<>("sapPN"));
        reelColumn.setCellValueFactory(new PropertyValueFactory<>("reelCount"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        historyTableView.setItems(historyData);

        // Gán sự kiện nút Scan
        scanButton.setOnAction(e -> onScan());
    }

    private void onScan() {
        String makerPN = makerPNField.getText().trim();
        String employeeId = employeeIdField.getText().trim();

        if (makerPN.isEmpty() || employeeId.isEmpty()) {
            showStatus("None", "#5B5B5B"); // Xám
            return;
        }

        try {
            historyService.createHistoryForScannedMakePN(makerPN, employeeId);

            // Lấy lại danh sách history liên quan
            List<History> all = historyService.getAllHistory().stream()
                    .filter(h -> h.getMakerPN().equals(makerPN))
                    .toList();

            if (all.isEmpty()) {
                showStatus("Not Found", "#5B5B5B");
                return;
            }

            // Tính tổng reel và quantity
            int reelCount = all.size();
            int totalQuantity = all.stream().mapToInt(History::getQuantity).sum();
            String sapPN = all.get(0).getSapPN(); // Lấy từ entry đầu tiên
            String today = all.get(all.size() - 1).getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            History display = new History();
            display.setDate(all.get(all.size() - 1).getDate()); // Hiện ngày mới nhất
            display.setSapPN(sapPN);
            display.setReelCount(reelCount); // custom field
            display.setQuantity(totalQuantity);

            historyData.add(display);
            showStatus("Good", "#4CAF50"); // Xanh lá cây

        } catch (Exception ex) {
            ex.printStackTrace();
            showStatus("NG", "#B00020"); // Đỏ đậm
        }
    }

    private void showStatus(String text, String color) {
        statusLabel.setText(text);
        statusLabel.setStyle("-fx-background-color: " + color + "; -fx-font-weight: Bold;");
    }
}
