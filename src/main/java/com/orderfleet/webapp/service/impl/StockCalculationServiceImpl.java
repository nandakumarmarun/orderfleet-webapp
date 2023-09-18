package com.orderfleet.webapp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.repository.DocumentStockCalculationRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.StockCalculationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.StockCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StockCalculationServiceImpl implements StockCalculationService {

    private final Logger log = LoggerFactory.getLogger(StockCalculationServiceImpl.class);

    @Inject
    private StockCalculationRepository  stockCalculationRepository;

    @Inject
    private OpeningStockRepository openingStockRepository;

    @Inject
    private DocumentStockCalculationRepository documentStockCalculationRepository;

    /**
     * Asynchronously processes solid orders from an Inventory Voucher Header and updates
     * the corresponding product stocks in the stock table.
     *
     * This method processes solid orders from the provided InventoryVoucherHeader and updates
     * the product stocks. It also takes a thread identifier for tracking purposes.
     *
     * @param orderProducts The Inventory Voucher Header containing solid order information.
     * @param thread The identifier for the processing thread.
     * @return A list of Stock objects representing the updated product stocks.
     */
    @Override
    @Async
    public List<Stock> saveSolidOrders(
            InventoryVoucherHeader orderProducts ,
            String thread) {

        log.debug(thread + "Enter : saveSolidOrders -> {}");

        long companyid = orderProducts.getCompany().getId();

        log.debug(thread +" Company Id : "+ companyid);


        // Initialize a list to store order products
        List<Stock> orderProductsList = new ArrayList<>();


        // Extract stock location PIDs from the InventoryVoucherDetails
        List<String> stocklocations =  orderProducts.getInventoryVoucherDetails()
                .stream().map(p->p.getSourceStockLocation().getPid())
                .collect(Collectors.toList());

        log.debug(thread + "Stock Location : "+stocklocations);


        //Extract product PIDs from the InventoryVoucherDetails
        List<String> productPids =  orderProducts.getInventoryVoucherDetails()
                .stream().map(p->p.getProduct().getPid())
                .collect(Collectors.toList());

        log.debug(thread + " productPids : "+ productPids);

        log.debug(thread + "Document Name  : "+ orderProducts.getDocument().getName());
        log.debug(thread + "Document pid  : "+ orderProducts.getDocument().getPid());

        //Retrieve optional DocumentStockCalculation based on document PID
        Optional<DocumentStockCalculation> optionalStockCalculation =
                documentStockCalculationRepository
                        .findOneByDocumentPid(
                                orderProducts.getDocument().getPid());

        if(optionalStockCalculation.isPresent()){
            log.debug(thread + " OptionalStockCalculation : "+ optionalStockCalculation.isPresent());
            log.debug(thread + " Actual  : "+optionalStockCalculation.get().getClosingActual());
            log.debug(thread + " Logical : "+optionalStockCalculation.get().getClosingLogical());
        }

        // Retrieve live stocks based on company, product PIDs, and stock locations
        List<Stock> livestocks =  stockCalculationRepository
               .findByCompanyProductAndOrderDateBetween(companyid,productPids,stocklocations);

        log.debug(thread + "current stock data : "+ livestocks.size());

        // Process each InventoryVoucherDetail
        for(InventoryVoucherDetail inventoryVoucherDetail : orderProducts.getInventoryVoucherDetails()){

            // Create a new Stock object
           Stock stock = new Stock();

            // Try to find an existing Stock object based on product and stock location
            Optional<Stock> optStock = livestocks
                   .stream()
                   .filter(data->data.getProductId().equals(inventoryVoucherDetail.getProduct().getId())
                           && data.getStockLocationPid().equals(inventoryVoucherDetail.getSourceStockLocation().getPid()))
                   .findAny();

           log.debug(thread + " Opening Stock is present   : " + optStock.isPresent());

            // Check if closingActual and closingLogical are enabled in stock calculation
           if(optionalStockCalculation.isPresent()
                   && optionalStockCalculation.get().getClosingActual()
                   && optionalStockCalculation.get().getClosingLogical()){

               log.debug(thread + " Actual  : "+optionalStockCalculation.get().getClosingActual());
               log.debug(thread + " Logical : "+optionalStockCalculation.get().getClosingLogical());

               // Existing Stock found; update its properties
               if(optStock.isPresent()){
                   // Use the existing Stock
                   stock = optStock.get();

                   log.debug(thread + " Existing Stock : " + stock.getProductName() + " New Stock : "  + inventoryVoucherDetail.getProduct().getName() );
                   log.debug(thread + " Existing Opening Stock  : " + stock.getOpeningQuantity());
                   log.debug(thread + " Existing Available Stock : " + stock.getAvilableQuantity());
                   log.debug(thread + " solid Stock  : " + inventoryVoucherDetail.getQuantity());

                   // Update Stock properties
                   stock.setSolidQuantity(stock.getSolidQuantity() + inventoryVoucherDetail.getQuantity() + inventoryVoucherDetail.getFreeQuantity());
                   stock.setAvilableQuantity(stock.getOpeningQuantity() - stock.getSolidQuantity());
                   stock.setProductName(inventoryVoucherDetail.getProduct().getName());
                   stock.setStockLocationPid(inventoryVoucherDetail.getSourceStockLocation().getPid());
                   stock.setStockLocationName(inventoryVoucherDetail.getSourceStockLocation().getName());
               }else{

                   log.debug(thread + "New stock : " +  inventoryVoucherDetail.getProduct().getId() +" : " +inventoryVoucherDetail.getProduct().getName());
                   log.debug(thread + "New Opening Stock  : " +inventoryVoucherDetail.getQuantity());

                   // Create a new Stock entry if not found
                   stock.setProductId(inventoryVoucherDetail.getProduct().getId());
                   stock.setProductPid(inventoryVoucherDetail.getProduct().getPid());
                   stock.setProductName(inventoryVoucherDetail.getProduct().getName());
                   stock.setSolidQuantity(0 + inventoryVoucherDetail.getQuantity());
                   stock.setDamageQuantity(0.0);
                   stock.setOpeningQuantity(0.0);
                   stock.setAvilableQuantity(stock.getOpeningQuantity() - stock.getSolidQuantity());
                   stock.setCreatedate(LocalDateTime.now());
                   stock.setLastModifiedDate(LocalDateTime.now());
                   stock.setStockLocationPid(inventoryVoucherDetail.getSourceStockLocation().getPid());
                   stock.setStockLocationName(inventoryVoucherDetail.getSourceStockLocation().getName());
                   stock.setCompany(companyid);
               }

           } else if (optionalStockCalculation.isPresent()
                   && optionalStockCalculation.get().getOpening()){

               log.debug(thread + " opening   : "+optionalStockCalculation.get().getOpening());

               // Check if an existing Stock is found
               if(optStock.isPresent()){
                   // Use the existing Stock
                   stock = optStock.get();

                   log.debug(thread + " Existing Stock : " + stock.getProductName() + " New Stock : "  + inventoryVoucherDetail.getProduct().getName() );
                   log.debug(thread + " Existing Opening Stock  : " + stock.getOpeningQuantity());
                   log.debug(thread + " Existing Available Stock : " + stock.getAvilableQuantity());
                   log.debug(thread + " solid Stock  : " + inventoryVoucherDetail.getQuantity());

                   // Update Stock properties
                   stock.setSolidQuantity(stock.getSolidQuantity() + inventoryVoucherDetail.getQuantity() + inventoryVoucherDetail.getFreeQuantity());
                   stock.setAvilableQuantity(stock.getOpeningQuantity());
                   stock.setProductName(inventoryVoucherDetail.getProduct().getName());
                   stock.setStockLocationName(inventoryVoucherDetail.getSourceStockLocation().getName());
               }else{

                   // Create a new Stock entry if not found
                   log.debug(thread + "New stock : " +  inventoryVoucherDetail.getProduct().getId() +" : " +inventoryVoucherDetail.getProduct().getName());
                   log.debug(thread + "New Opening Stock  : " +inventoryVoucherDetail.getQuantity());
                   stock.setProductId(inventoryVoucherDetail.getProduct().getId());
                   stock.setProductPid(inventoryVoucherDetail.getProduct().getPid());
                   stock.setProductName(inventoryVoucherDetail.getProduct().getName());
                   stock.setSolidQuantity(0 + inventoryVoucherDetail.getQuantity());
                   stock.setDamageQuantity(0.0);
                   stock.setOpeningQuantity(0.0);
                   stock.setAvilableQuantity(stock.getOpeningQuantity() - stock.getSolidQuantity());
                   stock.setCreatedate(LocalDateTime.now());
                   stock.setLastModifiedDate(LocalDateTime.now());
                   stock.setStockLocationPid(inventoryVoucherDetail.getSourceStockLocation().getPid());
                   stock.setStockLocationName(inventoryVoucherDetail.getSourceStockLocation().getName());
                   stock.setCompany(companyid);
               }
           }
           orderProductsList.add(stock);
       }

        log.debug(thread + " Order Size : " + orderProductsList.size());

        convertToJson(orderProductsList);
        List<Stock> result = stockCalculationRepository.save(orderProductsList);
        return result ;
    }
    /**
     * This method saves and updates stock data based on opening stock information.
     *
     * @param saveOpeningStocks A set of opening stock data to be used for updating or adding stock entries.
     * @param companyId         The ID of the company for which stock data is being managed.
     * @return A list of Stock objects representing the updated stock data.
     */
    @Override
    @Async
    public List<Stock> saveProductdstockdata(
            Set<OpeningStock> saveOpeningStocks,long companyId) {

        String Thread = "OPT-"+companyId + " : ";
        log.debug( Thread +"Entering saveProductdstockdata method : " + saveOpeningStocks.size());

        if(saveOpeningStocks.size() == 0){
            log.debug(Thread + "Exiting saveProductdstockdata By No Values Found : " + saveOpeningStocks.size());
            List<Stock> orderProductsList = new ArrayList<>();
            return orderProductsList ;
        }

        // Initialize a counter for updated stock entries.
        int updated_count_stock_count = 0;

        // Initialize a list to store updated stock data.
        List<Stock> orderProductsList = new ArrayList<>();

        log.debug(Thread + "current login company Id : " + companyId);

        // Extract stock location PIDs from the opening stock data.
        List<String> stockLocationPids = saveOpeningStocks
                .stream()
                .map(p -> p.getStockLocation().getPid())
                .distinct() // To remove duplicates
                .collect(Collectors.toList());

        // Fetch live stock data from the repository based on company,
        // product, and stock location.
        List<Stock>  livestocks =
                stockCalculationRepository
                        .findByCompanyProductAndOrderDateBetween(
                                companyId,stockLocationPids);

        log.debug(Thread + "stock Locations : " + stockLocationPids.toString());
        log.debug(Thread + "Live Stock : " + livestocks.size());

        // Iterate through the retrieved live stock entries.
        for(Stock liveStock : livestocks){

            // Create a reference to the live stock entry.
            Stock stock = liveStock;

            // Find the corresponding opening stock entry, if present.
            Optional<OpeningStock> optStock = saveOpeningStocks
                    .stream()
                    .filter(data->liveStock.getProductId()
                            .equals(data.getProductProfile().getId())
                            && liveStock.getStockLocationPid()
                            .equals(data.getStockLocation().getPid()))
                    .findAny();


            // Check if an opening stock entry was found.
            if(optStock.isPresent()){
                OpeningStock openingStock = optStock.get();
                System.out.println();
                log.debug("===========================opening stock Present ====================================");
                log.debug("-------------------------------------------------------------------------------------");
                log.debug(Thread + "stock.Product id       : " +  stock.getProductId());
                log.debug(Thread + "stock.Product pid      : " +  stock.getProductPid());
                log.debug(Thread + "stock.Product Name     : " +  stock.getProductName());
                log.debug(Thread + "stock.Opening Stock    : " + stock.getOpeningQuantity());
                log.debug(Thread + "stock.Available Stock  : " + stock.getAvilableQuantity());
                log.debug(Thread + "stock.damage Stock     : " + stock.getDamageQuantity());
                log.debug(Thread + "stock.solid Stock      : " + stock.getSolidQuantity());
                log.debug("-------------------------------------------------------------------------------------");
                log.debug("-------------------------------------------------------------------------------------");
                log.debug(Thread + "Opening Stock.Product id       : " +  openingStock.getProductProfile().getId());
                log.debug(Thread + "Opening Stock.Product pid      : " +  openingStock.getProductProfile().getPid());
                log.debug(Thread + "Opening Stock.Product Name     : " +  openingStock.getProductProfile().getName());
                log.debug(Thread + "Opening Stock.Opening Stock    : " +  openingStock.getQuantity());
                log.debug("-------------------------------------------------------------------------------------");
                log.debug("=====================================================================================");

                stock.setSolidQuantity(0.0);
                stock.setDamageQuantity(0.0);
                stock.setOpeningQuantity(openingStock.getQuantity());
                stock.setAvilableQuantity(openingStock.getQuantity());
                stock.setProductName(openingStock.getProductProfile().getName());
                stock.setLastModifiedDate(LocalDateTime.now());
                stock.setStockLocationName(openingStock.getStockLocation().getName());
                updated_count_stock_count = updated_count_stock_count + 1;

            } else{
                log.debug("==============================opening stock not present==============================");
                log.debug(Thread + "stock.Product id       : " +  stock.getProductId());
                log.debug(Thread + "stock.Product pid      : " +  stock.getProductPid());
                log.debug(Thread + "stock.Product Name     : " +  stock.getProductName());
                log.debug(Thread + "stock.Opening Stock    : " + stock.getOpeningQuantity());
                log.debug(Thread + "stock.Available Stock  : " + stock.getAvilableQuantity());
                log.debug(Thread + "stock.damage Stock     : " + stock.getDamageQuantity());
                log.debug(Thread + "stock.solid Stock      : " + stock.getSolidQuantity());
                log.debug("=====================================================================================");
                stock.setSolidQuantity(0.0);
                stock.setDamageQuantity(0.0);
                stock.setOpeningQuantity(0.0);
                stock.setAvilableQuantity(0.0);
                stock.setLastModifiedDate(LocalDateTime.now());
            }

            // Add the updated or reset stock entry to the list.
            orderProductsList.add(stock);
        }

        log.debug(Thread + " Existing Opening Stock Size : " + orderProductsList.size());
        log.debug(Thread + " updated_count_stock_count : " + updated_count_stock_count);


        // Iterate through the opening stock entries to add new stock entries, if needed.
        for(OpeningStock openingStock : saveOpeningStocks){
                Stock stock = new Stock();

            // Find the corresponding live stock entry, if present.
            Optional<Stock> optStock = livestocks
                        .stream()
                        .filter(data->data.getProductId()
                                .equals(openingStock.getProductProfile().getId())
                                && data.getStockLocationPid()
                                .equals(openingStock.getStockLocation().getPid()))
                        .findAny();

            // Check if there is no corresponding live stock entry.
            if(!optStock.isPresent()){

                log.debug("---------------------------------------new stock-----------------------------------------");
                log.debug(Thread + "Opening Stock.Product id       : " +  openingStock.getProductProfile().getId());
                log.debug(Thread + "Opening Stock.Product pid      : " +  openingStock.getProductProfile().getPid());
                log.debug(Thread + "Opening Stock.Product Name     : " +  openingStock.getProductProfile().getName());
                log.debug(Thread + "Opening Stock.Opening Stock    : " +  openingStock.getQuantity());
                log.debug("-----------------------------------------------------------------------------------------");

                stock.setProductId(openingStock.getProductProfile().getId());
                stock.setProductPid(openingStock.getProductProfile().getPid());
                stock.setProductName(openingStock.getProductProfile().getName());
                stock.setSolidQuantity(0.0);
                stock.setDamageQuantity(0.0);
                stock.setOpeningQuantity(openingStock.getQuantity());
                stock.setAvilableQuantity(openingStock.getQuantity());
                stock.setCreatedate(LocalDateTime.now());
                stock.setLastModifiedDate(LocalDateTime.now());
                stock.setStockLocationPid(openingStock.getStockLocation().getPid());
                stock.setStockLocationName(openingStock.getStockLocation().getName());
                stock.setCompany(companyId);
                // Add the new stock entry to the list.
                orderProductsList.add(stock);
            }
        }

        log.debug(Thread + " New Opening Stock Size : " + orderProductsList.size());

        convertToJson(orderProductsList);

        // Save the updated stock data to the repository and log the result.
        List<Stock> result =
                stockCalculationRepository
                        .save(orderProductsList);

        convertToJson(result);

        log.debug(Thread + "Exiting saveProductstockdata method.");
        return result;
    }
    /**
     * Asynchronously resets stock data for a specific product profile and stock location.
     *
     * This method resets the stock data for the specified product profile and stock location
     * by setting all quantity-related properties (solid, damage, opening, available) to zero.
     * It also updates the last modified date and saves the updated stock data.
     *
     * @param openingstock The OpeningStock object containing information about the product profile,
     *                     company, quantity, and stock location to be reset.
     * @return The Stock object representing the updated stock data after resetting.
     */
    @Override
    @Async
    public Stock deleteStockdata(OpeningStock openingstock) {
        log.debug("Reseting Stock Data : "
                + openingstock.getProductProfile().getName() + " : "
                + openingstock.getQuantity());
      Stock stock =
              stockCalculationRepository
                      .findByCompanyProductAndOrderDateBetween(
                              openingstock.getCompany().getId(),
                              openingstock.getProductProfile().getPid(),
                              openingstock.getStockLocation().getPid());
        stock.setSolidQuantity(0.0);
        stock.setDamageQuantity(0.0);
        stock.setOpeningQuantity(0.0);
        stock.setAvilableQuantity(0.0);
        stock.setLastModifiedDate(LocalDateTime.now());
        Stock result = stockCalculationRepository.save(stock);
        convertToJson(result);
        return result;
    }
    /**
     * Retrieves a list of live stock data for a specified company based on its ID.
     *
     * This method queries the stockCalculationRepository to fetch all live stock data
     * associated with the specified company ID.
     *
     * @param companyid The ID of the company for which live stock data is to be retrieved.
     * @return A list of Stock objects representing the live stock data for the specified company.
     */
    @Override
    public List<Stock> findAllLiveStocksByCompanyId(long companyid) {
        List<Stock>  LiveStock =
                stockCalculationRepository
                        .findAllStockByCompanyid(companyid);
        return LiveStock;
    }

    public ObjectMapper getObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        return mapper;
    }

    public <T> void convertToJson(Iterable<T> collection) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(collection);
            log.debug("json Data : " + jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void convertToJson(Object collection) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(collection);
            log.debug("json Data : " + jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
