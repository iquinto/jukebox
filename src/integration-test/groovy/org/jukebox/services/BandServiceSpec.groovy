package org.jukebox.services

import grails.core.GrailsApplication
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.jukebox.Band
import org.jukebox.BandService
import org.jukebox.utils.BaseContainerSpecification
import org.hibernate.SessionFactory
import spock.lang.IgnoreIf

import java.sql.ResultSet

@IgnoreIf({ System.getProperty('geb.env') })
@Integration
@Rollback
class BandServiceSpec extends BaseContainerSpecification {

    BandService bandService
    GrailsApplication grailsApplication



    def setup(){
    }

    void "test count created object"() {
        when:
        bandService.save(new Band( name: "prova",  yearFormed: "2019",  yearDissolution: "2019",  style: "Pop",  origin: "Usa"))
        assert bandService.count() == 1

        println "====> " + grailsApplication.config.environments.test.dataSource.url
        println "====> " + grailsApplication.config.environments.test.dataSource.username
        println "====> " + grailsApplication.config.environments.test.dataSource.password
        println "====> " +grailsApplication.config.environments.test.dataSource.driverClassName

        executeQuery("INSERT INTO BAND VALUES(1,'Metallica',1981,NULL,'Heavy','US');")

        then:
        bandService.count() == 2
    }

    void "test if db is not empty"() {
        when:
        ResultSet resultSet = performQueryResult("SELECT * FROM BAND WHERE id = '1'")
        String resultSetInt = resultSet.getString(2)

        then:
        resultSetInt == "Metallica"
    }


/*

    void "test get"() {
        setupData()

        expect:
        bandService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Band> bandList = bandService.list(max: 2, offset: 2)

        then:
        bandList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        bandService.count() == 5
    }

    void "test delete"() {
        Long bandId = setupData()

        expect:
        bandService.count() == 5

        when:
        bandService.delete(bandId)
        sessionFactory.currentSession.flush()

        then:
        bandService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Band band = new Band()
        bandService.save(band)

        then:
        band.id != null
    }


    */

}
