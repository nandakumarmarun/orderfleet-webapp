package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlab;

public class KmSlabDTO {
    private Long id;

    private String pid;

    private String slabName;

    private double minKm;

    private double maxKm;

    private double slabRate;

    private long companyId;

    public KmSlabDTO() {
    }

    public KmSlabDTO(KmSlab kmSlab) {
        this.id = kmSlab.getId();
        this.pid = kmSlab.getPid();
        this.slabName = kmSlab.getSlabName();
        this.minKm = kmSlab.getMinKm();
        this.maxKm = kmSlab.getMaxKm();
        this.slabRate = kmSlab.getAmount();
        this.companyId = kmSlab.getCompany().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSlabName() {
        return slabName;
    }

    public void setSlabName(String slabName) {
        this.slabName = slabName;
    }

    public double getMinKm() {
        return minKm;
    }

    public void setMinKm(double minKm) {
        this.minKm = minKm;
    }

    public double getMaxKm() {
        return maxKm;
    }

    public void setMaxKm(double maxKm) {
        this.maxKm = maxKm;
    }


    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public double getSlabRate() {
        return slabRate;
    }

    public void setSlabRate(double slabRate) {
        this.slabRate = slabRate;
    }

    @Override
    public String toString() {
        return "KmSlabDTO{" +
                "id=" + id +
                ", pid='" + pid + '\'' +
                ", slabName='" + slabName + '\'' +
                ", minKm=" + minKm +
                ", maxKm=" + maxKm +
                ", slabRate =" + slabRate +
                ", companyId='" + companyId + '\'' +
                '}';
    }
}
