package org.chemtrovina.iom_systemscan.model;

public class InvoiceDetail {
    private int id;
    private int invoiceId;
    private String maker;
    private String makerPN;
    private String sapPN;
    private String reel;
    private int quantity;

    public InvoiceDetail() {
    }

    public InvoiceDetail(int id, int invoiceId, String maker, String makerPN,
                         String sapPN, String reel, int quantity) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.maker = maker;
        this.makerPN = makerPN;
        this.sapPN = sapPN;
        this.reel = reel;
        this.quantity = quantity;
    }

    // Getter và Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getMakerPN() {
        return makerPN;
    }

    public void setMakerPN(String makerPN) {
        this.makerPN = makerPN;
    }

    public String getSapPN() {
        return sapPN;
    }

    public void setSapPN(String sapPN) {
        this.sapPN = sapPN;
    }

    public String getReel() {
        return reel;
    }

    public void setReel(String reel) {
        this.reel = reel;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return reel + " - " + makerPN + " (" + quantity + ")";
    }
}
