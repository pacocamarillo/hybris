import de.hybris.bootstrap.config.ConfigUtil
import de.hybris.platform.core.Registry
import de.hybris.platform.jalo.extension.ExtensionInfo


def script = new GroovyScriptEngine( '.' ).with {
    loadScriptByName( '../../Logger.groovy' ) //for this to work, Logger.groovy should've already been put inside platform directory
}
this.metaClass.mixin script

//NOTE: If we run scripts from ant there is no user session information
userService.setCurrentUser(userService.getAdminUser())

def checkExtensionExists(String extName){
	final List<ExtensionInfo> allExtensions = ConfigUtil.getPlatformConfig(Registry.class).getExtensionInfosInBuildOrder()
	
	def ext = allExtensions.find {extension -> extension.getName().equals(extName)}
	
	if (ext == null){
		addError("NOT OK: There is no extension with the name ${extName} loaded in the system. " +
				"Make sure that you have run the setup to create an extension called ${extName} and also have put it inside localextension.xml")
	} else{
		addLog("OK: The new extension called ${extName} is loaded into the system.")
	}
}


/********Check for trainingwcms*********************************/

def extName = "trainingwcms"
checkExtensionExists(extName)

/********Check itemtype AnnotatedResponsiveBannerComponent********/

def componentName = "AnnotatedResponsiveBannerComponent"
def componentPackage = "my.commerce.trainingwcms.model"
try{
	Class.forName(componentPackage+"."+componentName+"Model")
	addLog("OK: ${componentName} exists!")
} catch(ClassNotFoundException ex){
	addError("NOT OK: ${componentName} does not exist. "+
		"Make sure that you defined an item type for the component inside *items.xml")
}


/**check AnnotatedResponsiveBannerComponent in Section1 homepage**/

def contentCatOnline = catalogVersionService.getCatalogVersion("electronics-spaContentCatalog","Online")
def homepage = cmsPageDao.findPagesByIdAndCatalogVersion("homepage",contentCatOnline).get(0)
def componentPlacedCorrectly = false
def theAnnotatedResponsiveBannerComponent = null
def contentSlotsForPage = homepage.getContentSlots()
contentSlotsForPage.each{
	cs4page -> 
	if (cs4page.getPosition().equals("Section1")){
		cs4page.getContentSlot().getCmsComponents().each{
			cmsComponent ->
			if (cmsComponent.itemtype.equals("AnnotatedResponsiveBannerComponent")){
				componentPlacedCorrectly = true
              theAnnotatedResponsiveBannerComponent = cmsComponent
			}
		}
	}
}
if (componentPlacedCorrectly){
	addLog("OK: AnnotatedResponsiveBannerComponent is placed in the correct position on the homepage!")
  	restrictionList = theAnnotatedResponsiveBannerComponent.getRestrictions()
  if (restrictionList.isEmpty()){
  	addLog("NOT OK: AnnotatedResponsiveBannerComponent doesnot have any restriction assign")
  }
  else{
    if (restrictionList.get(0).getName().equals("Logged in User")){
    	addLog("OK: AnnotatedResponsiveBannerComponent has the Logged in User restriction assigned")
    }
    else{
      addLog("NOT OK: please make sure to assign the Logged in User restriction to the AnnotatedResponsiveBannerComponent")
    }
  }
}else{
	addError("NOT OK: AnnotatedResponsiveBannerComponent is not placed in the correct position on the homepage!")
}


/********Print the Log******************************************/

printOutputLog()