import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.variants.model.VariantCategoryModel;
import de.hybris.platform.variants.model.VariantValueCategoryModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.GenericVariantProductModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.variants.model.VariantTypeModel;


def script = new GroovyScriptEngine( '.' ).with {
    loadScriptByName( '../../Logger.groovy' ) //for this to work, Logger.groovy should've already been put inside platform directory
}
this.metaClass.mixin script

//NOTE: If we run scripts from ant there is no user session information
userService.setCurrentUser(userService.getAdminUser())

// NOTE: if catalog id not found, catalogService.getCatalogForId() throws exception -- it does NOT return null
// this wrapper fixes that
def getCatalogForId(catalogId) {
    try{
        return catalogService.getCatalogForId(catalogId);
    }
    catch(UnknownIdentifierException e) {
        return null;
    }
}
  
// NOTE: if category code not found, categoryService.getCategoryForCode() throws exception -- it does NOT return null
// this wrapper fixes that
def getCategoryForCode(catalogVersion, categoryCode) {
    try{
        return categoryService.getCategoryForCode(catalogVersion, categoryCode);
    }
    catch(UnknownIdentifierException e) {
        return null;
    }
}
def getCategoryForCode(categoryCode) {
    try{
        return categoryService.getCategoryForCode(categoryCode);
    }
    catch(UnknownIdentifierException e) {
        return null;
    }
}

  
// NOTE: if product code not found, productService.getProductForCode() throws exception -- it does NOT return null
// this wrapper fixes that
def getProductForCode(catalogVersion, productCode) {
    try{
        return (ProductModel)productService.getProductForCode( catalogVersion, productCode )
    }
    catch(UnknownIdentifierException e) {
        return null;
    }
}
  



def checkProductCategories(product, exCategoryList){
    List<CategoryModel>categories=product.getSupercategories();
    if(categories==null){
        addError('Product with code:8942944779 doesn\'t contain any categories!')
    }
    def realCategoryList = []
    for (category in categories){
        realCategoryList.add(category.getCode())
    }

    for (exCategory in exCategoryList){
        if (!realCategoryList.contains(exCategory))
            addError("Category ${exCategory} should be linked to product with code: 8942944779!\n     (I.e., Product should be assigned to this category)")
    }
    def setDif = realCategoryList.minus(exCategoryList)
    if (setDif.size() > 0){
        for (category in setDif)
            addError("Category ${category} is not supposed to be supercategory for product with code:8942944779!")
    }
}


def checkProductFeatures(product, exFeatureList){
    List<ProductFeatureModel>features=product.getFeatures();
    if(features==null){
        addError('Product with code:8942944779 doesn\'t contain any features!')
    }
    def realFeatureList = []
    for (pfm in features){
        realFeatureList.add(pfm.getQualifier().substring(pfm.getQualifier().lastIndexOf('/')+1))
    }

    for (exFeature in exFeatureList){
        if (!realFeatureList.contains(exFeature))
            addError("Feature ${exFeature} is missing from product with code: 8942944779!")
    }
    def setDif = realFeatureList.minus(exFeatureList)
    if (setDif.size() > 0){
        for (feature in setDif)
            addError("Feature ${feature} should not be assigned to product with code: 8942944779!")
    }
}





def checkSuperCategories(code, exSuperCatList, isClassifying){
    try{
        def categoryModel;

        if (isClassifying) {
            categoryModel = getCategoryForCode(code)
        } else {
            CatalogModel cm = getCatalogForId('electronicsProductCatalog');
            categoryModel = getCategoryForCode(cm.getActiveCatalogVersion(), code);
        }

        List<CategoryModel>categories = categoryModel.getSupercategories();
        
        
        def superCategoryList=[]
        for (superCategory in categories){
            superCategoryList.add(superCategory.getCode())
        }
 
        for (exSuperCategory in exSuperCatList) {
            if(!superCategoryList.contains(exSuperCategory)){
                addError("Missing supercategory: $exSuperCategory for category: $code")
            }else {
                addLog("Super category: $exSuperCategory is linked to category: $code.........OK")
            }
        }
  
    }catch(UnknownIdentifierException ex){
        addError("Error in verifying ${code}. Make sure this category exists or being created.")
    }
}


