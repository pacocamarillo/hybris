/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package my.commerce.trainingservices.service.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import my.commerce.trainingservices.service.CustomizedCustomerAccountService;


/**
 * Custom implementation extending the CustomerAccountService interface with a method returning active consents
 */
public class DefaultCustomizedCustomerAccountService extends DefaultCustomerAccountService implements CustomizedCustomerAccountService
{

	//TODO Step 1 declare a variable for holding the service to manage consents.
	//private CommerceConsentService ...;

	public List<ConsentModel> getAllActiveConsents(final CustomerModel customer, final BaseSiteModel baseSite)
	{
		final List<ConsentModel> activeConsents = new ArrayList<>();

		/*
		 * TODO Step 2 implement the business logic here
		 */

		return activeConsents;
	}

	/*
	 * TODO Step 3 override the original closeAccount method to withdraw all active consents before closing account.
	 * Use/uncomment this signature:
	 	@Override
		public CustomerModel closeAccount(final CustomerModel customer)
	 */


	/* 
	 * TODO Step 1 need a setter for the local variable
	 * Setter used by spring to inject a property via Dependency Injection (DI)
	 */
	/*
	 * public void setCommerceConsentService(...) { }
	 */

}
