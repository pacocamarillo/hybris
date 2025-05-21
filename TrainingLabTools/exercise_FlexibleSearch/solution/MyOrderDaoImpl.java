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

	@Override
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

		// FS query to find customer orders in the last 30 days
		// (As close as possible to Java version, but still works in HAC FS Console:
		//  Select only PK, and use subqueries or DB functions where FSQ placeholders will be)
		// ===========================================
		// SELECT {o.pk}, {o.date}
		// FROM {
		//    Order as o JOIN Customer as c
		//    ON {o.user} = {c.pk}
		//    JOIN BaseStore as bs on {o.store} = {bs.pk}
		// }
		// WHERE {bs.uid} = 'electronics'
		//   AND {c.pk} = ({{ SELECT {PK} FROM {Customer} WHERE {uid}='test@sap.com' }})
		//   AND {o.date} >= trunc(TODAY) - '30' DAY


		// FS query translated to Java code
		// (CustomerModel is provided by method parameter
		//  and date30DaysAgo is a local variable we calculate.)
		// ============================================
		// SELECT {o.pk}
		// FROM {
		//    Order as o JOIN Customer as c
		//      ON {o.user} = {c.pk}
		//    JOIN BaseStore as bs
		//      ON {o.store} = {bs.pk}
		// }
		// WHERE {bs.uid} = 'electronics'
		//   AND {c.pk} = ?customer
		//   AND {o.date} >= ?oldestOrderDate


		// Java version, just before replacing type names and attribute names with model-class constants:
		// =======================================================
		//	SELECT {o.pk} "
		//	+ "FROM {Order as o JOIN Customer as c ON {o.user} = {c.pk} "
		//	+ " JOIN BaseStore as bs ON {o.store} = {bs.pk} } "
		//	+ "WHERE {bs.uid} = 'electronics' "
		//	+ "  AND {c.pk} = ?customer "
		//	+ "  AND {o.date} >= ?oldestOrderDate";

		final String queryString = "SELECT {o.pk} FROM {" + OrderModel._TYPECODE + " as o JOIN " + CustomerModel._TYPECODE
				+ " as c " + "  ON {o." + OrderModel.USER + "} = {c.pk} " + "JOIN " + BaseStoreModel._TYPECODE + " as bs " + "ON {o."
				+ OrderModel.STORE + "} = {bs.pk} } "
				+ "WHERE {bs." + BaseStoreModel.UID + "} = 'electronics' "
				+ "  AND {c.pk} = ?customer "
				+ "  AND {o." + OrderModel.DATE + "} >= ?oldestOrderDate";

		// 1. Compile a query from this string
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);

		// 2. Add the query parameters
		query.addQueryParameter("customer", customer);
		query.addQueryParameter("oldestOrderDate", date30DaysAgo);

		// 3. Execute the FS query
		return getFlexibleSearchService().<OrderModel> search(query).getResult();
	}

}
