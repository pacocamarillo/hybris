/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

package my.commerce.trainingservices.service;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.List;


/**
 * Custom interface extending the CustomerAccountService interface with a method returning active consents
 */
public interface CustomizedCustomerAccountService extends CustomerAccountService
{

	/**
	 * get all active consents of a given customer for the given basesite
	 */
	public List<ConsentModel> getAllActiveConsents(final CustomerModel customer, final BaseSiteModel baseSite);

}
