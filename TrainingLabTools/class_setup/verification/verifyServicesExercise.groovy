import de.hybris.platform.basecommerce.model.site.BaseSiteModel
import de.hybris.platform.commerceservices.consent.CommerceConsentService
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService
import de.hybris.platform.commerceservices.model.consent.ConsentModel
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel
import de.hybris.platform.core.model.user.CustomerModel

def script = new GroovyScriptEngine( '.' ).with {
	loadScriptByName( '../../Logger.groovy' ) //for this to work, Logger.groovy should've already been put inside platform directory
}
this.metaClass.mixin script

//NOTE: If we run scripts from ant there is no user session information
userService.setCurrentUser(userService.getAdminUser())

// setting current site, otherwise RegistrationEventListener.getSiteChannelForEvent(RegisterEvent) fails
baseSiteService.setCurrentBaseSite("electronics",false)

def customer=modelService.create(CustomerModel.class)
customer.uid="test@sap.com"
customer.name="test"
customerAccountService.register(customer, "1234")

def baseSite=baseSiteDao.findBaseSiteByUID("electronics")
def consentTemplates = commerceConsentService.getConsentTemplates(baseSite)
def totalConsents=consentTemplates.size()
for(consentTemplate in consentTemplates){
    commerceConsentService.giveConsent(customer, consentTemplate)
}

//test the defaultCustomizedCustomerAccountService and getAllActiveConsents method
try{
    def allActiveConsents=defaultCustomizedCustomerAccountService.getAllActiveConsents(customer,baseSite)	
    addLog('defaultCustomizedCustomerAccountService and method getAllActiveConsents(CustomerModel, BaseSiteModel)   .....OK')
  if(totalConsents==allActiveConsents.size()){
   	addLog('logic for method getAllActiveConsents(CustomerModel, BaseSiteModel)   .....OK')
  }else{
    addError('logic for method getAllActiveConsents(CustomerModel, BaseSiteModel)   .....NOK')

  }
  
}catch(groovy.lang.MissingPropertyException e){
    addError('defaultCustomizedCustomerAccountService is not defined!')
}catch(groovy.lang.MissingMethodException e){
    addError('Method getAllActiveConsents(CustomerModel, BaseSiteModel) doesnt exist! Check if you defined defaultCustomizedCustomerAccountService properly!')
}

//test the closeAccount method

try{
	baseSiteService.setCurrentBaseSite("electronics",false)
	userService.setCurrentUser(customer)
  defaultCustomizedCustomerAccountService.closeAccount(customer)	
	def allActiveConsents=defaultCustomizedCustomerAccountService.getAllActiveConsents(customer,baseSite)

    //NOTE: Set the current user back to admin, otherwise Hac is broken because An item stored in a session is no longer valid.
    userService.setCurrentUser(userService.getAdminUser())

	if(allActiveConsents.size()==0){
		addLog('closeAccount(CustomerModel)   .....OK')
  }else{
	  addError('logic for method closeAccount(CustomerModel)   .....NOK')

  }
  
}catch(groovy.lang.MissingMethodException e){
    addError('Method closeAccount(CustomerModel) doesnt exist! Check if you defined defaultCustomizedCustomerAccountService properly!')
}

printOutputLog()
