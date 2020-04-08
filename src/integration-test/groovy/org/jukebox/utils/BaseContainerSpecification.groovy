package org.jukebox.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Specification
import javax.sql.DataSource
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
/*
   iquinto: this class inject testcoontainers DB for IT
 */
abstract class BaseContainerSpecification extends Specification{
    private static final Set<HikariDataSource> datasourcesForCleanup = new HashSet<>()
    private final static String DB_QUERY = new File('src/integration-test/groovy/org/jukebox//utils/base.sql').text

    protected static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer()
            .withDatabaseName("dipta")
            .withUsername("app")
            .withPassword("pass")

    def setupSpec() {
        startPostgresIfNeeded()
        ['JDBC_CONNECTION_STRING' : POSTGRES.getJdbcUrl(),
         'JDBC_CONNECTION_USER': POSTGRES.getUsername(),
         'JDBC_CONNECTION_PASSWORD': POSTGRES.getPassword(),
         'JDBC_CONNECTION_DRIVER': POSTGRES.getDriverClassName()
        ].each { k, v ->
            System.setProperty(k, v)
        }
        migrateDatabase()
    }

    def cleanupSpec() {
        if (POSTGRES.isRunning()) {
            println "[BASE-INTEGRATION-TEST] - Stopping Postgres..."
            POSTGRES.stop()
        }
        datasourcesForCleanup.each { it.close() }
    }

    private static void startPostgresIfNeeded() {
        if (!POSTGRES.isRunning()) {
            println "[BASE-INTEGRATION-TEST] - Postgres is not started. Running..."
            POSTGRES.start()
        }
    }

    private void migrateDatabase() throws SQLException {
        println"[BASE-INTEGRATION-TEST] - Migrating DataBase..."
        DataSource ds = getDataSource(POSTGRES)
        Statement statement = ds.getConnection().createStatement()
        statement.execute(DB_QUERY)
    }

    void executeQuery(String query) throws SQLException {
        DataSource ds = getDataSource(POSTGRES)
        Statement statement = ds.getConnection().createStatement()
        statement.execute(query)
    }

    ResultSet performQueryResult(String sql) throws SQLException {
        DataSource ds = getDataSource(POSTGRES);
        Statement statement = ds.getConnection().createStatement()
        statement.execute(sql)
        ResultSet resultSet = statement.getResultSet()
        resultSet.next()
        return resultSet
    }

    DataSource getDataSource(JdbcDatabaseContainer container) {
        HikariConfig hikariConfig = new HikariConfig()
        hikariConfig.setJdbcUrl(container.getJdbcUrl())
        hikariConfig.setUsername(container.getUsername())
        hikariConfig.setPassword(container.getPassword())
        hikariConfig.setDriverClassName(container.getDriverClassName())
        final HikariDataSource dataSource = new HikariDataSource(hikariConfig)
        datasourcesForCleanup.add(dataSource)
        return dataSource
    }

}