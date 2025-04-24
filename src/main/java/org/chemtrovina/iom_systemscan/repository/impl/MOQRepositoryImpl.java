package org.chemtrovina.iom_systemscan.repository.impl;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.chemtrovina.iom_systemscan.model.MOQ;
import org.chemtrovina.iom_systemscan.repository.base.MOQRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MOQRepositoryImpl extends GenericRepositoryImpl<MOQ> implements MOQRepository {

    public MOQRepositoryImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, new MOQRowMapper(), "MOQ");
    }



    @Override
    public void add(MOQ moq) {
        String sql = "INSERT INTO MOQ (Maker, MakerPN, SapPN, MOQ, MSQL) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                moq.getMaker(),
                moq.getMakerPN(),
                moq.getSapPN(),
                moq.getMoq(),
                moq.getMsql());
    }

    @Override
    public void update(MOQ moq) {
        String sql = "UPDATE MOQ SET Maker = ?, MakerPN = ?, SapPN = ?, MOQ = ?, MSQL = ? WHERE Id = ?";
        jdbcTemplate.update(sql,
                moq.getMaker(),
                moq.getMakerPN(),
                moq.getSapPN(),
                moq.getMoq(),
                moq.getMsql(),
                moq.getId());
    }

    @Override
    public List<MOQ> findAll() {
        String sql = "SELECT * FROM MOQ";
        return jdbcTemplate.query(sql, new MOQRowMapper());
    }

    @Override
    public List<String> findAllMakerPNs() {
        String sql = "SELECT MakerPN FROM MOQ";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public List<MOQ> searchMOQ(String maker, String makerPN, String sapPN, String MOQ, String MSL) {
        StringBuilder sql = new StringBuilder("Select * FROM MOQ Where 1=1 ");
        List<Object> params = new java.util.ArrayList<>();
        if (maker != null && !maker.isBlank()) {
            sql.append("AND Maker = ? ");
            params.add(maker);
        }
        if (makerPN != null && !makerPN.isBlank()) {
            sql.append("AND MakerPN = ? ");
            params.add(makerPN);
        }
        if (sapPN != null && !sapPN.isBlank()) {
            sql.append("AND SapPN = ? ");
            params.add(sapPN);
        }
        if (MOQ != null && !MOQ.isBlank()) {
            sql.append("AND MOQ = ? ");
            params.add(Integer.parseInt(MOQ));
        }
        if (MSL != null && !MSL.isBlank()) {
            sql.append("AND MSQL = ? ");
            params.add(Integer.parseInt(MSL));
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), new  MOQRowMapper());
    }


    @Override
    public MOQ findByMakerPN(String makerPN) {
        String sql = "SELECT * FROM MOQ WHERE MakerPN = ?";
        List<MOQ> result = jdbcTemplate.query(sql, new MOQRowMapper(), makerPN);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<MOQ> importMoqFromExcel(File file){
        List<MOQ> moqList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Bỏ dòng tiêu đề
                Row row = sheet.getRow(i);
                if (row == null) continue;

                MOQ moq = new MOQ();
                moq.setSapPN(row.getCell(0).getStringCellValue());
                moq.setMaker(row.getCell(1).getStringCellValue());
                moq.setMakerPN(row.getCell(2).getStringCellValue());
                moq.setMoq((int) row.getCell(3).getNumericCellValue());
                moq.setMsql(row.getCell(4).getStringCellValue());

                moqList.add(moq);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return moqList;
    }


    static class MOQRowMapper implements RowMapper<MOQ> {
        @Override
        public MOQ mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MOQ(
                    rs.getInt("Id"),
                    rs.getString("Maker"),
                    rs.getString("MakerPN"),
                    rs.getString("SapPN"),
                    rs.getInt("MOQ"),
                    rs.getString("MSQL")
            );
        }
    }


}
