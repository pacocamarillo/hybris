import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.type.TypeService;
import my.commerce.trainingdatamodeling.service.TrainingdatamodelingService;
import java.util.Collection;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;


def typeService = spring.getBean("typeService");

def script = new GroovyScriptEngine( '.' ).with { loadScriptByName( '../../Logger.groovy' ) //for this to work, Logger.groovy should've already been put inside platform directory
}
this.metaClass.mixin script

def checkType(String typeCode, Map<String, String> expectedAttribQualifier2TypeMap ){
	addLog('\nChecking type:'+ typeCode);
  
    ComposedTypeModel type = null;
    try {
  	  type = typeService.getComposedTypeForCode(typeCode);
    } catch(UnknownIdentifierException e) {
       addError( ">>>>> type: " + typeCode + " does not exist! " );
       return;
    }
      
  	final Collection<AttributeDescriptorModel> actualTypeAttribs = type.getDeclaredattributedescriptors();
  
  	Map<String, String> actualAttribQualifier2TypeMap = new HashMap<>();
  	for (final AttributeDescriptorModel iAttrib : actualTypeAttribs )
	{
		final String attribQualifier = iAttrib.getQualifier();

		final TypeModel attribType = iAttrib.getAttributeType();
		final String attribTypeCode = attribType.getCode();
      
      	actualAttribQualifier2TypeMap.put( attribQualifier, attribTypeCode );
	}
  
  	Set<String> expectedAttribQualifierSet = expectedAttribQualifier2TypeMap.keySet();
    Set<String> actualAttribQualifierSet = actualAttribQualifier2TypeMap.keySet()

    for( String iExpectedQualifier : expectedAttribQualifierSet ) {
      if( actualAttribQualifierSet.contains( iExpectedQualifier ) ) {
        final String expectedType = expectedAttribQualifier2TypeMap.get( iExpectedQualifier );
        final String actualType = actualAttribQualifier2TypeMap.get(iExpectedQualifier);
        
        //remember, the return value from the "actual" map COULD be null, except that we did a "contains()" test.
        if( expectedType.equals( actualType )  ) {   
			addLog(" attribute: " + iExpectedQualifier + " - type: " + expectedType + " exists ...OK" );
        } else {
         	addError(">>>>> attribute '" + iExpectedQualifier + "' exists but wrong type\n      (is " + actualType + " but should be " + expectedType + ")." );
        }
      } else {
       	addError( ">>>>> Type " + typeCode + " is missing attribute with exact qualifier '" 
                 + iExpectedQualifier + "' \n      (could also be due to a relation involving " + typeCode + ")." );
      }
    }
}

def checkSuperType( String typeCode, String superTypeCode ) {
  
  
    ComposedTypeModel type = null;
    try{
    	type = typeService.getComposedTypeForCode(typeCode);
    } catch( UnknownIdentifierException e ) {
    	addError( ">>>>> type: " + typeCode + " does not exist!" );
        return;
    }
    Collection<ComposedTypeModel> superTypes = type.getAllSuperTypes();

    ComposedTypeModel superType = null;
    try {
  		superType = typeService.getComposedTypeForCode( superTypeCode );  
    } catch(UnknownIdentifierException e) {
    	addError( ">>>>> type: " + typeCode + " does not exist!" );
        return;
    }

  
    if( superTypes.contains(superType ) ) {
  		addLog( "\n" + typeCode + " type extends " + superTypeCode + " type....OK");
    } else {
        addError( "\n>>>>> " + typeCode + " type DOES NOT extend " + superTypeCode + " type" );
    }
}


