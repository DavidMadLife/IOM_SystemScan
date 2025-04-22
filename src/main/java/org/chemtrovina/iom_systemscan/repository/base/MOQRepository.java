package org.chemtrovina.iom_systemscan.repository.base;

import org.chemtrovina.iom_systemscan.model.MOQ;

import java.time.LocalDate;
import java.util.List;

public interface MOQRepository extends GenericRepository<MOQ>{

    MOQ findByMakerPN(String makerPN);
    List<String> findAllMakerPNs();
    List<MOQ> searchMOQ(String maker,String makerPN, String sapPN, String MOQ, String MSL);


}
