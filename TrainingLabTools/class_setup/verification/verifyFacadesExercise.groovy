import de.hybris.platform.servicelayer.dto.converter.Converter

import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.commercefacades.product.data.StockData

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;


def script = new GroovyScriptEngine( '.' ).with {
    loadScriptByName( '../../Logger.groovy' ) //for this to work, Logger.groovy should've already been put inside platform directory
}
this.metaClass.mixin script

//NOTE: We must set the anonymous user as our session user because HAC and ant don't have a realistic session user for this scenario
userService.setCurrentUser(userService.getAnonymousUser())

//We need to set the current base site because when running the script from HAC or ant, the current base site is null
baseSiteService.setCurrentBaseSite("electronics", true);

def checkPopulatorExists(Converter theConverter, String thePopulatorName){
	def exists = false
	theConverter.getProperties().get("populators").each{
		populator -> exists = (populator.toString().contains(thePopulatorName) || exists)}
	if (exists){
		addLog("OK: ${thePopulatorName} is added to the proper converter!")
	}else{
		addError("NOT OK: ${thePopulatorName} is not added to the proper converter!")
	}
}

def checkPopulatorForAttribute(theModel, theDTO, Converter theConverter, String theAttr, theVal){
	theConverter.convert(theModel, theDTO)
	def dtoVal = theDTO.invokeMethod("get"+theAttr, null)
	
	if (dtoVal.equals(theVal)){
		addLog("OK: ${theConverter.getClass().getSimpleName()} populates the attribute ${theAttr} in ${theDTO.getClass().getSimpleName()}!")
	} else{
		addError("NOT OK: ${theConverter.getClass().getSimpleName()} problem populating attribute ${theAttr} in ${theDTO.getClass().getSimpleName()} EXPECTING:${theVal}  ACTUAL:${dtoVal} ")
	}
}


// All of this code is written as if catalogService.getCatalogForId(catalogId) returns null if target not found.
// Actually, it throws an exception.  This wrapper fixes that
def getCatalogForId(catalogId) {
    try {
        return catalogService.getCatalogForId(catalogId)
    } catch (UnknownIdentifierException e) {
        return null;
    }
}


// All of this code is written as if productService.getProductForCode(code) returns null if target not found.
// Actually, it throws an exception.  This wrapper fixes that
def getProductForCode(catalogVersion, prodCode) {
    try {
        return productService.getProductForCode(catalogVersion, prodCode)
    } catch (UnknownIdentifierException e) {
        return null;
    }
}


//

checkPopulatorExists(stockConverter,"my.commerce.trainingfacades.populators.DefaultProductRestockEstimatePopulator")

def catModel = getCatalogForId('electronicsProductCatalog')
theModel = (ProductModel) getProductForCode(catModel.getActiveCatalogVersion(), "1320808")
def theDTO = StockData.class.newInstance()

try {
    checkPopulatorForAttribute(theModel, theDTO, stockConverter, "EstimatedRestockDays", 2)
} catch(Exception e) {
    addError(e)
}

printOutputLog()