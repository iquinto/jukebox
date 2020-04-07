import grails.util.Environment

// Place your Spring DSL code here
beans = {

    if (Environment.TEST){
        application.config.grails.plugin.springsecurity.cas.active
    }


            /*
            if (application.config.grails.plugin.springsecurity.cas.active) {
                ma3UserDetailsService(MA3AuthorizationProvider) {
                    ma3EndPointURL = "${application.config.casMa3Authorization.ma3Url}/rest/"
                    ma3ApplicationID = "${application.config.casMa3Authorization.appID}"
                    ma3SecCredentialsUsername = "${application.config.casMa3Authorization.ma3SecCredUsername}"
                    ma3SecCredentialsPassword = "${application.config.casMa3Authorization.ma3SecCredPassword}"

                    // Si volem carregar la informació de la unitat organitzativa per la que treballa el
                    // principal logat, posem aquesta variable a ?true?. Si no ho necessitem, no cal posar-la o bé deixar-la en ?false? (es mes eficient)
                    bddtHierarchyLoad = "true"

                    // Si volem carregar la informacio dels ambits de visibilitat del principal logat,
                    // posem aquesta variable a ?true?. Si no ho necessitem, no cal posar-la o be deixar-la en false (es mes eficient)
                    ambitsLoad = "true"

                    //carregar el perfil de l'usuari al request de forma transparent
                    perfilLoad = "true"
                }

                casMA3AuthenticationProvider(CasAuthenticationProvider) {
                    authenticationUserDetailsService = ref('ma3UserDetailsService')
                    serviceProperties = ref('casServiceProperties')
                    ticketValidator = new Cas20ServiceTicketValidator("${application.config.casMa3Authorization.casUrl}")
                    // Set drift between CAS server and application client server to be as much as 30 seconds
                    key = "mfi"
                }
            }
*/


}
