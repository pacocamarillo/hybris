import javax.xml.xpath.*
import javax.xml.parsers.DocumentBuilderFactory


def script = new GroovyScriptEngine( '.' ).with {
    loadScriptByName( '../../Logger.groovy' ) //for this to work, Logger.groovy should've already been put inside platform directory
}
this.metaClass.mixin script


def checkLocalExtensionXml(boolean shouldExist, String... extNames){
    def fis =null;
    try{
        def xpath = XPathFactory.newInstance().newXPath()
        def builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        fis = new FileInputStream(new File(System.getProperty("HYBRIS_CONFIG_DIR"),"localextensions.xml"))  
        def localextensionsXml     = builder.parse(fis).documentElement
    
        addLog('Checking localextensions.xml...');
        for(def extName:extNames){
            //note: ^ is the xor operator.  It's being used as follows: if left operand is true, it essentially inverts the right operand (  i.e., false ^ b = b     true ^ b = !b  )
            if(  !shouldExist ^  ('true'.equals(xpath.evaluate("/hybrisconfig/extensions/extension/@name=\'"+extName+"\'", localextensionsXml))  || xpath.evaluate("/hybrisconfig/extensions/extension[contains(@dir,'"+extName+"')]", localextensionsXml,XPathConstants.NODE)!=null)    ){
                addLog('...checking that extension named ' + extName + ' IS ' + ( (shouldExist)?'':'NOT ' ) + 'present.....OK')
            }else{
                addError('Please ' + ( (shouldExist)?"add":"remove" ) + ' <extension name=\"'+extName+'\"/> ' + ( (shouldExist)?'to':'from' ) + ' your localextensions.xml, rebuild the platform, and restart the server')
            }
        }
    
    }finally{
        if(fis!=null)
        fis.close()
    }   
}

def checkLocalProperties(valuesMap){
    Properties properties = new Properties()
    File propertiesFile = new File(System.getProperty("HYBRIS_CONFIG_DIR"),"local.properties")
    propertiesFile.withInputStream {
        properties.load(it)
    }
  for(entry in valuesMap){
    if(properties."${(entry.key)}" == "${entry.value}"){
        addLog("Checking property ${entry.key} ....OK")
    }else{
        if ("${(entry.key)}" == 'website.megacorp.http') {
            if (properties."${(entry.key)}" == 'http://localhost:9001?site=megacorp') {
                addLog("Checking property ${entry.key} ....OK")
            }else {
                addError("Please set property ${entry.key}=${entry.value} or ${entry.key}=http://localhost:9001?site=megacorp")
            }
        } else if ("${(entry.key)}" == 'website.megacorp.https') {
            if (properties."${(entry.key)}" == 'https://localhost:9002?site=megacorp') {
                addLog("Checking property ${entry.key} ....OK")
            }else {
                addError("Please set property ${entry.key}=${entry.value} or ${entry.key}=https://localhost:9002?site=megacorp")
            }
        } else {
            addError("Please set property ${entry.key}=${entry.value}")
        }

    }
  }
}

//extensions to verify are PRESENT
checkLocalExtensionXml(true, "megacorpfulfilmentprocess","megacorpcore","megacorpinitialdata","megacorpfacades","megacorpstorefront","megacorpbackoffice")

//extensions to verify are ABSENT
checkLocalExtensionXml(false, "megacorptest");

checkLocalProperties(['hac.webroot':"/hac",'website.megacorp.http':"http://megacorp:9001",'website.megacorp.https':"https://megacorp:9002"])

printOutputLog()