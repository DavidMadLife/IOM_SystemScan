package org.chemtrovina.iom_systemscan.service.impl;

import org.chemtrovina.iom_systemscan.model.MOQ;
import org.chemtrovina.iom_systemscan.repository.base.MOQRepository;
import org.chemtrovina.iom_systemscan.service.MOQService;

import java.util.List;

public class MOQServiceImpl implements MOQService {

    private final MOQRepository moqRepository;
    public MOQServiceImpl(MOQRepository moqRepository) {
        this.moqRepository = moqRepository;
    }

    @Override
    public List<MOQ> searchMOQ(String maker, String makerPN, String sapPN, String MOQ, String MSL) {
        return moqRepository.searchMOQ(maker, makerPN, sapPN, MOQ, MSL);
    }
}
