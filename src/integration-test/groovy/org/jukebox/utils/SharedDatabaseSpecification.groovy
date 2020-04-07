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

abstract class SharedDatabaseSpecification extends Specification{
    private static final Set<HikariDataSource> datasourcesForCleanup = new HashSet<>()
    private final static String DB_QUERY = new File('src/integration-test/groovy/org/jukebox//utils/base.sql').text

    protected static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer()
            .withDatabaseName("dipta")
            .withUsername("app")
            .withPassword("pass")

    def setupSpec() {
        startPostgresIfNeeded()
        ['grailsApplication.config.environments.test.dataSource.url'     : POSTGRES.getJdbcUrl(),
         'grailsApplication.config.environments.test.dataSource.username': POSTGRES.getUsername(),
         'grailsApplication.config.environments.test.dataSource.password': POSTGRES.getPassword(),
         'grailsApplication.config.environments.test.dataSource.driverClassName': POSTGRES.getDriverClassName()
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