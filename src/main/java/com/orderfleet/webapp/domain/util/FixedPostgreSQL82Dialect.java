package com.orderfleet.webapp.domain.util;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class FixedPostgreSQL82Dialect extends PostgreSQL82Dialect {

    public FixedPostgreSQL82Dialect() {
        super();
        registerColumnType(Types.BLOB, "bytea");
        registerColumnType(Types.JAVA_OBJECT, "json");
    }

    @Override
    public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
        if (sqlTypeDescriptor.getSqlType() == java.sql.Types.BLOB) {
            return BinaryTypeDescriptor.INSTANCE;
        }
        return super.remapSqlTypeDescriptor(sqlTypeDescriptor);
    }
}
