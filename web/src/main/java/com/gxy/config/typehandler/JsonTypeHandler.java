package com.gxy.config.typehandler;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.sql.*;

/**
 * 继承BaseTypeHandler,实现一个json转object通用的类
 *
 * @author pengyonglei
 * @version 1.0.0
 */
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {

    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        PGobject pGobject = new PGobject();
        pGobject.setType("jsonb");
        pGobject.setValue(GSON.toJson(parameter));
        ps.setObject(i, pGobject);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String data = rs.getString(columnName);
        return StringUtils.isBlank(data) ? null : GSON.fromJson(data, (Class<T>) getRawType());
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String data = rs.getString(columnIndex);
        return StringUtils.isBlank(data) ? null : GSON.fromJson(data, (Class<T>) getRawType());
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String data = cs.getString(columnIndex);
        return StringUtils.isBlank(data) ? null : GSON.fromJson(data, (Class<T>) getRawType());
    }
}
