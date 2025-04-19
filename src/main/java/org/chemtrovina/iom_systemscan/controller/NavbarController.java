package org.chemtrovina.iom_systemscan.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.chemtrovina.iom_systemscan.model.FxmlPage;

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
        navigateTo("/org/chemtrovina/iom_systemscan/view/scanner-feature.fxml");
    }

    private void handleStatisticalButton(ActionEvent event) {
        navigateTo("/org/chemtrovina/iom_systemscan/view/statistical-feature.fxml");
    }

    private void handleInvoiceButton(ActionEvent event) {
        navigateTo("/org/chemtrovina/iom_systemscan/view/invoiceData-feature.fxml");
    }

    private void navigateTo(String fxmlPath) {
        try {
            FxmlPage page = FXMLCacheManager.getPage(fxmlPath);
            Parent view = page.getView();

            // Gán view mới vào mainContentPane trong MainController
            AnchorPane contentPane = MainController.getMainContentPane();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi khi load view: " + fxmlPath);
        }
    }

}