/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.camunda.bpm.engine.rest.dto.repository.WordpressDto;

/**
 *
 * @author TramSunny
 */
public class WordpressConnection {

    public static void addPreparedStatement(PreparedStatement sm, int i, Object obj) throws SQLException {
        if (obj == null) {
            sm.setObject(i, null, java.sql.Types.NULL);
        } else if (obj instanceof Long) {
            sm.setLong(i, (Long) obj);
        } else if (obj instanceof Integer) {
            sm.setInt(i, (Integer) obj);
        } else if (obj instanceof Date) {
            sm.setObject(i, obj, java.sql.Types.DATE);
        } else if (obj instanceof Double) {
            sm.setDouble(i, (Double) obj);
        } else if (obj instanceof String) {
            if (obj.equals("")) {
                sm.setObject(i, null, java.sql.Types.NULL);
            } else {
                sm.setNString(i, (String) obj);
            }
        }
    }
    public static HashMap<WordpressDto, WordpressConnection> listConnection = new HashMap<>();

    public static WordpressConnection getWordpressConnection(WordpressDto wordpressDto) {
        if (!listConnection.containsKey(wordpressDto)) {
            listConnection.put(wordpressDto, new WordpressConnection(wordpressDto));
        }
        return listConnection.get(wordpressDto);
    }

    public WordpressDto wordpressDto;
    public Connection conn = null;

    public WordpressConnection(WordpressDto wordpress) {
        wordpressDto = wordpress;
    }

    public Connection getMySQLConnection() throws SQLException {
        return getMySQLConnection(wordpressDto.dbhost, wordpressDto.dbname, wordpressDto.uname, wordpressDto.pwd);
    }

    public Connection getMySQLConnection(String hostName, String dbName, String userName, String password) throws SQLException {
        if (conn != null) {
            return conn;
        }
        try {
            String driverName = "org.gjt.mm.mysql.Driver";
            Class.forName(driverName);
            String url = "jdbc:mysql://" + hostName + "/" + dbName + "?useSSL=false"; // a JDBC url
            conn = DriverManager.getConnection(url, userName, password);
            return conn;

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CamundaConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int createIfExistDatabase() throws SQLException {
        String query = "CREATE DATABASE IF NOT EXISTS `" + wordpressDto.dbname + "`";
        Connection conCreate = getMySQLConnection(wordpressDto.dbhost, "", wordpressDto.uname, wordpressDto.pwd);
        Statement statement = conCreate.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return statement.executeUpdate(query);
    }

    public ResultSet executeQuery(String sql) {
        return executeQuery(sql, false);
    }

    public ResultSet executeQuery(String sql, boolean muiltRow) {
        ResultSet rs = null;
        try {
            Connection con = getMySQLConnection();
            Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery(sql);
            if (!muiltRow) {
                rs.next();

            }
        } catch (Exception ex) {
            Logger.getLogger(CamundaConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public ResultSet executeQuery(String sql, List<Object> thamso) throws SQLException {
        return executeQuery(sql, thamso, false);
    }

    public ResultSet executeQuery(String sql, List<Object> thamso, boolean muiltRow) throws SQLException {
        Connection con = getMySQLConnection();
        PreparedStatement pstm = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        int i = 1;

        for (Object ts : thamso) {
            addPreparedStatement(pstm, i++, ts);
        }
        ResultSet rs = pstm.executeQuery();
        if (!muiltRow) {
            rs.next();
        }
        return rs;
    }

    public Long executeQueryAsLong(String sql, String col) {
        return executeQueryAsLong(sql, null, col);
    }

    public Long executeQueryAsLong(String sql, Object thamso, String col) {
        try {
            ResultSet rs = executeQuery(sql, thamso, false);
            if (rs == null || rs.getRow() == 0) {
                return null;
            }
            return rs.getLong(col);

        } catch (SQLException ex) {
            Logger.getLogger(CamundaConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String executeQueryAsString(String sql, Object thamso, String col) throws SQLException {
        ResultSet rs = executeQuery(sql, thamso, false);
        return rs.getString(col);
    }

    public ResultSet executeQuery(String sql, Object thamso) throws SQLException {
        return executeQuery(sql, thamso, false);
    }

    public ResultSet executeQuery(String sql, Object thamso, boolean muiltRow) throws SQLException {
        if (thamso == null) {
            return executeQuery(sql, muiltRow);
        }
        Connection con = getMySQLConnection();
        PreparedStatement pstm = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        addPreparedStatement(pstm, 1, thamso);
        ResultSet rs = pstm.executeQuery();
        if (!muiltRow) {
            rs.next();
        }
        return rs;
    }

    public int executeUpdate(String sql, List<Object> thamso) throws SQLException {
        Connection con = getMySQLConnection();
        PreparedStatement pstm = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        int i = 1;
        for (Object ts : thamso) {
            addPreparedStatement(pstm, i++, ts);
        }
        return pstm.executeUpdate();
    }

    public int executeUpdate(String sql, Object thamso) throws SQLException {
        Connection con = getMySQLConnection();
        PreparedStatement pstm = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        addPreparedStatement(pstm, 1, thamso);
        return pstm.executeUpdate();
    }

    public int executeUpdate(String sql) throws SQLException {
        Connection con = getMySQLConnection();
        Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return statement.executeUpdate(sql);
    }

}
