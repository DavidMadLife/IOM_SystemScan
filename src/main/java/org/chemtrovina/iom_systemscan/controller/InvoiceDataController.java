package org.chemtrovina.iom_systemscan.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.chemtrovina.iom_systemscan.config.DataSourceConfig;
import org.chemtrovina.iom_systemscan.model.Invoice;
import org.chemtrovina.iom_systemscan.service.InvoiceService;
import org.chemtrovina.iom_systemscan.service.impl.InvoiceServiceImpl;
import org.chemtrovina.iom_systemscan.repository.base.InvoiceRepository;
import org.chemtrovina.iom_systemscan.repository.impl.InvoiceRepositoryImpl;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.LocalDate;

public class InvoiceDataController {

    @FXML
    private TableView<Invoice> invoiceTableView;

    @FXML
    private TableColumn<Invoice, String> invoiceNoColumn;

    @FXML
    private TableColumn<Invoice, LocalDate> invoiceDateColumn;

    @FXML
    private TableColumn<Invoice, String> makerColumn;

    @FXML
    private TableColumn<Invoice, Integer> reelColumn;

    @FXML
    private TableColumn<Invoice, Integer> quantityColumn;

    private InvoiceService invoiceService;
    private final ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup columns
        invoiceNoColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceNo"));
        invoiceDateColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));
        makerColumn.setCellValueFactory(new PropertyValueFactory<>("supplier")); // supplier ~ maker
        reelColumn.setCellValueFactory(new PropertyValueFactory<>("totalReel"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));

        // Tạo DataSource
        DataSource dataSource = DataSourceConfig.getDataSource();

        // Khởi tạo JdbcTemplate và repository
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        InvoiceRepository invoiceRepository = new InvoiceRepositoryImpl(jdbcTemplate);
        invoiceService = new InvoiceServiceImpl(invoiceRepository);

        // Load data
        loadInvoices();
    }

    private void loadInvoices() {
        invoiceList.clear();
        invoiceList.addAll(invoiceService.getAllInvoices());
        invoiceTableView.setItems(invoiceList);
    }
}
