/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

/* -- Begin SAP Training: Exercise Facades -- */
package my.commerce.trainingfacades.populators;

import de.hybris.platform.commercefacades.futurestock.FutureStockFacade;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.services.BaseStoreService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This populator uses the product model to check if that product is out of stock. If it is out of stock, the populator
 * sets the estimatedRestockDays attribute on the StockData bean.
 */
public class DefaultProductRestockEstimatePopulator<SOURCE extends ProductModel, TARGET extends StockData>
		implements Populator<SOURCE, TARGET>
{
	/* Injected services */
	private BaseStoreService baseStoreService;
	private FutureStockFacade futureStockFacade;
	private CommerceStockService commerceStockService;

	final Logger logger = LoggerFactory.getLogger(DefaultProductRestockEstimatePopulator.class);

	@Override
	public void populate(final SOURCE productModel, final TARGET stockData) throws ConversionException
	{
		/*
		 * We need to get the active base store to get the stock level status of the product. We use the
		 * BaseStoreService to get the currently active base store (better than hard-coding a base store UID). Then we use
		 * that value in a call to CommerceStockService to get the stock level status.
		 */
		final StockLevelStatus stockLevelStatus = getCommerceStockService().getStockLevelStatusForProductAndBaseStore(productModel,
				getBaseStoreService().getCurrentBaseStore());

		/*
		 * This value will further be initialized as either the number of days between today and the future stock
		 * availability, or 0.
		 */
		final int estimatedRestockDays;

		/*
		 * We use the StockLevelStatus of the product to determine if the product is out of stock. If it is, let's set a
		 * non-zero value to the estimatedRestockDays attribute on the StockData bean.
		 *
		 * For training purposes, this value could have been hard-coded, but it's more realistic to use another API such
		 * as the FutureStockService to get our estimated re-stock days value. FutureStockService provides us with a mock
		 * restock date. We can use this to calculate the number of dates between today and the mock re-stock date.
		 *
		 */
		if (stockLevelStatus != null && StockLevelStatus.OUTOFSTOCK.equals(stockLevelStatus))
		{
			logger.debug("********* StockLevelStatus for product code: " + productModel.getCode() + " is OUTOFSTOCK");

			/*
			 * Normally, by convention, we would only call other services from a populator. In this case we chose to call the
			 * FutureStockFacade method rather than the FutureStockService method to avoid having to duplicate a bunch of
			 * the code from the facade just to get the product's restock date. The FutureStock facade and service
			 * are just mocked anyway, so in an actual system we would need to provide an implementation for them.
			 *
			 * The getFutureAvailability() method returns an ArrayList of FutureStockData objects. We are just getting the
			 * first object in the list and using its associated date of restock - keeping it simple.
			 */
			final Date estRestockDate = getFutureStockFacade().getFutureAvailability(productModel.getCode()).get(0).getDate();
			logger.debug("***** estRestockDate: " + estRestockDate);


			/*
			 * Note: How you calculate the number of days between the current date and the restock date is up to you. Any
			 * approach that calculates the correct number of days is fine (using the Calendar, etc).
			 *
			 * We wanted to use this handy Java 8 method to easily get the number of days between two dates, but it
			 * requires passing in LocalDate objects rather than Date objects. Since the FutureStockService returned us a
			 * regular Date object, we need to convert that into a LocalDate before passing it into the between() method
			 * below.
			 *
			 * Also, to keep it simple and not deal with partial days for restock, let's round up to the nearest
			 * integer using the Math ceil() function.
			 */
			final LocalDate estRestockLocalDate = LocalDate.ofInstant(estRestockDate.toInstant().truncatedTo(ChronoUnit.DAYS),
					ZoneId.systemDefault());
			logger.debug("***** estRestockLocalDate: " + estRestockLocalDate);

			final LocalDate todayLocalDate = LocalDate.ofInstant(Instant.now().truncatedTo(ChronoUnit.DAYS), ZoneId.systemDefault());
			logger.debug("***** todayLocalDate: " + todayLocalDate);

			estimatedRestockDays = (int) ChronoUnit.DAYS.between(todayLocalDate, estRestockLocalDate);
			logger.debug("***** estimatedRestockDays: " + estimatedRestockDays);

			logger.info("Product code: " + productModel.getCode() + " is estimated to be back in stock in " + estimatedRestockDays + " days");


		} else {
			/*
			 * If a product isn't out of stock, then by default it's estimated restock days should be 0, just to avoid a
			 * null value, so let's initialize our variable to 0.
			 */
			estimatedRestockDays=0;
			logger.debug("********* StockLevelStatus for product code: " + productModel.getCode() + " is null or " + stockLevelStatus + ". Method populate() will set estimatedRestockDays to 0." );
		}

		/*
		 * The most important part of the populate method is here -- populating the value of the
		 * restockDaysEstimate field on the StockData object
		 */
		stockData.setEstimatedRestockDays(estimatedRestockDays);

	}


	/* Getters/setters */
	/**
	 * @return the injected baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to inject
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the injected futureStockFacade
	 */
	public FutureStockFacade getFutureStockFacade()
	{
		return futureStockFacade;
	}

	/**
	 * @param futureStockFacade
	 *           the futureStockFacade to inject
	 */
	public void setFutureStockFacade(final FutureStockFacade futureStockFacade)
	{
		this.futureStockFacade = futureStockFacade;
	}

	/**
	 * @return the injected commerceStockService
	 */
	public CommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * @param commerceStockService
	 *           the commerceStockService to inject
	 */
	public void setCommerceStockService(final CommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}
}

/* -- End SAP Training: Exercise Facades -- */
