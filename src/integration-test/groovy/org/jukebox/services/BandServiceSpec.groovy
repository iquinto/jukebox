package org.jukebox.services

import grails.core.GrailsApplication
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.jukebox.Band
import org.jukebox.BandService
import org.jukebox.utils.BaseContainerSpecification
import org.hibernate.SessionFactory
import spock.lang.IgnoreIf


@IgnoreIf({ System.getProperty('geb.env') })
@Integration
@Rollback
class BandServiceSpec extends BaseContainerSpecification {

    BandService bandService
    GrailsApplication grailsApplication
    SessionFactory sessionFactory

    void "test count objects in DB"() {
        when:
        List<Band> bandList = Band.findAll()

        then:
        println "Names : ${bandList.name}"
        bandList.size() > 0
    }

    void "test get object in DB"() {
        when:
        Band band = bandService.get(1)

        then:
        println "Name : ${band.name}"
        band != null
    }

    void "test create and modify an object"() {
        given:
        String str = "Bon Jovi"

        when:
        bandService.save(new Band(name: str , yearFormed: "1998",
                yearDissolution: "2018", style: "Rock", origin: "US"))
        sessionFactory.currentSession.flush()

        then:
        bandService.count() == old (Band.count()) + 1

        when:
        def b = Band.findByName(str)
        b.name = "HOLA"
        bandService.save(b)
        sessionFactory.currentSession.flush()

        then:
        println "Name : ${b.name}"
        b.name == "HOLA"
    }

    void "test delete"() {
        given:
        println "Before delete names : ${Band.findAll().name}"
        Long bandId = bandService.get(1).id

        when:
        bandService.delete(bandId)
        sessionFactory.currentSession.flush()

        then:
        println "After delete names : ${Band.findAll().name}"
        bandService.count() == old(Band.count()) - 1
    }


}
