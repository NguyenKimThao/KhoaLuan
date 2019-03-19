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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.camunda.bpm.engine.config.ConfigBusinessWebappCreator;
import org.camunda.bpm.engine.config.ConfigDatabase;

public class CamundaConnection {

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

    public static Connection conn = null;
    public static boolean isInit = false;

    public static Connection getMySQLConnection() throws SQLException {
        if (conn != null) {
            return conn;
        }
        try {

            if (!isInit) {
                return CreateIfExitTableCamunda();
            }
            conn = ((ConfigDatabase) ConfigBusinessWebappCreator.getConfigWebapp("database")).getConnection();
            return conn;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CamundaConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

//        String hostName = "localhost";
//        String dbName = "camunda-extend";
//        String userName = "root";
//        String password = "admin";
//        return getMySQLConnection(hostName, dbName, userName, password);
    }

    public static Connection getMySQLConnection(String hostName, String dbName, String userName, String password) throws SQLException {
        if (conn != null) {
            return conn;
        }
        try {
            String driverName = "org.gjt.mm.mysql.Driver";
            Class.forName(driverName);
            String url = "jdbc:mysql://" + hostName + ":3306/" + dbName + "?useSSL=false"; // a JDBC url
            conn = DriverManager.getConnection(url, userName, password);
            return conn;

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CamundaConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static ResultSet executeQuery(String sql) {
        return executeQuery(sql, false);
    }

    public static ResultSet executeQuery(String sql, boolean muiltRow) {
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

    public static ResultSet executeQuery(String sql, List<Object> thamso) throws SQLException {
        return executeQuery(sql, thamso, false);
    }

    public static ResultSet executeQuery(String sql, List<Object> thamso, boolean muiltRow) throws SQLException {
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

    public static Long executeQueryAsLong(String sql, Object thamso, String col) {
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

    public static String executeQueryAsString(String sql, Object thamso, String col) throws SQLException {
        ResultSet rs = executeQuery(sql, thamso, false);
        return rs.getString(col);
    }

    public static ResultSet executeQuery(String sql, Object thamso) throws SQLException {
        return executeQuery(sql, thamso, false);
    }

    public static ResultSet executeQuery(String sql, Object thamso, boolean muiltRow) throws SQLException {
        Connection con = getMySQLConnection();
        PreparedStatement pstm = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        addPreparedStatement(pstm, 1, thamso);
        ResultSet rs = pstm.executeQuery();
        if (!muiltRow) {
            rs.next();
        }
        return rs;
    }

    public static int executeUpdate(String sql, List<Object> thamso) throws SQLException {
        Connection con = getMySQLConnection();
        PreparedStatement pstm = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        int i = 1;
        for (Object ts : thamso) {
            addPreparedStatement(pstm, i++, ts);
        }
        return pstm.executeUpdate();
    }

    public static int executeUpdate(String sql, Object thamso) throws SQLException {
        Connection con = getMySQLConnection();
        PreparedStatement pstm = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        addPreparedStatement(pstm, 1, thamso);
        return pstm.executeUpdate();
    }

    private static Connection CreateIfExitTableCamunda() throws SQLException, ClassNotFoundException {
        Connection con = ((ConfigDatabase) ConfigBusinessWebappCreator.getConfigWebapp("database")).getConnection();
        String sqlwordpress = "CREATE TABLE IF NOT EXISTS `wordpress` (\n"
                + "  `id` VARCHAR(100) NOT NULL,\n"
                + "  `name` VARCHAR(100) NULL DEFAULT NULL,\n"
                + "  `wphost` VARCHAR(100) NULL DEFAULT NULL,\n"
                + "  `directory` VARCHAR(500) NULL DEFAULT NULL,\n"
                + "  `dbhost` VARCHAR(100) NULL DEFAULT NULL,\n"
                + "  `dbname` VARCHAR(100) NULL DEFAULT NULL,\n"
                + "  `uname` VARCHAR(100) NULL DEFAULT NULL,\n"
                + "  `pwd` VARCHAR(100) NULL DEFAULT NULL,\n"
                + "  `prefix` VARCHAR(45) NULL DEFAULT 'wp_',\n"
                + "  PRIMARY KEY (`id`));";
        String sqlworkspace = "CREATE TABLE IF NOT EXISTS `workspace` (\n"
                + "  `workspaceID` VARCHAR(100) NOT NULL,\n"
                + "  `workspaceName` VARCHAR(100) CHARACTER SET 'utf8' NULL DEFAULT NULL,\n"
                + "  `username` VARCHAR(100) NULL DEFAULT NULL,\n"
                + "  PRIMARY KEY (`workspaceID`));";
        String sqlworkspace_deployment = "CREATE TABLE IF NOT EXISTS `workspace_deployment` (\n"
                + "  `workspaceID` VARCHAR(100) NOT NULL,\n"
                + "  `deploymentID` VARCHAR(100) NOT NULL,\n"
                + "  PRIMARY KEY (`workspaceID`, `deploymentID`));";
        String sqlworkspace_process = "CREATE TABLE IF NOT EXISTS `workspace_process` (\n"
                + "  `workspaceID` VARCHAR(100) NOT NULL,\n"
                + "  `processID` VARCHAR(100) NOT NULL,\n"
                + "  PRIMARY KEY (`workspaceID`, `processID`));";
        String sqlworkspace_wordpress = "CREATE TABLE IF NOT EXISTS `workspace_wordpress` (\n"
                + "  `workspaceID` VARCHAR(45) NOT NULL,\n"
                + "  `wordpressID` VARCHAR(45) NOT NULL,\n"
                + "  PRIMARY KEY (`workspaceID`, `wordpressID`));";
        con.createStatement().executeUpdate(sqlworkspace);
        con.createStatement().executeUpdate(sqlwordpress);
        con.createStatement().executeUpdate(sqlworkspace_deployment);
        con.createStatement().executeUpdate(sqlworkspace_process);
        con.createStatement().executeUpdate(sqlworkspace_wordpress);
        isInit = true;
        conn = con;
        return conn;
    }
}
