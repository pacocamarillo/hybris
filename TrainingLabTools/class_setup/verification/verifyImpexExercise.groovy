import de.hybris.platform.catalog.model.CatalogModel;
import java.util.Locale;
import java.util.List;
import java.util.Collection;

import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.security.PrincipalModel;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;



def script = new GroovyScriptEngine( '.' ).with {
  loadScriptByName( '../../Logger.groovy' ) //for this to work, Logger.groovy should've already been put inside platform directory
}
this.metaClass.mixin script


//This "wrapper" converts categoryService.getCategoryForCode( catVersion, catCode ) exception into "return null" if target not found
def getCategoryForCode( catVersion, catCode ) {

    try {
        return categoryService.getCategoryForCode( catVersion, catCode )
    } catch(UnknownIdentifierException e) {
        return null;
    }
}


//This "wrapper" converts productService.getProductForCode( catVersion, prodCode ) exception into "return null" if target not found
def getProductForCode( catVersion, prodCode ) {

    try {
        return productService.getProductForCode( catVersion, prodCode )
    } catch(UnknownIdentifierException e) {
        return null;
    }
}

  

//NOTE: If we run scripts from ant there is no user session information
userService.setCurrentUser(userService.getAdminUser())

CatalogModel cm=catalogService.getCatalogForId('electronicsProductCatalog');
if(cm==null)
    addError('electronicsProductCatalog catalog not found!')
else {
  CategoryModel category = getCategoryForCode(cm.getActiveCatalogVersion(),'sapSponsored')
  if(category==null)
    addError('Category with code:sapSponsored is not found!')
  else {
    addLog('Category with code:sapSponsored found!')
      if(category.getName(Locale.ENGLISH)==null)
            addError('Name in english for category sapSponsored is missing!')
    List<PrincipalModel> principals = category.getAllowedPrincipals()
    if(principals==null)
            addError('allowedPrincipals for category sapSponsored is missing!')
    else
        if (!principals.get(0).getUid().equals("customergroup"))
        addError('allowedPrincipals for category sapSponsored should be customergroup!')
  }

    ProductModel product = getProductForCode(cm.getActiveCatalogVersion(),'commerceSDCard')
    if(product==null)
      addError('Product with code:commerceSDCard is not found!')
    else{
      addLog('Product with code:commerceSDCard found!')
      if(product.getName(Locale.ENGLISH)==null)
      addError('Name in english for product commerceSDCard is missing!')

    Collection<CategoryModel> categories = product.getSupercategories()
        if(categories==null  ||  categories.isEmpty() ) {
        addError('No categories are assigned for product commerceSDCard!')
        }
        else {
            if (!categories.iterator().next().getCode().equals("sapSponsored")) {
          addError("Product: 'commerceSDCard' should be linked to Category: 'sapSponsored!'")
            }
        }
        List<ProductFeatureModel>features=product.getFeatures();
    if( features==null ){
      addError('Product commerceSDCard doesn\'t contain any features!')
      } else {
      def expectedFeatures = [
        '828.internal memory, 6',
        '828.read speed, 1644',
        '828.flash card type, 2305'
      ]
      def featuresList = []
      featuresOk = true
      for (pfm in features){
        featuresList.add(pfm.getQualifier().substring(pfm.getQualifier().lastIndexOf('/')+1))
      }
      for (exFeature in expectedFeatures){
        if (!featuresList.contains(exFeature)) {
          addError("Feature ${exFeature} is missing for product commerceSDCard!")
          featuresOk = false
        }
            }
            def setDif = featuresList.minus(expectedFeatures)
            if (setDif.size() > 0){
                featuresOk = false
                for (feature in setDif)
                    addError("Feature ${feature} is not supposed to be assigned to product commerceSDCard!")
            }

          if (featuresOk)
        addLog('Product with code:commerceSDCard has all expected features!')

            category = categoryService.getCategoryForCode(cm.getActiveCatalogVersion(),'sapSponsored')
      }
    }
}

printOutputLog()
