package com.xuexibao.xserver.service.db.xmysql;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.xuexibao.xserver.exception.ParamException;
import com.xuexibao.xserver.util.ObjectToMapUtil;
import com.xuexibao.xserver.util.StringUtil;

public class XMySql {
    public DataSource dataSource;

    public XMySql(XMySqlConfig xmysqlConfig) throws SQLException {
        this.dataSource = this.init(xmysqlConfig);
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    private DataSource init(XMySqlConfig config) throws SQLException {
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(config.url, config.user,
                config.password);

        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        poolableConnectionFactory.setValidationQuery("select 1");
        poolableConnectionFactory.setDefaultReadOnly(false);
        poolableConnectionFactory.setDefaultAutoCommit(true);

        GenericObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<PoolableConnection>(
                poolableConnectionFactory);

        connectionPool.setMaxTotal(config.maxActive);
        connectionPool.setMinIdle(config.minIdle);
        connectionPool.setMaxIdle(config.maxIdle);
        connectionPool.setMaxWaitMillis(config.maxWait);
        connectionPool.setTestOnBorrow(false);
        connectionPool.setTestOnReturn(false);
        connectionPool.setTestWhileIdle(true);
        AbandonedConfig abandonedConfig = new AbandonedConfig();
        abandonedConfig.setRemoveAbandonedOnBorrow(true);
        abandonedConfig.setRemoveAbandonedOnMaintenance(true);
        abandonedConfig.setRemoveAbandonedTimeout(180);
        connectionPool.setAbandonedConfig(abandonedConfig);
        connectionPool.setNumTestsPerEvictionRun(config.numTestsPerEvictionRun);
        connectionPool.setTimeBetweenEvictionRunsMillis(config.timeBetweenEvictionRunsMillis);
        connectionPool.setMinEvictableIdleTimeMillis(config.minEvictableIdleTimeMillis);

        poolableConnectionFactory.setPool(connectionPool);

        PoolingDataSource<PoolableConnection> ds = new PoolingDataSource<PoolableConnection>(connectionPool);

        Connection conn = ds.getConnection();
        conn.close();

        return ds;
    }

    public Object getObject(String sql) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        try {
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getObject(1);
            }
            return null;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public Integer getInt(String sql) throws SQLException {
        Object obj = this.getObject(sql);
        if (obj != null) {
            return (Integer) obj;
        }
        return null;
    }

    public Long getLong(String sql) throws SQLException {
        Object obj = this.getObject(sql);
        if (obj != null) {
            return (Long) obj;
        }
        return null;
    }

    public String getString(String sql) throws SQLException {
        Object obj = this.getObject(sql);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public Date getDate(String sql) throws SQLException {
        Object obj = this.getObject(sql);
        if (obj != null) {
            return (Date) obj;
        }
        return null;
    }

    public Object pGetObject(String sql, Object... objects) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i + 1, objects[i]);
            }
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getObject(1);
            }
            return null;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public Integer pGetInt(String sql, Object... objects) throws SQLException {
        Object obj = this.pGetObject(sql, objects);
        if (obj != null) {
            return (Integer) obj;
        }
        return null;
    }

    public Long pGetLong(String sql, Object... objects) throws SQLException {
        Object obj = this.pGetObject(sql, objects);
        if (obj != null) {
            return (Long) obj;
        }
        return null;
    }

    public String pGetString(String sql, Object... objects) throws SQLException {
        Object obj = this.pGetObject(sql, objects);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public Date pGetDate(String sql, Object... objects) throws SQLException {
        Object obj = this.pGetObject(sql, objects);
        if (obj != null) {
            return (Date) obj;
        }
        return null;
    }

    public List<Object> pGetObjList(String sql, Object... objects) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i + 1, objects[i]);
            }
            rs = stmt.executeQuery();
            List<Object> list = new LinkedList<Object>();
            while (rs.next()) {
                list.add(rs.getObject(1));
            }
            return list;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public List<Object> getObjList(String sql) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        try {
            rs = stmt.executeQuery(sql);
            List<Object> list = new LinkedList<Object>();
            while (rs.next()) {
                list.add(rs.getObject(1));
            }
            return list;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public List<Integer> getIntegerList(String sql) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        try {
            rs = stmt.executeQuery(sql);
            List<Integer> list = new LinkedList<Integer>();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
            return list;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public List<Integer> pGetIntegerList(String sql, Object... objects) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i + 1, objects[i]);
            }
            rs = stmt.executeQuery();
            List<Integer> list = new LinkedList<Integer>();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
            return list;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public List<Long> getLongList(String sql) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        try {
            rs = stmt.executeQuery(sql);
            List<Long> list = new LinkedList<Long>();
            while (rs.next()) {
                list.add(rs.getLong(1));
            }
            return list;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public List<Long> pGetLongList(String sql, Object... objects) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i + 1, objects[i]);
            }
            rs = stmt.executeQuery();
            List<Long> list = new LinkedList<Long>();
            while (rs.next()) {
                list.add(rs.getLong(1));
            }
            return list;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public List<String> getStringList(String sql) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        try {
            rs = stmt.executeQuery(sql);
            List<String> list = new LinkedList<String>();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            return list;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public List<String> pGetStringList(String sql, Object... objects) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i + 1, objects[i]);
            }
            rs = stmt.executeQuery();
            List<String> list = new LinkedList<String>();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            return list;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public Map<String, Object> getMap(String sql) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        try {
            rs = stmt.executeQuery(sql);
            Map<String, Object> map = new HashMap<String, Object>();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                int count = rsmd.getColumnCount();
                for (int i = 0; i < count; i++) {
                    map.put(rsmd.getColumnLabel(i + 1), rs.getObject(i + 1));
                }
            }
            return map;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public Map<String, Object> pGetMap(String sql, Object... objects) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i + 1, objects[i]);
            }
            rs = stmt.executeQuery();
            Map<String, Object> map = new HashMap<String, Object>();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                int count = rsmd.getColumnCount();
                for (int i = 0; i < count; i++) {
                    map.put(rsmd.getColumnLabel(i + 1), rs.getObject(i + 1));
                }
            }
            return map;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public List<Map<String, Object>> getListMap(String sql) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        try {
            rs = stmt.executeQuery(sql);
            List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                int count = rsmd.getColumnCount();
                for (int i = 0; i < count; i++) {
                    map.put(rsmd.getColumnLabel(i + 1), rs.getObject(i + 1));
                }
                list.add(map);
            }
            return list;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public <T> List<T> getListObject(String sql, Class<T> clazz)
            throws SQLException, InstantiationException, IllegalAccessException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        try {
            rs = stmt.executeQuery(sql);
            List<T> list = new LinkedList<T>();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                int count = rsmd.getColumnCount();
                for (int i = 0; i < count; i++) {
                    map.put(rsmd.getColumnLabel(i + 1), rs.getObject(i + 1));
                }
                T t = ObjectToMapUtil.getObjectByMap(clazz.newInstance(), map);
                list.add(t);
            }
            return list;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public List<Map<String, Object>> pGetListMap(String sql, Object... objects) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i + 1, objects[i]);
            }
            rs = stmt.executeQuery();
            List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                int count = rsmd.getColumnCount();
                for (int i = 0; i < count; i++) {
                    map.put(rsmd.getColumnLabel(i + 1), rs.getObject(i + 1));
                }
                list.add(map);
            }
            return list;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public <T> List<T> pGetListObject(String sql, Class<T> clazz, Object... objects)
            throws SQLException, InstantiationException, IllegalAccessException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i + 1, objects[i]);
            }
            rs = stmt.executeQuery();
            List<T> list = new LinkedList<T>();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                int count = rsmd.getColumnCount();
                for (int i = 0; i < count; i++) {
                    map.put(rsmd.getColumnLabel(i + 1), rs.getObject(i + 1));
                }
                T t = ObjectToMapUtil.getObjectByMap(clazz.newInstance(), map);
                list.add(t);
            }
            return list;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public Set<Long> pGetLongSet(String sql, Object... objects) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i + 1, objects[i]);
            }
            rs = stmt.executeQuery();
            Set<Long> set = new HashSet<Long>();
            while (rs.next()) {
                set.add(rs.getLong(1));
            }
            return set;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }

    public Long update(String sql) throws SQLException {
        Connection conn = this.getConnection();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            return ((Integer) stmt.executeUpdate(sql)).longValue();
        } finally {
            XMySql.closeConnection(null, stmt, conn);
        }
    }

    public int pUpdate(String sql, Object... objects) throws SQLException {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++) {
                stmt.setObject(i + 1, objects[i]);
            }
            return stmt.executeUpdate();
        } finally {
            XMySql.closeConnection(null, stmt, conn);
        }
    }

    public Long mInsert(String tableName, Map<String, Object> obj) throws SQLException {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        try {
            List<Object> param = new LinkedList<Object>();
            String sql = "INSERT INTO `" + tableName + "`(";
            Iterator<String> it = obj.keySet().iterator();
            String values = " VALUES(";
            while (it.hasNext()) {
                String columnName = it.next();
                sql = sql + "`" + columnName + "`, ";
                values = values + "?, ";
                param.add(obj.get(columnName));
            }
            sql = sql.substring(0, sql.length() - 2);
            values = values.substring(0, values.length() - 2);
            sql = sql + ") " + values + ")";
            stmt = conn.prepareStatement(sql);
            Iterator<Object> iter = param.iterator();
            int i = 1;
            while (iter.hasNext()) {
                stmt.setObject(i++, iter.next());
            }
            return ((Integer) stmt.executeUpdate()).longValue();
        } finally {
            XMySql.closeConnection(null, stmt, conn);
        }
    }

    public int mUpdate(String tableName, Map<String, Object> query, Map<String, Object> update) throws SQLException {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = "UPDATE `" + tableName + "` SET ";
            Iterator<String> it = update.keySet().iterator();
            List<Object> param = new LinkedList<Object>();
            while (it.hasNext()) {
                String columnName = it.next();
                sql = sql + " `" + columnName + "` = ?, ";
                param.add(update.get(columnName));
            }
            sql = sql.substring(0, sql.length() - 2);
            if (query.size() > 0) {
                sql = sql + " WHERE ";
            }
            it = query.keySet().iterator();
            while (it.hasNext()) {
                String columnName = it.next();
                sql = sql + "`" + columnName + "` = ?, ";
                param.add(query.get(columnName));
            }
            sql = sql.substring(0, sql.length() - 2);
            stmt = conn.prepareStatement(sql);
            Iterator<Object> iter = param.iterator();
            int i = 1;
            while (iter.hasNext()) {
                stmt.setObject(i++, iter.next());
            }
            return stmt.executeUpdate();
        } finally {
            XMySql.closeConnection(null, stmt, conn);
        }
    }

    public String sortString(StringBuffer sb, Map<String, Integer> map) {
        if (map.size() == 0) {
            return sb.toString();
        }
        sb.append(" order by ");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                sb.append(entry.getKey() + " asc,");
            } else {
                sb.append(entry.getKey() + " desc,");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static boolean isNumber(Object obj) {
        if (obj instanceof Long || obj instanceof Integer || obj instanceof Double || obj instanceof Short
                || obj instanceof BigInteger) {
            return true;
        }
        return false;
    }

    public static void closeConnection(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
        }

        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
        }
    }

    public <T> String getWhereIn(Collection<T> ids) {
        if (ids == null || 0 == ids.size()) {
            return "()";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        Iterator<T> it = ids.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append(", ");
        }
        sb.replace(sb.length() - 2, sb.length(), ")");
        return sb.toString();
    }

    public Long insert(String tableName, Map<String, Object> params, boolean generateKey)
            throws SQLException, ParamException {
        if (StringUtil.checkNullAndEmpty(tableName)) {
            throw new ParamException("insert operation: tableName is need");
        }
        if (params == null || params.size() == 0) {
            throw new ParamException("insert operation: params is need");
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder value = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append("(");
        value.append(" VALUES (");
        for (Map.Entry<String, Object> m : params.entrySet()) {
            if (m.getValue() != null) {
                sb.append("`" + m.getKey() + "`").append(",");
                if (m.getValue() instanceof String) {
                    value.append("'" + m.getValue() + "'");
                } else {
                    value.append(m.getValue());
                }
                value.append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        value.deleteCharAt(value.length() - 1);
        value.append(")");
        sb.append(")").append(value);
        if (generateKey) {
            String sql = sb.toString();
            System.out.println(sql);
            return this.insertGetGenerateKey(sql);
        }
        return this.update(sb.toString());
    }

    private Long insertGetGenerateKey(String sql) throws SQLException {
        Connection conn = this.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            while (rs.next()) {
                return rs.getLong(1);
            }
        } finally {
            XMySql.closeConnection(rs, pstmt, conn);
        }
        return 0L;
    }

    public Long insertMany(String tableName, List<Map<String, Object>> paramList) throws ParamException, SQLException {
        if (StringUtil.checkNullAndEmpty(tableName)) {
            throw new ParamException("insertMany operation: tableName is need");
        }
        if (paramList == null || paramList.size() == 0) {
            throw new ParamException("insertMany operation: paramList is need");
        }
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append("(");
        StringBuilder values = new StringBuilder("VALUES");
        boolean flag = true;
        for (Map<String, Object> m : paramList) {
            values.append("(");
            for (Map.Entry<String, Object> entry : m.entrySet()) {
                if (flag) {
                    sql.append("`" + entry.getKey() + "`,");
                }
                if (entry.getValue() instanceof String) {
                    values.append("'" + entry.getValue() + "'");
                } else {
                    values.append(entry.getValue());
                }
                values.append(",");
            }
            values.deleteCharAt(values.length() - 1);
            values.append("),");
            flag = false;
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(") ");
        values.deleteCharAt(values.length() - 1);
        sql.append(values);
        return this.update(sql.toString());
    }

    public Long updateById(String tableName, String idFieldName, Object idValue, Map<String, Object> updateMap)
            throws ParamException, SQLException {
        if (StringUtil.checkNullAndEmpty(tableName)) {
            throw new ParamException(tableName + " update operation: tableName is need");
        }
        if (idValue == null) {
            throw new ParamException(tableName + " update operation: idValue is null");
        }
        if (updateMap == null || updateMap.size() == 0) {
            throw new ParamException(tableName + " update operation: updateMap is need");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(tableName).append(" SET ");
        for (Map.Entry<String, Object> m : updateMap.entrySet()) {
            sb.append("`").append(m.getKey()).append("`").append("=");
            if (m.getValue() instanceof String) {
                sb.append("'" + m.getValue() + "'");
            } else {
                sb.append(m.getValue());
            }
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" WHERE ");
        if (idFieldName != null) {
            sb.append("`").append(idFieldName).append("`").append("=");

        } else {
            sb.append("`id`=");
        }
        if (idValue instanceof String) {
            sb.append("'" + idValue + "'");
        } else {
            sb.append(idValue);
        }
        return this.update(sb.toString());
    }

    public Map<Long, Map<String, Object>> getObjectMapping(String sql) throws SQLException {
        Connection conn = this.getConnection();
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        try {
            Map<Long, Map<String, Object>> mapping = new HashMap<Long, Map<String, Object>>();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                int count = rsmd.getColumnCount();
                for (int i = 0; i < count; i++) {
                    map.put(rsmd.getColumnLabel(i + 1), rs.getObject(i + 1));
                }
                mapping.put((Long) map.get("id"), map);
            }
            return mapping;
        } finally {
            XMySql.closeConnection(rs, stmt, conn);
        }
    }
}
