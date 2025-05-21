import java.util.HashMap;
import java.util.Map;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.order.OrderModel;
import java.text.NumberFormat;

def script = new GroovyScriptEngine( '.' ).with {
    loadScriptByName( '../../Logger.groovy' ) //for this to work, Logger.groovy should've already been put inside platform directory
}
this.metaClass.mixin script

userService.setCurrentUser(userService.getAdminUser());

List<CustomerModel> customers;
String target = 'test@sap.com';
try{
    final Map<String, String> params = new HashMap<String, String>();
    params.put(CustomerModel.UID, target);
    customers=defaultGenericCustomerDao.find(params);
	addLog('defaultGenericCustomerDao and method find(params)   .....OK')
}catch(groovy.lang.MissingPropertyException e){
	addError('customerDao is not defined!')
}catch(groovy.lang.MissingMethodException e){
    addError('Method find(param) doesnt exist! Check if you defined customerDao properly!')
}

if(customers==null || customers.size()==0){
  addError('Couldnt find customer ' + target + '. Consequently, unable to test myOrderDao.getRecentOrdersForCustomer(customer) method')
} else{
  	addLog("Number of customers found: " + customers.size())
	try{
        List<OrderModel> orders = myOrderDao.getRecentOrdersForCustomer(customers.get(0));
        if(orders==null || orders.size()==0)
            addError('Couldnt find orders. Check if the specified customer has placed any orders and if your flexible query is correct!')
        else {
            addLog(orders.size() + (orders.size() > 1 ? ' orders ':' order ') + 'found for customer ' + target + '.')
            NumberFormat formatter = NumberFormat.getCurrencyInstance()
            for (int i = 0; i < orders.size(); i++) {
                addLog("Total for order " + (i+1) + " : " + formatter.format(orders[i].getTotalPrice()))
            }
        }
    } catch(groovy.lang.MissingPropertyException e){
        addError('myOrderDao is not defined!')
    } catch(groovy.lang.MissingMethodException e){
        addError('Method getRecentOrdersForCustomer(customer) doesnt exist! Check if you defined myOrderDao properly!')
    } catch(de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException e){
        addError('There is error in your flexible search query in method: myOrderDao.getRecentOrdersForCustomer(customer)!')
        addError(e)
    }
}

printOutputLog()