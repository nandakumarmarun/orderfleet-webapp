package com.orderfleet.webapp.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_stock")
public class Stock {

    @Id
    @GenericGenerator(name = "seq_stock_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "seq_stock_id") })
    @GeneratedValue(generator = "seq_stock_id_GEN")
    @Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_stock_id')")
    private Long id;


    @Column(name = "product_id")
    private long productId;

    @Column(name = "product_pid")
    private String productPid;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "sold_quantity")
    private double soldQuantity;

    @Column(name = "opening_quantity")
    private double openingQuantity;

    @Column(name = "damage_quantity")
    private double damageQuantity;

    @Column(name = "avilable_quantity")
    private double avilableQuantity;

    @Column(name = "created_date")
    private LocalDateTime createdate;


    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "source_stock_location_pid")
    private String StockLocationPid;

    @Column(name = "source_stock_location_name")
    private String StockLocationName;
    @NotNull
    @Column(name = "company_id")
    private long company;

    @Column(name = "batch_no")
    private  String  batchNo;


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductPid() {
        return productPid;
    }

    public void setProductPid(String productPid) {
        this.productPid = productPid;
    }

    public double getSolidQuantity() {
        return soldQuantity;
    }

    public void setSolidQuantity(double soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public LocalDateTime getCreatedate() {
        return createdate;
    }

    public void setCreatedate(LocalDateTime createdate) {
        this.createdate = createdate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getStockLocationPid() {
        return StockLocationPid;
    }

    public void setStockLocationPid(String stockLocationPid) {
        StockLocationPid = stockLocationPid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCompany() {
        return company;
    }

    public void setCompany(long company) {
        this.company = company;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public double getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(double soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public double getOpeningQuantity() {
        return openingQuantity;
    }

    public void setOpeningQuantity(double openingQuantity) {
        this.openingQuantity = openingQuantity;
    }

    public double getDamageQuantity() {
        return damageQuantity;
    }

    public void setDamageQuantity(double damageQuantity) {
        this.damageQuantity = damageQuantity;
    }

    public double getAvilableQuantity() {
        return avilableQuantity;
    }

    public void setAvilableQuantity(double avilableQuantity) {
        this.avilableQuantity = avilableQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStockLocationName() {
        return StockLocationName;
    }

    public void setStockLocationName(String stockLocationName) {
        StockLocationName = stockLocationName;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", productId=" + productId +
                ", productPid='" + productPid + '\'' +
                ", productName='" + productName + '\'' +
                ", soldQuantity=" + soldQuantity +
                ", openingQuantity=" + openingQuantity +
                ", damageQuantity=" + damageQuantity +
                ", avilableQuantity=" + avilableQuantity +
                ", createdate=" + createdate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", StockLocationPid='" + StockLocationPid + '\'' +
                ", StockLocationName='" + StockLocationName + '\'' +
                ", company=" + company +
                ", batchNo='" + batchNo + '\'' +
                '}';
    }
}
