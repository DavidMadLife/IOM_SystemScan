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
        btnScanner.setOnAction(e -> navigateTo("/org/chemtrovina/iom_systemscan/view/scanner-feature.fxml", e));
        btnStatistical.setOnAction(e -> navigateTo("/org/chemtrovina/iom_systemscan/view/statistical-feature.fxml", e));
        btnInvoice.setOnAction(e -> navigateTo("/org/chemtrovina/iom_systemscan/view/invoiceData-feature.fxml", e));
    }

    private void navigateTo(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
