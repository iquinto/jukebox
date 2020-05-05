package org.jukebox

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

import java.text.SimpleDateFormat

class BandSpec extends Specification implements DomainUnitTest<Band> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        when:
        new Band().save(failOnError: true)

        then:
        Band.count() == 1

    }
}
