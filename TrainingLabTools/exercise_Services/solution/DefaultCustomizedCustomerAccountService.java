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



import my.commerce.trainingservices.service.CustomizedCustomerAccountService;


/**
 * Custom implementation extending the CustomerAccountService interface with a method returning active consents
 */
public class DefaultCustomizedCustomerAccountService extends DefaultCustomerAccountService implements CustomizedCustomerAccountService
{

	private CommerceConsentService commerceConsentService;


	public List<ConsentModel> getAllActiveConsents(final CustomerModel customer, final BaseSiteModel baseSite)
	{
		final List<ConsentModel> activeConsents = new ArrayList<>();

		/*
		 * TODO Step 2 implement the business logic here
		 */

		final List<ConsentTemplateModel> consentTemplates = commerceConsentService.getConsentTemplates(baseSite);
		for (final ConsentTemplateModel consentTemplate : consentTemplates)
		{
			final ConsentModel consent = commerceConsentService.getActiveConsent(customer, consentTemplate);
			if (consent != null && consent.isActive())
			{
				activeConsents.add(consent);
			}
		}
		return activeConsents;
	}


	/*
	 * TODO Step 3 override the original closeAccount method to withdraw all active consents before closing account.
	 */
	@Override
	public CustomerModel closeAccount(final CustomerModel customer)
	{
		final List<ConsentModel> allActiveConsents = this.getAllActiveConsents(customer,
				super.getBaseSiteService().getCurrentBaseSite());
		for (final ConsentModel consent : allActiveConsents)
		{
			commerceConsentService.withdrawConsent(consent);
		}
		return super.closeAccount(customer);
	}


	public void setCommerceConsentService(CommerceConsentService commerceConsentService)
	{
		this.commerceConsentService = commerceConsentService;
	}

}