def checkClassificationCategories(classCatCode, features){
   
    try{
        def categoryModel = getCategoryForCode(classCatCode)
        def classModelSet=defaultClassificationClassesResolverStrategy.resolve(categoryModel)
        def attr=defaultClassificationClassesResolverStrategy.getAllClassAttributeAssignments(classModelSet)
        addLog("Classification Category: $classCatCode.........OK")
        addLog("checking Category features for: $classCatCode")
            
        def createdFeatures=[:]
 
        attr.each{
            createdFeatures.put(it.getClassificationAttribute().getCode(), it.getAttributeType())
        }

        features.keySet().each{ feature ->

            if(createdFeatures.containsKey(feature)){
                if(createdFeatures.get(feature)==features.get(feature)){
                    addLog("Category feature: $feature...........OK")
                }else{
                    addError("Category feature: $feature is wrong type. It should be: ${features.get(feature)}")
                }
            }else {
                addError("Missing category feature: $feature for classifying category: $classCatCode")
            }
           
        }
        
    }catch(UnknownIdentifierException ex){
        addError("Error in verifying $classCatCode. Make sure this classification category exists or being created.")
    }
    
}

def checkBaseProduct(){
    CatalogModel cm = getCatalogForId('electronicsProductCatalog');
    if(cm==null)
        addError('electronicsProductCatalog catalog not found!')
    else{
        ProductModel actionCam=(ProductModel)getProductForCode(cm.getActiveCatalogVersion(),'8942944779')
        if(actionCam==null)
            addError('Action Cam with code: 8942944779 is not found!')
        else{
            addLog('Action Cam with code: 8942944779 found!')

            def expectedCategories = [
                'Camcorders_Color',
                'action-cam',
            ]
            checkProductCategories(actionCam, expectedCategories);
    
            def expectedFeatures = [
                'advancedaudio.noisefree',
                '2910.built-in speakers, 2433',
                '2910.audio system, 442',
                '2910.built-in microphone, 1025'
            ]
            checkProductFeatures(actionCam, expectedFeatures)
            
            if( actionCam.getVariantType() == null ) {
                addError("Action Cam with code: 8942944779 requires a value for attribute: 'variantType'.")
            } else {
                if( actionCam.getVariantType().code != 'GenericVariantProduct' ) {
                    addError("Action Cam with code: 8942944779 has incorrect value for attribute: 'variantType' -- required to be 'GenericVariantPoduct'.")
                } else {
                    addLog("Action Cam with code: 8942944779 has attribute: 'variantType' value of GenericVariantPoduct..........OK");

                }
            }
        }
    }
}

def checkGenericVariantProduct(expectedCategory, exBaseProductCode, code){
    CatalogModel cm = getCatalogForId('electronicsProductCatalog');
    if(cm==null)
        addError('electronicsProductCatalog catalog not found!')
    else{
        GenericVariantProductModel variantProduct = (GenericVariantProductModel)getProductForCode(cm.getActiveCatalogVersion(),code)
        if(variantProduct==null)
            addError("GenericVariantProduct with code: $code is not found!")
        else{
            addLog("GenericVariantProduct with code: $code found!")
        }

        def expectedCategories = [
            expectedCategory
        ]
        checkProductCategories(variantProduct, expectedCategories);

        
        def realBase = variantProduct.getBaseProduct().getCode();
        if (realBase == exBaseProductCode)
            addLog("Base product: ${exBaseProductCode} is found linked to GenericVariantProduct: ${code}")
        else
            addError("Base product: ${exBaseProductCode} is not linked to for GenericVariantProduct: ${code}")
    }
}

// Check category: action-cam
CatalogModel elecProdCatalog = getCatalogForId('electronicsProductCatalog');
CategoryModel actionCamCategory = getCategoryForCode(elecProdCatalog.getActiveCatalogVersion(), 'action-cam');

if( actionCamCategory == null ) {
    addError("Category: action-cam not found in electronicsProuctCatalog:Online.")
} else {
    addLog("Category: action-cam FOUND...........OK")
    exSuperCategories = [
        '584',
        'advancedaudio'
    ]
    checkSuperCategories('action-cam', exSuperCategories, false)
}


