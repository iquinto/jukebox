package org.jukebox.services

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import grails.testing.spock.OnceBefore
import org.jukebox.Band
import org.jukebox.BandController
import org.jukebox.BandService
import org.jukebox.utils.BaseContainerSpecification
import org.jukebox.utils.BaseRequestSpecification
import org.springframework.beans.factory.annotation.Autowired

@Integration
@Rollback
class BandControllerIntegrationSpec extends BaseContainerSpecification implements BaseRequestSpecification{

    String controllerName = "bandController"

    @Autowired
    BandController controller

    @Autowired
    BandService bandService

    @OnceBefore
    def populateData() {
        bandService.save(new Band( name: "prova",  yearFormed: "2019",  yearDissolution: "2019",  style: "Pop",  origin: "Usa"))
    }

    void "test get"() {
        when:
        controller.index()

        then:
        bandService.count() == 1
    }


}
