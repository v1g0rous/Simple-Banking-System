package banking.util;

import java.util.Map;

public class SqlWrapper {
    private String sql;
    private Map<Integer, Object> sqlArgsByIndex;

    public SqlWrapper(String sql, Map<Integer, Object> sqlArgsByIndex) {
        this.sql = sql;
        this.sqlArgsByIndex = sqlArgsByIndex;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<Integer, Object> getSqlArgsByIndex() {
        return sqlArgsByIndex;
    }

    public void setSqlArgsByIndex(Map<Integer, Object> sqlArgsByIndex) {
        this.sqlArgsByIndex = sqlArgsByIndex;
    }
}