// Check classifying category: advancedaudio
CatalogModel elecClassifSys = getCatalogForId('ElectronicsClassification');
CategoryModel advancedAudioCC = getCategoryForCode(elecClassifSys.getActiveCatalogVersion(), 'advancedaudio');
if( advancedAudioCC == null ) {
    addError("Classifying Category: 'advancedaudio' not found in ElectronicsClassification. (Might be in wrong catalog).")
} else {
    addLog("Classifying Category: 'advancedaudio' FOUND...........OK")
    if( !(advancedAudioCC instanceof ClassificationClassModel) ) {
        addError("Classifying Category: 'advancedaudio' is of wrong type -- must be a Classifying Category");
    } else {

        def exSuperCategories = [
            '2910'
        ]
        checkSuperCategories('advancedaudio', exSuperCategories, true)

        def exCatFeatures=
            ["noiseFree":ClassificationAttributeTypeEnum.BOOLEAN]
        checkClassificationCategories('advancedaudio',exCatFeatures)
    }
}


// Check base product: 8942944779
checkBaseProduct()



// Check VariantCategory: Camcorders_Color
CategoryModel camcordersColorVariantCategory = getCategoryForCode(elecProdCatalog.getActiveCatalogVersion(), 'Camcorders_Color');
if( camcordersColorVariantCategory == null ) {
    addError("Variant Category: 'Camcorders_Color' not found in electronicsProductCatalog:Online.")
} else {
    addLog("Variant Category: 'Camcorders_Color' FOUND............OK")
    if( !(camcordersColorVariantCategory instanceof VariantCategoryModel) ) {
        addError("Variant Category: 'Camcorders_Color' is of wrong type -- must be a VariantCategory.")
    } else {
        addLog("Variant Category: 'Camcorders_Color' is of correct type, VariantCategory............OK")
    }
}



// Check VariantValueCategory: Camcorders_Red
def checkVariantValueCategory( variantValCategoryCode, baseProdCode, variantProdCode ) {
    CatalogModel elecProdCatalog = getCatalogForId('electronicsProductCatalog');
    CategoryModel variantValueCategory = getCategoryForCode(elecProdCatalog.getActiveCatalogVersion(), variantValCategoryCode);
    if( variantValueCategory == null ) {
        addError("Variant Value Category: '$variantValCategoryCode' not found in electronicsProductCatalog:Online.")
    } else {
        addLog("Variant Value Category: '$variantValCategoryCode' FOUND............OK")
    
        if( !(variantValueCategory instanceof VariantValueCategoryModel) ) {
            addError("Variant Value Category: '$variantValCategoryCode' is of wrong type -- must be a VariantValueCategory.")
        } else {
            addLog("Variant Value Category: '$variantValCategoryCode' is of correct type, VariantValueCategory............OK")
    
            exSuperCategories = [
                'Camcorders_Color',
            ]
            checkSuperCategories(variantValCategoryCode, exSuperCategories, false)
    
            ProductModel variantProd = getProductForCode(elecProdCatalog.getActiveCatalogVersion(), variantProdCode);
            if( variantProd == null ) {
                addError("GenericVariantProduct: '$variantProdCode' not found in electronicsProductCatalog:Online");
            } else {
                addLog("Product: '$variantProdCode' FOUND in electronicsProductCatalog:Online..........OK")
    
                if( !(variantProd instanceof GenericVariantProductModel) ) {
                    addError("Generic Variant Product: '$variantProdCode' is of wrong type -- must be a GenericVariantProduct.");
                } else {
                    addLog("Generic Variant Product: '$variantProdCode' is of correct type: GenericVariantProduct..........OK");
    
                    //Check GenericVariantProduct
                    checkGenericVariantProduct(variantValCategoryCode, baseProdCode, variantProdCode)
                }
            }
        }
    }
}


addLog("============")
// Check VariantValueCategory: Camcorders_Red
checkVariantValueCategory( 'Camcorders_Red', '8942944779', '8942944779_red' )

// Check VariantValueCategory: Camcorders_Blue
addLog("============")
checkVariantValueCategory( 'Camcorders_Blue', '8942944779', '8942944779_blue' )


printOutputLog()

