/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package my.commerce.trainingflexiblesearch.dao.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.commerce.trainingflexiblesearch.dao.MyOrderDao;


/**
 * Searches for a customer's orders placed in the last 30 days.
 */
public class MyOrderDaoImpl extends AbstractItemDao implements MyOrderDao
{

//	@Override
	public List<OrderModel> getRecentOrdersForCustomer(final CustomerModel customer)
	{
		// Calculate the time period
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		final Date today = cal.getTime(); // current date
		cal.add(Calendar.DATE, -30);
		final Date date30DaysAgo = cal.getTime(); // 30 days back

        // TODO Step 4.2 • Implement query written in Step 3.1

		// FS query to find customer orders in the last 30 days
		// SELECT ...

		final String queryString = "SELECT ...";


		// 1. Compile a query from this string


		// 2. Add the query parameters


		// 3. Execute the FS query


		return null; // Delete this line once you have a REAL return value (of type List<OrderModel> )
	}

}
