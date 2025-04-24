package org.chemtrovina.iom_systemscan.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.chemtrovina.iom_systemscan.config.DataSourceConfig;
import org.chemtrovina.iom_systemscan.model.History;
import org.chemtrovina.iom_systemscan.repository.base.HistoryRepository;
import org.chemtrovina.iom_systemscan.repository.base.MOQRepository;
import org.chemtrovina.iom_systemscan.repository.impl.HistoryRepositoryImpl;
import org.chemtrovina.iom_systemscan.repository.impl.MOQRepositoryImpl;
import org.chemtrovina.iom_systemscan.service.HistoryService;
import org.chemtrovina.iom_systemscan.service.impl.HistoryServiceImpl;
import org.springframework.jdbc.core.JdbcTemplate;


import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

public class StatisticalFeatureController {

    @FXML private TableView<History> historyDateTableView;
    @FXML private TableColumn<History, String> dateColumn;
    @FXML private TableColumn<History, String> makerColumn;
    @FXML private TableColumn<History, String> makerPNColumn;
    @FXML private TableColumn<History, String> sapPNColumn;
    @FXML private TableColumn<History, Integer> quantityColumn;
    @FXML private TableColumn<History, String> scanCodeColumn;

    @FXML private TextField invoiceNoField;
    @FXML private TextField makerField;
    @FXML private TextField pnField;
    @FXML private TextField sapField;
    @FXML private DatePicker dateTimePicker;

    @FXML private CheckBox invoiceNoCheckBox;
    @FXML private CheckBox makerCheckBox;
    @FXML private CheckBox pnCheckBox;
    @FXML private CheckBox sapCheckBox;
    @FXML private CheckBox dateCheckBox;

    @FXML private Button searchBtn;

    @FXML private Text reelText;
    @FXML private Text quantityText;

    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;

    private HistoryService historyService;
    private final ObservableList<History> historyObservableList = FXCollections.observableArrayList(); // Inject nếu dùng DI


    @FXML
    public void initialize() {

        historyDateTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateBtn.setDisable(false);
                deleteBtn.setDisable(false);
            } else {
                updateBtn.setDisable(true);
                deleteBtn.setDisable(true);
            }
        });

        // Gán các cột với property trong HistoryEntrance
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date")); // cần có field date dạng String
        makerColumn.setCellValueFactory(new PropertyValueFactory<>("maker"));
        makerPNColumn.setCellValueFactory(new PropertyValueFactory<>("makerPN"));
        sapPNColumn.setCellValueFactory(new PropertyValueFactory<>("sapPN"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        scanCodeColumn.setCellValueFactory(new PropertyValueFactory<>("scanCode"));

        DataSource dataSource = DataSourceConfig.getDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        HistoryRepository historyRepository = new HistoryRepositoryImpl(jdbcTemplate);
        MOQRepository moqRepository = new MOQRepositoryImpl(jdbcTemplate);
        historyService = new HistoryServiceImpl(historyRepository,moqRepository);

        searchBtn.setOnAction(e -> onSearch());

        deleteBtn.setOnAction(e -> onDeleteSelectedRow());
        updateBtn.setOnAction(e -> onUpdateSelectedRow());
    }

    private void onSearch() {
        String invoiceNo = invoiceNoCheckBox.isSelected() ? invoiceNoField.getText() : null;
        String maker = makerCheckBox.isSelected() ? makerField.getText() : null;
        String pn = pnCheckBox.isSelected() ? pnField.getText() : null;
        String sap = sapCheckBox.isSelected() ? sapField.getText() : null;
        LocalDate date = dateCheckBox.isSelected() ? dateTimePicker.getValue() : null;

        List<History> result = historyService.searchHistory(invoiceNo, maker, pn, sap, date);

        ObservableList<History> observableList = FXCollections.observableArrayList(result);
        historyDateTableView.setItems(observableList);

        int totalReels = result.size();
        int totalQuantity = result.stream().mapToInt(History::getQuantity).sum();

        reelText.setText(String.valueOf(totalReels));
        quantityText.setText(String.valueOf(totalQuantity));
    }

    private void onDeleteSelectedRow() {
        History selected = historyDateTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Accept Delete");
            alert.setHeaderText("Are you sure you want to delete this record?");
            alert.setContentText("ScanCode: " + selected.getScanCode());

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(okButton, cancelButton);

            alert.showAndWait().ifPresent(type -> {
                if (type == okButton) {
                    historyService.deleteById(selected.getId());
                    historyDateTableView.getItems().remove(selected);

                    // Cập nhật số lượng reel và tổng quantity sau khi xóa
                    reelText.setText(String.valueOf(historyDateTableView.getItems().size()));
                    quantityText.setText(String.valueOf(
                            historyDateTableView.getItems().stream().mapToInt(History::getQuantity).sum()
                    ));
                }
            });
        }
    }

    private void onUpdateSelectedRow() {
        History selected = historyDateTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Dialog<History> dialog = new Dialog<>();
            dialog.setTitle("Cập nhật thông tin");
            dialog.setHeaderText("Chỉnh sửa thông tin bản ghi");

            ButtonType updateButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            // Tạo các trường nhập
            TextField makerField = new TextField(selected.getMaker());
            TextField makerPNField = new TextField(selected.getMakerPN());
            TextField sapPNField = new TextField(selected.getSapPN());
            TextField quantityField = new TextField(String.valueOf(selected.getQuantity()));

            // Giao diện form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(new Label("Maker:"), 0, 0);
            grid.add(makerField, 1, 0);
            grid.add(new Label("Maker P/N:"), 0, 1);
            grid.add(makerPNField, 1, 1);
            grid.add(new Label("SAP P/N:"), 0, 2);
            grid.add(sapPNField, 1, 2);
            grid.add(new Label("Quantity:"), 0, 3);
            grid.add(quantityField, 1, 3);

            dialog.getDialogPane().setContent(grid);

            // Xử lý khi nhấn nút Cập nhật
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    try {
                        int updatedQuantity = Integer.parseInt(quantityField.getText().trim());

                        // Cập nhật thông tin
                        selected.setMaker(makerField.getText().trim());
                        selected.setMakerPN(makerPNField.getText().trim());
                        selected.setSapPN(sapPNField.getText().trim());
                        selected.setQuantity(updatedQuantity);

                        return selected;
                    } catch (NumberFormatException e) {
                        showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Quantity phải là số nguyên.");
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(updated -> {

                System.out.println("Update ID: " + updated.getId());

                historyService.updateHistory(updated); // Gọi DB update
                historyDateTableView.refresh(); // Cập nhật TableView
                quantityText.setText(String.valueOf(
                        historyDateTableView.getItems().stream().mapToInt(History::getQuantity).sum()
                ));
            });
        }
    }


    //Alert
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }



}
