package org.chemtrovina.iom_systemscan.model;

import java.time.LocalDate;

public class Invoice {
    private int id;
    private String invoiceNo;
    private LocalDate invoiceDate;
    private String supplier;
    private Integer totalReel;
    private Integer totalQuantity;
    private LocalDate createdAt;

    public Invoice() {
        // Constructor mặc định
    }

    public Invoice(int id, String invoiceNo, LocalDate invoiceDate, String supplier,
                   Integer totalReel, Integer totalQuantity, LocalDate createdAt) {
        this.id = id;
        this.invoiceNo = invoiceNo;
        this.invoiceDate = invoiceDate;
        this.supplier = supplier;
        this.totalReel = totalReel;
        this.totalQuantity = totalQuantity;
        this.createdAt = createdAt;
    }

    // Getter và Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Integer getTotalReel() {
        return totalReel;
    }

    public void setTotalReel(Integer totalReel) {
        this.totalReel = totalReel;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return invoiceNo + " - " + supplier;
    }
}
