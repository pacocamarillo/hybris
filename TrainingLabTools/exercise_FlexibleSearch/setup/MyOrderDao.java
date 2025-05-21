/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package my.commerce.trainingflexiblesearch.dao;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;


/**
 * Searches for a customer's orders placed in the last 30 days.
 */
public interface MyOrderDao extends Dao
{

    // TODO Step 4.1 • Define method signature

	/**
	 * Searches for orders placecd in the last 30 days belonging to the <code>customer</code>.
	 *
	 * @param customer
	 *           whose orders are looked at
	 * @return a list of orders placed by the customer in the last 30 days
	 */
	//List...
}
