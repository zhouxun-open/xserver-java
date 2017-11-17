package com.xuexibao.xserver.service.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xuexibao.xserver.exception.ParamException;
import com.xuexibao.xserver.service.db.xmysql.XMySql;
import com.xuexibao.xserver.service.db.xredis.XRedis;

public class XPersistenceService {
    public XMySql xmysql;
    public XRedis xredis;
    public XPojoExplain explain;

    public XPersistenceService(XMySql mysql, XRedis redis, Class<?> pojoClazz) throws Exception {
        this.xmysql = mysql;
        this.xredis = redis;
        this.explain = XPojoExplain.compile(pojoClazz);
    }

    public Map<String, Object> insert(Map<String, Object> map) throws SQLException, ParamException {
        Map<String, Object> dbMap = this.dbKey(map);
        Long id = this.xmysql.insert(this.explain.tableName, dbMap, true);
        map.put("id", id);
        Map<String, String> redisMap = this.redisMap(map);
        String redisKey = this.redisKey(id);
        this.xredis.hmset(redisKey, redisMap);

        return map;
    }

    public void updateById(Map<String, Object> map) throws ParamException, SQLException {
        Long id = (Long) map.remove("id");
        this.xmysql.updateById(this.explain.tableName, "id", id, map);
    }

    public Map<String, Object> query(Long id) throws SQLException {
        Map<String, Object> obj = null;
        String sql = this.getObjectDetailSql(id);
        obj = this.xmysql.getMap(sql);
        return obj;
    }

    public Map<String, Object> findOne(Map<String, Object> query) throws SQLException {
        query = this.dbKey(query);
        String sql = this.selectFields();
        sql = String.format("%s%s LIMIT 1", sql, this.getWhere(query));
        List<Object> objs = new ArrayList<>();
        for (String key : query.keySet()) {
            objs.add(query.get(key));
        }
        return this.xmysql.pGetMap(sql, objs.toArray());
    }

    public List<Map<String, Object>> getObjectList(List<Long> ids) throws SQLException {
        String sql = this.getObjectListSql(ids);
        return this.xmysql.getListMap(sql);
    }

    public Map<Long, Map<String, Object>> getObjectMapping(List<Long> ids) throws SQLException {
        String sql = this.getObjectListSql(ids);
        return this.xmysql.getObjectMapping(sql);
    }

    private String redisKey(Long id) {
        String redisKey = String.format("%s%d", this.explain.identifier, id);
        return redisKey;
    }

    private Map<String, Object> dbKey(Map<String, Object> map) {
        Map<String, Object> dbMap = new HashMap<String, Object>();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (this.explain.fieldToDBField.containsKey(key)) {
                key = this.explain.fieldToDBField.get(key);
            }
            dbMap.put(key, value);
        }
        return dbMap;
    }

    private Map<String, String> redisMap(Map<String, Object> map) {
        Map<String, String> redisMap = new HashMap<String, String>();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (null != value) {
                redisMap.put(key, value.toString());
            }
        }
        return redisMap;
    }

    // private Map<String, Object> fromRedisMap(Map<String, String> redisMap) {
    // Map<String, Object> map = new HashMap<String, Object>();
    // for (String key : redisMap.keySet()) {
    // XFieldType type = this.explain.fieldType.get(key);
    // String strValue = redisMap.get(key);
    // Object value = strValue;
    // if(null == type)
    // continue;
    // switch (type) {
    // case INTEGER:
    // value = Integer.parseInt(strValue);
    // break;
    // case LONG:
    // value = Long.parseLong(strValue);
    // break;
    // case STRING:
    // break;
    // case FLOAT:
    // value = Float.parseFloat(strValue);
    // break;
    // case DOUBLE:
    // value = Double.parseDouble(strValue);
    // break;
    // default:
    // break;
    // }
    // map.put(key, value);
    // }
    // return map;
    // }

    private String getObjectListSql(List<Long> ids) {
        StringBuffer sb = new StringBuffer();
        sb.append(this.selectFields());
        sb.append(" WHERE `id` IN ");
        String in = this.xmysql.getWhereIn(ids);
        sb.append(in);
        return sb.toString();
    }

    private String getObjectDetailSql(Long objId) {
        StringBuffer sb = new StringBuffer();
        sb.append(this.selectFields());
        sb.append(" WHERE `id` = ");
        sb.append(objId);
        return sb.toString();
    }

    private String getWhere(Map<String, Object> query) {
        if (null == query || 0 == query.size()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(" WHERE ");
        boolean first = true;
        for (String key : query.keySet()) {
            if (!first) {
                sb.append(" AND ");
            }
            sb.append(" `");
            sb.append(key);
            sb.append("` = ?");
            first = false;
        }
        return sb.toString();
    }

    private String selectFields() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT ");
        for (String dbField : this.explain.dbFieldToPojoField.keySet()) {
            sb.append(" `");
            sb.append(dbField);
            sb.append("` AS `");
            sb.append(this.explain.dbFieldToPojoField.get(dbField));
            sb.append("`,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" FROM `");
        sb.append(this.explain.tableName);
        sb.append("` ");
        return sb.toString();
    }
}
