package com.orderfleet.webapp.service;

import com.orderfleet.webapp.domain.*;

import java.util.List;
import java.util.Set;
/**
 * Service interface for managing and calculating stock-related data.
 *
 * This interface defines methods for managing stock data, including updating
 * stocks based on orders and opening stock information, deleting stock data, and
 * retrieving live stocks by company.
 *
 * Stock-related operations are crucial for businesses that need to track and
 * manage inventory, and this service provides the necessary functionality for
 * performing these operations.
 *
 *
 *
 *
 *
 *@author  Nandakumar m arun
 *@since 1.111.1
 *
 */
public interface StockCalculationService {
    /**
     * Updates product stocks based on solid orders from an Inventory Voucher Header.
     *
     * This method processes solid orders from the provided InventoryVoucherHeader,
     * and updates the corresponding product stocks in the stock table. It also takes
     * a thread identifier for tracking purposes.
     *
     * @param orderDate The Inventory Voucher Header containing solid order information.
     * @param thread The identifier for the processing thread.
     * @return A list of Stock objects representing the updated product stocks.
     */
    List<Stock> saveSolidOrders(InventoryVoucherHeader orderDate, String thread);
    /**
     * This method saves and updates stock data based on opening stock information.
     *
     * @param saveOpeningStocks A set of opening stock data to be used for updating or adding stock entries.
     * @param companyId         The ID of the company for which stock data is being managed.
     * @return A list of Stock objects representing the updated stock data.
     */
    List<Stock> saveProductdstockdata(Set<OpeningStock> saveOpeningStocks,long companyId);
    /**
     * Deletes stock data based on opening stock information.
     *
     * @param openingstock The opening stock data to be deleted.
     * @return The Stock object representing the deleted stock data.
     */
    Stock deleteStockdata(OpeningStock openingstock);
    /**
     * Retrieves all live stocks by company ID.
     *
     * @param Companyid The ID of the company for which live stocks are being retrieved.
     * @return A list of Stock objects representing the live stocks for the company.
     */
    List<Stock> findAllLiveStocksByCompanyId(long Companyid);

}
