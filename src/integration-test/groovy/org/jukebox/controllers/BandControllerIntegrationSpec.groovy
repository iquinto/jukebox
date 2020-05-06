package org.jukebox.controllers

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import grails.testing.spock.OnceBefore
import org.jukebox.Band
import org.jukebox.BandController
import org.jukebox.BandService
import org.jukebox.utils.BaseContainerSpecification
import org.jukebox.utils.BaseRequestSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.testcontainers.spock.Testcontainers
import spock.lang.IgnoreIf
import spock.lang.Shared


@IgnoreIf({ System.getProperty('geb.env') })
@Integration
@Rollback
@Testcontainers
class BandControllerIntegrationSpec extends BaseContainerSpecification implements BaseRequestSpecification{

    String controllerName = "bandController"

    @Autowired
    BandController controller

    @Autowired
    BandService bandService

    @Shared
    List<Band> bandList = []

    @OnceBefore
    def populateData() {
        bandList << bandService.save(new Band(name: "Bon Jovi" , yearFormed: "1998", yearDissolution: "2018", style: "Rock", origin: "US"))
        bandList << bandService.save(new Band( name: "Joan and Company",  yearFormed: "2019",  style: "Pop",  origin: "Spain"))
    }

    void "test get action"() {
        when:
        controller.index()

        then:
        controller.modelAndView.model.bandCount ==  Band.findAll().size()
    }

}
