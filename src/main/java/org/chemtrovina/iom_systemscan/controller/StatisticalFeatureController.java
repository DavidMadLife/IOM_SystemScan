package org.chemtrovina.iom_systemscan.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    private HistoryService historyService;
    private final ObservableList<History> historyObservableList = FXCollections.observableArrayList(); // Inject nếu dùng DI


    @FXML
    public void initialize() {
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
}
