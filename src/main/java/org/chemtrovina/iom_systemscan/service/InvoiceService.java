package org.chemtrovina.iom_systemscan.service;

import org.chemtrovina.iom_systemscan.model.Invoice;

import java.util.List;

public interface InvoiceService {
    Invoice getInvoiceById(int id);
    List<Invoice> getAllInvoices();
    void createInvoice(Invoice invoice);
    void updateInvoice(Invoice invoice);
    void deleteInvoice(int id);
}
