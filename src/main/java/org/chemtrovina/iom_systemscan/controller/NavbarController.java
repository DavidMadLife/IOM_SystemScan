package org.chemtrovina.iom_systemscan.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class NavbarController {

    @FXML private Button btnScanner;
    @FXML private Button btnStatistical;
    @FXML private Button btnInvoice;

    @FXML
    public void initialize() {
        // Thiết lập trình xử lý sự kiện trực tiếp
        btnScanner.setOnAction(this::handleScannerButton);
        btnStatistical.setOnAction(this::handleStatisticalButton);
        btnInvoice.setOnAction(this::handleInvoiceButton);
    }

    private void handleScannerButton(ActionEvent event) {
        navigateTo("/org/chemtrovina/iom_systemscan/view/scanner-feature.fxml", event);
    }

    private void handleStatisticalButton(ActionEvent event) {
        navigateTo("/org/chemtrovina/iom_systemscan/view/statistical-feature.fxml", event);
    }

    private void handleInvoiceButton(ActionEvent event) {
        navigateTo("/org/chemtrovina/iom_systemscan/view/invoiceData-feature.fxml", event);
    }

    private void navigateTo(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Lấy Stage hiện tại (không tạo mới)
            Stage currentStage = (Stage) btnScanner.getScene().getWindow(); // Hoặc dùng event.getSource()
            currentStage.setScene(new Scene(root));
            currentStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi khi chuyển trang: " + fxmlPath);
        }
    }
}