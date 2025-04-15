package org.chemtrovina.iom_systemscan.dto;

import java.time.LocalDate;

public class HistorySummaryViewModel {
    private String sapPN;
    private LocalDate date;
    private int reel;
    private int quantity;

    public HistorySummaryViewModel(String sapPN, LocalDate date, int reel, int quantity) {
        this.sapPN = sapPN;
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
}


