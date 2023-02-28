package de.schwarzes_brett.data_access.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * This utility class provides utility functions for database access.
 *
 * @author Tim-Florian Feulner
 */
final class DAOPsqlUtil {

    /**
     * Private constructor, as this class is a utility class.
     */
    private DAOPsqlUtil() {}

    /**
     * Inserts a nullable string into the given {@code PreparedStatement}.
     *
     * @param parameterIndex The index where to insert.
     * @param value          The value to insert.
     * @param statement      The statement to insert into.
     * @throws SQLException If the insert threw this exception.
     */
    static void insertNullableString(int parameterIndex, String value, PreparedStatement statement) throws SQLException {
        if (value == null || value.equals("")) {
            statement.setNull(parameterIndex, Types.VARCHAR);
        } else {
            statement.setString(parameterIndex, value);
        }
    }

    /**
     * Inserts a nullable integer into the given {@code PreparedStatement}.
     *
     * @param parameterIndex The index where to insert.
     * @param value          The value to insert.
     * @param statement      The statement to insert into.
     * @throws SQLException If the insert threw this exception.
     */
    static void insertNullableInteger(int parameterIndex, Integer value, PreparedStatement statement) throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, Types.INTEGER);
        } else {
            statement.setInt(parameterIndex, value);
        }
    }

    /**
     * Inserts a nullable long into the given {@code PreparedStatement}.
     *
     * @param parameterIndex The index where to insert.
     * @param value          The value to insert.
     * @param statement      The statement to insert into.
     * @throws SQLException If the insert threw this exception.
     */
    static void insertNullableLong(int parameterIndex, Long value, PreparedStatement statement) throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, Types.BIGINT);
        } else {
            statement.setLong(parameterIndex, value);
        }
    }

    /**
     * Extracts a nullable string from the given {@code ResultSet}.
     *
     * @param column The column name to extract from.
     * @param result The source of the data.
     * @return The extracted {@code String} value, or {@code null} if that value was {@code NULL} in the database.
     * @throws SQLException If the access threw this exception.
     */
    static String extractNullableString(String column, ResultSet result) throws SQLException {
        String value = result.getString(column);
        if (result.wasNull()) {
            value = null;
        }
        return value;
    }

    /**
     * Extracts a nullable integer from the given {@code ResultSet}.
     *
     * @param column The column name to extract from.
     * @param result The source of the data.
     * @return The extracted {@code String} value, or {@code null} if that value was {@code NULL} in the database.
     * @throws SQLException If the access threw this exception.
     */
    static Integer extractNullableInteger(String column, ResultSet result) throws SQLException {
        Integer value = result.getInt(column);
        if (result.wasNull()) {
            value = null;
        }
        return value;
    }

    /**
     * Extracts a nullable long from the given {@code ResultSet}.
     *
     * @param column The column name to extract from.
     * @param result The source of the data.
     * @return The extracted {@code String} value, or {@code null} if that value was {@code NULL} in the database.
     * @throws SQLException If the access threw this exception.
     */
    static Long extractNullableLong(String column, ResultSet result) throws SQLException {
        Long value = result.getLong(column);
        if (result.wasNull()) {
            value = null;
        }
        return value;
    }

}
