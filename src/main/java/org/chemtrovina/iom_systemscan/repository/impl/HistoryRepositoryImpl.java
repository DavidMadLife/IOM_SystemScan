package org.chemtrovina.iom_systemscan.repository.impl;

import org.chemtrovina.iom_systemscan.model.History;
import org.chemtrovina.iom_systemscan.repository.base.HistoryRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class HistoryRepositoryImpl extends GenericRepositoryImpl<History> implements HistoryRepository {

    public HistoryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, new HistoryRowMapper(), "History"); // "History" là tên bảng trong database
    }

    @Override
    public void add(History history) {
        String sql = "INSERT INTO History (InvoiceId, Date, Time, Maker, MakerPN, SapPN, Quantity, EmployeeId, Status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                history.getInvoiceId(),
                history.getDate(),
                history.getTime(),
                history.getMaker(),
                history.getMakerPN(),
                history.getSapPN(),
                history.getQuantity(),
                history.getEmployeeId(),
                history.getStatus()
        );
    }

    @Override
    public void update(History history) {
        String sql = "UPDATE History SET InvoiceId = ?, Date = ?, Time = ?, Maker = ?, MakerPN = ?, SapPN = ?, " +
                "Quantity = ?, EmployeeId = ?, Status = ? WHERE Id = ?";
        jdbcTemplate.update(sql,
                history.getInvoiceId(),
                history.getDate(),
                history.getTime(),
                history.getMaker(),
                history.getMakerPN(),
                history.getSapPN(),
                history.getQuantity(),
                history.getEmployeeId(),
                history.getStatus(),
                history.getId()
        );
    }

    @Override
    public List<History> findAll() {
        String sql = "SELECT * FROM History";
        return jdbcTemplate.query(sql, new HistoryRowMapper());
    }

    @Override
    public History findById(int id) {
        String sql = "SELECT * FROM History WHERE Id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new HistoryRowMapper());
    }

    @Override
    public List<History> search(String invoiceNo, String maker, String makerPN, String sapPN, LocalDate date) {
        StringBuilder sql = new StringBuilder(
                "SELECT h.* FROM History h LEFT JOIN Invoice i ON h.InvoiceId = i.Id WHERE 1=1 "
        );

        // Danh sách parameter
        List<Object> params = new java.util.ArrayList<>();

        if (invoiceNo != null && !invoiceNo.isBlank()) {
            sql.append("AND i.InvoiceNo = ? ");
            params.add(invoiceNo);
        }

        if (maker != null && !maker.isBlank()) {
            sql.append("AND h.Maker = ? ");
            params.add(maker);
        }

        if (makerPN != null && !makerPN.isBlank()) {
            sql.append("AND h.MakerPN = ? ");
            params.add(makerPN);
        }

        if (sapPN != null && !sapPN.isBlank()) {
            sql.append("AND h.SapPN = ? ");
            params.add(sapPN);
        }

        if (date != null) {
            sql.append("AND h.Date = ? ");
            params.add(date);
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), new HistoryRowMapper());
    }



    static class HistoryRowMapper implements RowMapper<History> {
        @Override
        public History mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new History(
                    rs.getInt("Id"),
                    rs.getInt("InvoiceId"),
                    rs.getDate("Date").toLocalDate(),
                    rs.getTime("Time").toLocalTime(),
                    rs.getString("Maker"),
                    rs.getString("MakerPN"),
                    rs.getString("SapPN"),
                    rs.getInt("Quantity"),
                    rs.getString("EmployeeId"),
                    rs.getString("Status")
            );
        }
    }
}
