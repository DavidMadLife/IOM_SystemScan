package org.chemtrovina.iom_systemscan.service.impl;

import org.chemtrovina.iom_systemscan.model.MOQ;
import org.chemtrovina.iom_systemscan.repository.base.GenericRepository;
import org.chemtrovina.iom_systemscan.repository.base.MOQRepository;
import org.chemtrovina.iom_systemscan.repository.impl.GenericRepositoryImpl;
import org.chemtrovina.iom_systemscan.service.MOQService;

import java.io.File;
import java.util.ArrayList;
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

    /*@Override
    public void saveImportedData(File file) {
        List<MOQ> moqs = moqRepository.importMoqFromExcel(file);
        for (MOQ moq : moqs) {
            MOQ existing = moqRepository.findByMakerPN(moq.getMakerPN());
            if (existing == null) {
                moqRepository.add(moq);
            } else {
                moq.setId(existing.getId());
                moqRepository.update(moq);
            }
        }
    }*/

    @Override
    public void saveImportedData(File file) {
        List<MOQ> moqs = moqRepository.importMoqFromExcel(file);

        // Lấy hết MakerPN đang có trong database
        List<String> existingMakerPNs = moqRepository.findAllMakerPNs();

        List<MOQ> toInsert = new ArrayList<>();
        List<MOQ> toUpdate = new ArrayList<>();

        for (MOQ moq : moqs) {
            if (existingMakerPNs.contains(moq.getMakerPN())) {
                // Tìm đúng ID để update
                MOQ existing = moqRepository.findByMakerPN(moq.getMakerPN());
                moq.setId(existing.getId());
                toUpdate.add(moq);
            } else {
                toInsert.add(moq);
            }
        }

        // Batch insert và batch update
        if (!toInsert.isEmpty()) {
            moqRepository.saveAll(toInsert);
        }
        if (!toUpdate.isEmpty()) {
            moqRepository.updateAll(toUpdate);
        }
    }

    @Override
    public void deleteById(int id) {
        moqRepository.delete(id);
    }

    @Override
    public void updateImportedData(MOQ moq) {
        moqRepository.update(moq);
    }


}
