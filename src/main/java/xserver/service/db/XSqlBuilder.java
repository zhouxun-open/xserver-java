package xserver.service.db;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import xserver.service.db.xmysql.XMySql;

/**
 * this class has not test, not use
 * 
 * @author x
 *
 */
public class XSqlBuilder {
    public static final String EQUAL = "=";
    public static final String GT = ">";
    public static final String LT = "<";
    public static final String LIKE = "LIKE";
    public StringBuffer sb;
    private String[] fields;
    private List<SqlTriple> conditions;
    private List<Object> values;
    private XPojoExplain explain;
    private XMySql xmysql;

    public XSqlBuilder(XPojoExplain explain, XMySql xmysql) {
        this.sb = new StringBuffer();
        this.conditions = new LinkedList<SqlTriple>();
        this.explain = explain;
        this.xmysql = xmysql;
    }

    public XSqlBuilder select(String... strings) {
        this.fields = strings;
        return this;
    }

    public XSqlBuilder where(String field, String condi, Object value) {
        SqlTriple condition = new SqlTriple(field, condi, value);
        this.conditions.add(condition);
        return this;
    }

    public XSqlBuilder and(String field, String condi, Object value) {
        SqlTriple condition = new SqlTriple(field, condi, value);
        this.conditions.add(condition);
        return this;
    }

    public Map<String, Object> findOne() throws SQLException {
        this.assemblySql();
        String sql = this.sb.toString();
        if (this.values.size() > 0) {
            return this.xmysql.pGetMap(sql, this.values.toArray());
        }
        return this.xmysql.getMap(sql);
    }

    public void assemblySql() {
        this.sb.append("SELECT ");
        for (String field : this.fields) {
            this.sb.append("`");
            this.sb.append(this.explain.fieldToDBField.get(field));
            this.sb.append("` ");
            this.sb.append(" AS `");
            this.sb.append(field);
            this.sb.append("` ");
        }
        this.sb.append("FROM `");
        this.sb.append(this.explain.tableName);
        this.sb.append("` ");
        if (0 != this.conditions.size()) {
            this.sb.append("WHERE ");
            boolean first = true;
            for (SqlTriple condition : this.conditions) {
                if (!first) {
                    this.sb.append("AND ");
                }
                this.sb.append("`");
                this.sb.append(this.explain.fieldToDBField.get(condition.fieldName));
                this.sb.append("` ");
                this.sb.append(condition.condition);
                this.sb.append(" ");
                if (condition.condition.equals(XSqlBuilder.LIKE)) {
                    String v = condition.value.toString();
                    if (false == v.startsWith("'") && false == v.endsWith("\"")) {
                        v = String.format("'%s'", v);
                    }
                    this.sb.append(v);
                } else {
                    this.sb.append("? ");
                    this.values.add(condition.value);
                }
                this.sb.append(" ");
                first = false;
            }
        }
        this.sb.append("LIMIT 1");
    }
}
