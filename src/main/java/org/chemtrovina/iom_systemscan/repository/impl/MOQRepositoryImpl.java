package org.chemtrovina.iom_systemscan.repository.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
        String sql = "INSERT INTO MOQ (Maker, MakerPN, SapPN, MOQ, MSQL, Spec) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                moq.getMaker(),
                moq.getMakerPN(),
                moq.getSapPN(),
                moq.getMoq(),
                moq.getMsql(),
                moq.getSpec());
    }

    @Override
    public void update(MOQ moq) {
        String sql = "UPDATE MOQ SET Maker = ?, MakerPN = ?, SapPN = ?, MOQ = ?, MSQL = ?, Spec = ? WHERE Id = ?";
        jdbcTemplate.update(sql,
                moq.getMaker(),
                moq.getMakerPN(),
                moq.getSapPN(),
                moq.getMoq(),
                moq.getMsql(),
                moq.getSpec(),
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
            params.add(MSL);
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
                moq.setSapPN(getCellValueAsString(row.getCell(0)));
                moq.setSpec(getCellValueAsString(row.getCell(1)));
                moq.setMaker(getCellValueAsString(row.getCell(3)));
                moq.setMakerPN(getCellValueAsString(row.getCell(2)));
                moq.setMoq((int) getCellValueAsNumeric(row.getCell(4)));
                moq.setMsql(getCellValueAsString(row.getCell(5)));

                moqList.add(moq);
            }
            System.out.println("Đọc được " + moqList.size() + " dòng từ Excel");

            //saveAll(moqList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return moqList;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue()); // nếu muốn hiển thị không có .0
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    private double getCellValueAsNumeric(Cell cell) {
        if (cell == null) {
            return 0;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    //Save all
    @Override
    public void saveAll(List<MOQ> moqList) {
        String sql = "INSERT INTO MOQ (Maker, MakerPN, SapPN, MOQ, MSQL, Spec) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new org.springframework.jdbc.core.BatchPreparedStatementSetter() {
            @Override
            public void setValues(java.sql.PreparedStatement ps, int i) throws SQLException {
                MOQ moq = moqList.get(i);
                ps.setString(1, moq.getMaker());
                ps.setString(2, moq.getMakerPN());
                ps.setString(3, moq.getSapPN());
                ps.setInt(4, moq.getMoq());
                ps.setString(5, moq.getMsql());
                ps.setString(6, moq.getSpec());
            }

            @Override
            public int getBatchSize() {
                return moqList.size();
            }
        });
    }

    public void updateAll(List<MOQ> moqList) {
        String sql = "UPDATE MOQ SET Maker = ?, MakerPN = ?, SapPN = ?, MOQ = ?, MSQL = ?, Spec = ? WHERE Id = ?";

        jdbcTemplate.batchUpdate(sql, new org.springframework.jdbc.core.BatchPreparedStatementSetter() {
            @Override
            public void setValues(java.sql.PreparedStatement ps, int i) throws SQLException {
                MOQ moq = moqList.get(i);
                ps.setString(1, moq.getMaker());
                ps.setString(2, moq.getMakerPN());
                ps.setString(3, moq.getSapPN());
                ps.setInt(4, moq.getMoq());
                ps.setString(5, moq.getMsql());
                ps.setString(6, moq.getSpec());
                ps.setInt(7, moq.getId());
            }

            @Override
            public int getBatchSize() {
                return moqList.size();
            }
        });
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
                    rs.getString("MSQL"),
                    rs.getString("Spec")
            );
        }
    }
}
