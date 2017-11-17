package xserver.service.db.xredis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class XRedis {
    public JedisPool pool;
    private String password;

    public XRedis(XRedisConfig xredisConfig) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(xredisConfig.maxIdle);
        config.setTestOnBorrow(xredisConfig.testOnBorrow);
        config.setMaxTotal(xredisConfig.maxTotal);
        config.setMaxWaitMillis(xredisConfig.maxWaitMillis);
        this.pool = new JedisPool(config, xredisConfig.host, xredisConfig.port);
        this.password = xredisConfig.password;
    }

    public Jedis getJedis() {
        Jedis jedis = this.pool.getResource();
        if (null != this.password && 0 != this.password.length()) {
            jedis.auth(this.password);
        }
        return jedis;
    }

    public Set<String> keys(String keyPtn) {
        Jedis jedis = this.getJedis();
        Set<String> set = null;
        try {
            set = jedis.keys(keyPtn);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
        return set;
    }

    public void set(String key, String value) {
        Jedis jedis = this.getJedis();
        try {
            jedis.set(key, value);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
    }

    public void setEx(String key, String value, int second) {
        Jedis jedis = this.getJedis();
        try {
            jedis.setex(key, second, value);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
    }

    public String get(String key) {
        Jedis jedis = this.getJedis();
        String value = null;
        try {
            value = jedis.get(key);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
        return value;
    }

    public String hget(String key, String type) {
        Jedis jedis = this.getJedis();
        String value = "";
        try {
            value = jedis.hget(key, type);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
        return value;
    }

    public void hset(String key, String type, String value) {
        Jedis jedis = this.getJedis();
        try {
            jedis.hset(key, type, value);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
    }

    public boolean exist(String key) {
        Jedis jedis = this.getJedis();
        boolean exist = false;
        try {
            exist = jedis.exists(key);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
        return exist;
    }

    public void publish(String key, String message) {
        Jedis jedis = this.getJedis();
        try {
            jedis.publish(key, message);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
    }

    public void hmset(String key, Map<String, String> redisMap) {
        Jedis jedis = this.getJedis();
        try {
            jedis.hmset(key, redisMap);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
    }

    public void rpush(String key, String... value) {
        Jedis jedis = this.getJedis();
        try {
            jedis.rpush(key, value);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
    }

    public List<String> lrange(String key, int start, int end) {
        Jedis jedis = this.getJedis();
        try {
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
        return null;
    }

    public String lset(String key, int index, String value) {
        Jedis jedis = this.getJedis();
        try {
            return jedis.lset(key, index, value);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
        return null;
    }

    public String ltrim(String key, long start, long end) {
        Jedis jedis = this.getJedis();
        try {
            return jedis.ltrim(key, start, end);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
        return null;
    }

    public Integer del(String key) {
        Jedis jedis = this.getJedis();
        try {
            return jedis.del(key).intValue();
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
        return null;
    }

    public Map<String, String> hgetAll(String redisKey) {
        Jedis jedis = this.getJedis();
        try {
            return jedis.hgetAll(redisKey);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
        return null;
    }

    public void expire(String key, int second) {
        Jedis jedis = this.getJedis();
        try {
            jedis.expire(key, second);
        } catch (Exception e) {
            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
    }
}