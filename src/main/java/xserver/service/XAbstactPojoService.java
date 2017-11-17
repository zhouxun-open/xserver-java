package xserver.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import xserver.XServerConfig;
import xserver.exception.ParamException;
import xserver.service.db.XPersistenceService;
import xserver.service.db.xmysql.XMySql;
import xserver.service.db.xredis.XRedis;

public class XAbstactPojoService {
    public XPersistenceService xPersistence;

    public XAbstactPojoService(String xmysqlConfigKey, String xredisConfigKey, Class<?> clazz)
            throws SQLException, Exception {
        XServerConfig xserverconfig = XServerConfig.getInst();
        XMySql xmysql = new XMySql(xserverconfig.getMySqlConfig(xmysqlConfigKey));
        XRedis xredis = new XRedis(xserverconfig.getRedisConfig(xredisConfigKey));
        this.xPersistence = new XPersistenceService(xmysql, xredis, clazz);
    }

    public Map<String, Object> newObject(Map<String, Object> object) throws ParamException, SQLException {
        object = this.xPersistence.insert(object);
        return object;
    }

    public void executeUpdate(String sql) throws SQLException {
        this.getXMysql().update(sql);
    }

    public void executePrepareUpdate(String sql, Object... params) throws SQLException {
        this.getXMysql().pUpdate(sql, params);
    }

    public List<Map<String, Object>> objects(List<Long> ids) throws SQLException {
        return this.xPersistence.getObjectList(ids);
    }

    public Map<Long, Map<String, Object>> objectMapping(List<Long> ids) throws SQLException {
        return this.xPersistence.getObjectMapping(ids);
    }

    public Map<String, Object> detail(Long id) throws SQLException {
        return this.xPersistence.query(id);
    }

    public void updateById(Map<String, Object> object) throws ParamException, SQLException {
        this.xPersistence.updateById(object);
    }

    public XMySql getXMysql() {
        return this.xPersistence.xmysql;
    }

    public XRedis getXRedis() {
        return this.xPersistence.xredis;
    }

    public Jedis getJedis() {
        return this.xPersistence.xredis.getJedis();
    }

    public Connection getMysqlCon() throws SQLException {
        return this.xPersistence.xmysql.getConnection();
    }
}
