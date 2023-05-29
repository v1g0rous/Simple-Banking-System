package banking.util;

import org.sqlite.SQLiteDataSource;
import banking.entity.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteManager implements DatabaseManager {
    private static String databaseName;
    private static Connection connection;
    public static final String CREATE_CARD_TABLE_SQL = "CREATE TABLE IF NOT EXISTS card(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "number TEXT," + "pin TEXT," + "balance INTEGER DEFAULT 0);";


    public SQLiteManager(String[] args) {
        this.databaseName = getDatabaseName(args);
        initDatabaseWithTable();
    }

    private void initDatabaseWithTable() {

        Connection connection = getConnectionInstance();

        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_CARD_TABLE_SQL);
            statement.executeUpdate();
        } catch (Exception e) {
            new Log("Error executing sql statement: " + CREATE_CARD_TABLE_SQL, e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                new Log("Error closing connection to DB" + databaseName, e);
            }
        }
    }

    public static Connection getConnectionInstance() {
        return getConnectionInstance(databaseName);
    }

    private static Connection getConnectionInstance(String fileName) {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            } else {
                String url = "jdbc:sqlite:" + fileName;

                SQLiteDataSource dataSource = new SQLiteDataSource();
                dataSource.setUrl(url);
                connection = dataSource.getConnection();
            }

        } catch (SQLException e) {
            new Log("Can't get connection to DB -" + fileName, e);
        }

        return connection;
    }


    public static int executeUpdate(SqlWrapper sqlWrapper) {
        Connection connection = getConnectionInstance();
        int updatedRecords = 0;

        try {
            PreparedStatement statement = connection.prepareStatement(sqlWrapper.getSql());
            SQLiteManager.setSqlArguments(statement, sqlWrapper.getSqlArgsByIndex());

            updatedRecords = statement.executeUpdate();
        } catch (Exception e) {
            new Log("Error executing sql statement: " + sqlWrapper.getSql(), e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                new Log("Error closing connection to DB" + databaseName, e);
            }
        }

        return updatedRecords;
    }

    // connection remains open till caller closes it when transaction is over
    private static int executeUpdateAsTransaction(Connection connection, SqlWrapper sqlWrapper) {
        int updatedRecords = 0;

        try {
            PreparedStatement statement = connection.prepareStatement(sqlWrapper.getSql());
            SQLiteManager.setSqlArguments(statement, sqlWrapper.getSqlArgsByIndex());

            updatedRecords = statement.executeUpdate();
        } catch (Exception e) {
            new Log("Error executing sql statement: " + sqlWrapper.getSql(), e);
        }

        return updatedRecords;
    }


    public static int executeUpdateAsTransaction(List<SqlWrapper> sqlWrappers) {
        Connection connection = getConnectionInstance();
        int updatedRecords = 0;

        try {
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();

            try {
                for (SqlWrapper sqlWrapper : sqlWrappers) {
                    updatedRecords += SQLiteManager.executeUpdateAsTransaction(connection, sqlWrapper);
                }
                connection.commit();

            } catch (Exception e) {
                new Log("Failed to execute sql statement", e);
                try {
                    connection.rollback(savepoint);
                } catch (SQLException ex) {
                    new Log("Failed to rollback", e);
                }
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    new Log("Error closing connection to DB" + databaseName, e);
                }
            }
        } catch (SQLException e) {
            new Log("Failed to executeUpdateAsTransaction", e);
        }

        return updatedRecords;
    }


    // sqlArgsByIndex contains settings for each ? placeholder in prepared JDBC statement
    private static void setSqlArguments(PreparedStatement statement, Map<Integer, Object> sqlArgsByIndex) throws SQLException {
        if (sqlArgsByIndex != null && sqlArgsByIndex.size() > 0) {
            for (Integer argIndex : sqlArgsByIndex.keySet()) {
                Object argValue = sqlArgsByIndex.get(argIndex);

                statement.setObject(argIndex, argValue);
            }
        }
    }

    public static List<Map<String, Object>> executeSelectQuery(SqlWrapper sqlWrapper) {
        List<Map<String, Object>> records = new ArrayList<>();

        Connection connection = getConnectionInstance();

        try {
            PreparedStatement statement = connection.prepareStatement(sqlWrapper.getSql());
            SQLiteManager.setSqlArguments(statement, sqlWrapper.getSqlArgsByIndex());

            ResultSet recordsSet = statement.executeQuery();

            ResultSetMetaData metaData = recordsSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (recordsSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), recordsSet.getObject(i));
                }
                records.add(row);
            }

        } catch (Exception e) {
            new Log("Error executing sql statement: " + sqlWrapper.getSql(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                new Log("Error closing connection to DB", e);
            }
        }

        return records;
    }


    private static String getDatabaseName(String[] args) {
        String name = "";
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-fileName")) {
                name = args[i + 1];
            }
        }
        return name;
    }
}
