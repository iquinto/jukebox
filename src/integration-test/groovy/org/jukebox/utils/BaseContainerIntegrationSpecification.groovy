package org.jukebox.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import grails.util.GrailsWebMockUtil
import groovy.transform.CompileStatic
import org.grails.plugins.testing.GrailsMockHttpServletRequest
import org.grails.plugins.testing.GrailsMockHttpServletResponse
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.request.RequestContextHolder
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Ignore
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

/**
 * combination of BaseRequestSpecification and  BaseContainerSpecification for IT testing
 */

@CompileStatic
@Testcontainers
abstract class BaseContainerIntegrationSpecification extends Specification{

    private static final Set<HikariDataSource> datasourcesForCleanup = new HashSet<>()
    private final static String DB_QUERY = new File('src/integration-test/groovy/org/jukebox//utils/base.sql').text

    @Autowired
    WebApplicationContext ctx


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

    void setup() {
        MockHttpServletRequest request = new GrailsMockHttpServletRequest(ctx.servletContext)
        MockHttpServletResponse response = new GrailsMockHttpServletResponse()
        GrailsWebMockUtil.bindMockWebRequest(ctx, request, response)
        currentRequestAttributes.setControllerName(controllerName)
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


    @Ignore
    abstract String getControllerName()

    @Ignore
    protected GrailsWebRequest getCurrentRequestAttributes() {
        return (GrailsWebRequest) RequestContextHolder.currentRequestAttributes()
    }

    void cleanup() {
        RequestContextHolder.resetRequestAttributes()
    }

    @Ignore
    def autowire(Class clazz) {
        def bean = clazz.newInstance()
        ctx.autowireCapableBeanFactory.autowireBeanProperties(bean, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false)
        bean
    }


    @Ignore
    def autowire(def bean) {
        ctx.autowireCapableBeanFactory.autowireBeanProperties(bean, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false)
        bean
    }



}