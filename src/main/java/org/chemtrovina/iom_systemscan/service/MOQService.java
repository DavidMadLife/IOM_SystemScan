package org.chemtrovina.iom_systemscan.service;

import org.chemtrovina.iom_systemscan.model.MOQ;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public interface MOQService {
    List<MOQ> searchMOQ(String maker, String makerPN, String sapPN, String MOQ, String MSL);
    void saveImportedData(File file) ;
    void deleteById(int id);
    void updateImportedData(MOQ moq);
}