//This method can only see attributes DECLARED in a given type (i.e., to also see inherited attributes, you must 
//add lines to look into: type.getInheritedattributedescriptors()  )
def checkOptionalAttributes( String typeCode, String[] optionalAttribQualifierArry ) {
  
  	addLog('\nChecking ' + typeCode + ' for OPTIONAL attributes...' );
  	
  	List<String> optionalAttribQualifierList = Arrays.asList( optionalAttribQualifierArry );
    
    ComposedTypeModel type = null;
    try {
  	   type = typeService.getComposedTypeForCode(typeCode);
    } catch(UnknownIdentifierException e) {
       addError( ">>>>> type: " + typeCode + " does not exist! " );
       return;
    }
  	final Collection<AttributeDescriptorModel> declaredAttributes = type.getDeclaredattributedescriptors();

    //build Map of declared attribute qualifiers to optional (t/f)
  	Map<String, Boolean> declaredQualifier2OptionalMap = new HashMap<>();
	for( AttributeDescriptorModel iDeclaredAttrib : declaredAttributes ) {
      final String iQualifier = iDeclaredAttrib.getQualifier();
      declaredQualifier2OptionalMap.put( iQualifier, iDeclaredAttrib.getOptional() );
    }  
    
  
  	for( String iQualifier : optionalAttribQualifierArry ) {
      if( declaredQualifier2OptionalMap.containsKey( iQualifier ) ) {
  		Boolean attributeDeclaredOptional = declaredQualifier2OptionalMap.get( iQualifier );
      	if( attributeDeclaredOptional == null ) {
        	addError( ">>>>> " + typeCode + " attribute '" + iQualifier + "' might be declared for supertype or not declared at all." );
        } else {
          if( attributeDeclaredOptional.booleanValue() ) {
            addLog( " '" + iQualifier + "' was declared OPTIONAL ...OK" );
          } else {
        	addError( ">>>>> " + typeCode + " attribute '" + iQualifier + "' must be declared OPTIONAL but IS NOT." );
          }
        }
      } else {
      	addError( ">>>>> " + typeCode + " attribute '" + iQualifier + "' must be declared OPTIONAL but is NOT DECLARED in this type." );
      }
  	}
}


def checkIfClassExists( final String javaClassName ) {
  
  try {
 	Class modelClass = Class.forName( javaClassName ); 
  } catch (ClassNotFoundException e ) {
  	addError( ">>>>> " + javaClassName + " does not exist -- \n      (possibly no ...autogenerate='true'...)\n      (possibly misspelled type code or base package)" );
  }
  
  addLog("\n" + javaClassName + " exists. ....OK"); 

}


def checkLocalProperties(valuesMap){
	Properties properties = new Properties()
	InputStream inputStream = TrainingdatamodelingService.getClassLoader().getResourceAsStream("localization/trainingdatamodeling-locales_en.properties")
	if(inputStream==null){
		addError('cannot find trainingdatamodeling-locales_en.properties file')
	}
	else{
		properties.load(inputStream)

		for(entry in valuesMap){
			if(properties."$entry" !=null && properties."$entry" !=""){
				addLog("Checking property $entry ....OK")
			}else{
				addError("Please set property $entry")
			}
		}
	}
}

addLog('\n* 1. Checking SAP Commerce Types *')

checkIfClassExists("de.hybris.platform.core.model.product.ProductModel");

Map<String, String> bookTypeMap=new HashMap<>();
bookTypeMap.put("language","java.lang.String");
bookTypeMap.put("ISBN10","java.lang.String");
bookTypeMap.put("ISBN13","java.lang.String");
bookTypeMap.put("authors","EBook2AuthorRelationauthorsColl");
checkType("EBook", bookTypeMap)

checkSuperType( "EBook", "Product" );
checkIfClassExists( "my.commerce.trainingdatamodeling.model.EBookModel" );
checkOptionalAttributes( "EBook", "edition", "authors" );


Map<String, String> userTypeMap=new HashMap<>();
userTypeMap.put("ebooks","EBook2AuthorRelationebooksColl");
checkType("User", userTypeMap)

checkOptionalAttributes( "User", "ebooks" );
checkIfClassExists("de.hybris.platform.core.model.user.UserModel");



def propertiesMap=[
	'type.EBook.publication.name',
	'type.EBook.publication.description',
  
  	'type.EBook.publisher.name',
	'type.EBook.publisher.description',
  
]

addLog('\n* 2. Checking localization *\n')
checkLocalProperties(propertiesMap)

printOutputLog()