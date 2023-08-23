package com.indi.product.adapter.out.persistence.jpa.datamodel

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet


class ProductDataModelRowMapper : RowMapper<ProductDataModel> {
    override fun mapRow(rs: ResultSet, rowNum: Int) =
        ProductDataModel(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            salesUnit = rs.getInt("sales_unit"),
            stockSmall = rs.getInt("stock_small"),
            stockMedium = rs.getInt("stock_medium"),
            stockLarge = rs.getInt("stock_large"),
        )

}