package org.chemtrovina.iom_systemscan.service.impl;

import org.chemtrovina.iom_systemscan.model.Invoice;
import org.chemtrovina.iom_systemscan.repository.base.InvoiceRepository;
import org.chemtrovina.iom_systemscan.service.InvoiceService;

import java.util.List;

public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public void createInvoice(Invoice invoice) {
        // Có thể validate ở đây nếu cần
        invoiceRepository.add(invoice);
    }

    @Override
    public void updateInvoice(Invoice invoice) {
        invoiceRepository.update(invoice);
    }

    @Override
    public void deleteInvoice(Long id) {
        invoiceRepository.delete(id);
    }
}
