/*
 * QCRI, NADEEF LICENSE
 * NADEEF is an extensible, generalized and easy-to-deploy data cleaning platform built at QCRI.
 * NADEEF means "Clean" in Arabic
 *
 * Copyright (c) 2011-2013, Qatar Foundation for Education, Science and Community Development (on
 * behalf of Qatar Computing Research Institute) having its principle place of business in Doha,
 * Qatar with the registered address P.O box 5825 Doha, Qatar (hereinafter referred to as "QCRI")
 *
 * NADEEF has patent pending nevertheless the following is granted.
 * NADEEF is released under the terms of the MIT License, (http://opensource.org/licenses/MIT).
 */

package qa.qcri.nadeef.core.util.sql;

import qa.qcri.nadeef.core.datamodel.NadeefConfiguration;
import qa.qcri.nadeef.tools.DBConfig;
import qa.qcri.nadeef.tools.Tracer;
import qa.qcri.nadeef.tools.sql.SQLDialect;

import java.sql.Connection;
import java.sql.Statement;

/**
 * NADEEF database installation utility class.
 */
public final class DBInstaller {
    private static Tracer tracer = Tracer.getTracer(DBInstaller.class);

    /**
     * Install NADEEF on the target database.
     * @param dbConfig Connection pool dbconfig.
     */
    public static void install(DBConfig dbConfig) throws Exception {
        Connection conn = null;
        Statement stat = null;
        SQLDialect dialect = dbConfig.getDialect();
        SQLDialectBase dialectManager =
            SQLDialectFactory.getDialectManagerInstance(dialect);
        String violationTableName = NadeefConfiguration.getViolationTableName();
        String repairTableName = NadeefConfiguration.getRepairTableName();
        String auditTableName = NadeefConfiguration.getAuditTableName();

        // TODO: make tables BNCF
        try {
            conn = DBConnectionPool.createConnection(dbConfig);
            stat = conn.createStatement();

            if (DBMetaDataTool.isTableExist(dbConfig, violationTableName)) {
                tracer.info(
                    "Violation is already installed on the database, skip installing."
                );
            } else {
                stat.execute(dialectManager.createViolationTable(violationTableName));
            }

            if (DBMetaDataTool.isTableExist(dbConfig, repairTableName)) {
                tracer.info(
                    "Repair is already installed on the database, skip installing."
                );
            } else {
                stat.execute(dialectManager.createRepairTable(repairTableName));
            }

            if (DBMetaDataTool.isTableExist(dbConfig, auditTableName)) {
                tracer.info(
                    "Audit is already installed on the database, skip installing."
                );
            } else {
                stat.execute(dialectManager.createAuditTable(auditTableName));
            }

            conn.commit();
        } catch (Exception ex) {
            tracer.err("Exception during installing tables.", ex);
            throw ex;
        } finally {
            if (stat != null) {
                stat.close();
            }

            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * Uninstall NADEEF on the target database.
     * @param dbConfig DBConfig.
     */
    public static void uninstall(DBConfig dbConfig) throws Exception {
        Connection conn = null;
        Statement stat = null;
        SQLDialect dialect = dbConfig.getDialect();
        SQLDialectBase dialectManager =
            SQLDialectFactory.getDialectManagerInstance(dialect);

        // TODO: make tables BNCF
        try {
            String violationTableName = NadeefConfiguration.getViolationTableName();
            String repairTableName = NadeefConfiguration.getRepairTableName();
            String auditTableName = NadeefConfiguration.getAuditTableName();

            conn = DBConnectionPool.createConnection(dbConfig);
            stat = conn.createStatement();

            if (DBMetaDataTool.isTableExist(dbConfig, violationTableName)) {
                stat.execute(dialectManager.dropTable(violationTableName));
            }

            if (DBMetaDataTool.isTableExist(dbConfig, repairTableName)) {
                stat.execute(dialectManager.dropTable(repairTableName));
            }

            if (DBMetaDataTool.isTableExist(dbConfig, auditTableName)) {
                stat.execute(dialectManager.dropTable(auditTableName));
            }

            conn.commit();
        } catch (Exception ex) {
            tracer.err("SQLException during installing tables.", ex);
            throw ex;
        } finally {
            if (stat != null) {
                stat.close();
            }

            if (conn != null) {
                conn.close();
            }
        }
    }
}
