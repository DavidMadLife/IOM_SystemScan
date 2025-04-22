package org.chemtrovina.iom_systemscan.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.chemtrovina.iom_systemscan.config.DataSourceConfig;
import org.chemtrovina.iom_systemscan.model.MOQ;
import org.chemtrovina.iom_systemscan.repository.base.MOQRepository;
import org.chemtrovina.iom_systemscan.repository.impl.MOQRepositoryImpl;
import org.chemtrovina.iom_systemscan.service.MOQService;
import org.chemtrovina.iom_systemscan.service.impl.MOQServiceImpl;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportDataController {
    @FXML private TableView<MOQ> moqTableView;
    @FXML private TableColumn<MOQ, Integer> noColumn;
    @FXML private TableColumn<MOQ, String> makerColumn;
    @FXML private TableColumn<MOQ, String> makerPNColumn;
    @FXML private TableColumn<MOQ, String> sapPNColumn;
    @FXML private TableColumn<MOQ, Integer> moqColumn;
    @FXML private TableColumn<MOQ, String> mslColumn;

    @FXML private TextField makerField;
    @FXML private TextField pnField;
    @FXML private TextField sapField;
    @FXML private TextField moqField;
    @FXML private TextField mslField;

    @FXML private CheckBox makerCheckBox;
    @FXML private CheckBox pnCheckBox;
    @FXML private CheckBox sapCheckBox;
    @FXML private CheckBox moqCheckBox;
    @FXML private CheckBox mslCheckBox;

    @FXML private Button chooseFileBtn;
    @FXML private Button btnImportData;
    @FXML private Button btnSearch;
    @FXML private Text fileNameLabel;

    private File selectedFile;

    private final List<MOQ> allData = new ArrayList<>(); // để lưu toàn bộ dữ liệu từ Excel

    private MOQService moqService;
    private final ObservableList<MOQ> moqObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        //noColumn.setCellValueFactory(new PropertyValueFactory<>("no"));
        makerColumn.setCellValueFactory(new PropertyValueFactory<>("maker"));
        makerPNColumn.setCellValueFactory(new PropertyValueFactory<>("makerPN"));
        sapPNColumn.setCellValueFactory(new PropertyValueFactory<>("sapPN"));
        moqColumn.setCellValueFactory(new PropertyValueFactory<>("moq"));
        mslColumn.setCellValueFactory(new PropertyValueFactory<>("msl"));

        DataSource dataSource = DataSourceConfig.getDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        MOQRepository moqRepository = new MOQRepositoryImpl(jdbcTemplate);
        moqService = new MOQServiceImpl(moqRepository);

        chooseFileBtn.setOnAction(e -> chooseFile());
        btnImportData.setOnAction(e -> importDataFromExcel());
        btnSearch.setOnAction(e -> onSearch());

    }

    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file Excel");

        // Chỉ cho phép chọn file Excel (.xlsx)
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel Files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        // Lấy cửa sổ hiện tại để hiển thị FileChooser
        Stage stage = (Stage) chooseFileBtn.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            selectedFile = file;
            fileNameLabel.setText(file.getName());
        } else {
            fileNameLabel.setText("Chưa chọn file");
        }
    }
    private void importDataFromExcel() {}
    private void onSearch() {
        String maker = makerCheckBox.isSelected() ? makerField.getText() : null;
        String pn = pnCheckBox.isSelected() ? pnField.getText() : null;
        String sap = sapCheckBox.isSelected() ? sapField.getText() : null;
        String moq = moqCheckBox.isSelected() ? moqField.getText() : null;
        String msl = mslCheckBox.isSelected() ? mslField.getText() : null;

        List<MOQ> results = moqService.searchMOQ(maker, pn, sap, moq, msl);
        ObservableList<MOQ> observableList = FXCollections.observableArrayList(results);
        moqTableView.setItems(observableList);

    }
}
