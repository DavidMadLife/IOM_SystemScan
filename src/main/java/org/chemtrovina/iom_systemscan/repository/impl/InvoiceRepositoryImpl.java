package org.chemtrovina.iom_systemscan.repository.impl;

import org.chemtrovina.iom_systemscan.model.Invoice;
import org.chemtrovina.iom_systemscan.repository.base.InvoiceRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InvoiceRepositoryImpl extends GenericRepositoryImpl<Invoice> implements InvoiceRepository {

    public InvoiceRepositoryImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, new InvoiceRowMapper(), "Invoice"); // Sửa thành "Invoice" thay vì "Invoices"
    }

    @Override
    public void add(Invoice invoice) {
        String sql = "INSERT INTO Invoice (InvoiceNo, InvoiceDate, Supplier, TotalReel, TotalQuantity, CreatedAt) " +
                "VALUES (?, ?, ?, ?, ?, ?)"; // Sửa tên cột cho đúng với database
        jdbcTemplate.update(sql,
                invoice.getInvoiceNo(),
                invoice.getInvoiceDate(),
                invoice.getSupplier(),
                invoice.getTotalReel(),
                invoice.getTotalQuantity(),
                invoice.getCreatedAt()
        );
    }

    @Override
    public void update(Invoice invoice) {
        String sql = "UPDATE Invoice SET InvoiceNo = ?, InvoiceDate = ?, Supplier = ?, " +
                "TotalReel = ?, TotalQuantity = ?, CreatedAt = ? WHERE Id = ?"; // Sửa tên cột
        jdbcTemplate.update(sql,
                invoice.getInvoiceNo(),
                invoice.getInvoiceDate(),
                invoice.getSupplier(),
                invoice.getTotalReel(),
                invoice.getTotalQuantity(),
                invoice.getCreatedAt(),
                invoice.getId()
        );
    }

    // Thêm phương thức findAll nếu chưa có trong GenericRepositoryImpl
    @Override
    public List<Invoice> findAll() {
        String sql = "SELECT * FROM Invoice";
        return jdbcTemplate.query(sql, new InvoiceRowMapper());
    }

    static class InvoiceRowMapper implements RowMapper<Invoice> {
        @Override
        public Invoice mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Invoice(
                    rs.getInt("Id"), // Sửa thành "Id" thay vì "id"
                    rs.getString("InvoiceNo"),
                    rs.getDate("InvoiceDate").toLocalDate(),
                    rs.getString("Supplier"),
                    rs.getInt("TotalReel"),
                    rs.getInt("TotalQuantity"),
                    rs.getDate("CreatedAt").toLocalDate()
            );
        }
    }
}