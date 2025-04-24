package org.chemtrovina.iom_systemscan.dto;

import java.time.LocalDate;

public class HistorySummaryViewModel {
    private String sapPN;
    private String makerPN;
    private LocalDate date;
    private int reel;
    private int quantity;

    public HistorySummaryViewModel(String sapPN, String makerPN, LocalDate date, int reel, int quantity) {
        this.sapPN = sapPN;
        this.makerPN = makerPN;
        this.date = date;
        this.reel = reel;
        this.quantity = quantity;


    }

    public String getSapPN() { return sapPN; }
    public void setSapPN(String sapPN) { this.sapPN = sapPN; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getReel() { return reel; }
    public void setReel(int reel) { this.reel = reel; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getMakerPN() { return makerPN; }
    public void setMakerPN(String makerPN) { this.makerPN = makerPN; }
}


