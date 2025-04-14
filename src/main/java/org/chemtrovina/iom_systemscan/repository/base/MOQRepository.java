package org.chemtrovina.iom_systemscan.repository.base;

import org.chemtrovina.iom_systemscan.model.MOQ;

import java.util.List;

public interface MOQRepository extends GenericRepository<MOQ>{

    MOQ findByMakerPN(String makerPN);

}
