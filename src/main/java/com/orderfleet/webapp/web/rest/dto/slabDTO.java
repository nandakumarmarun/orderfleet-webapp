package com.orderfleet.webapp.web.rest.dto;

public class slabDTO {

        private String pid;

        private Long id;

        private int minimumUser;

        private int maximumUser;

        private double slabRate;

        private String companyPid;

        private String companyLegalName;


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public int getMinimumUser() {
            return minimumUser;
        }

        public void setMinimumUser(int minimumUser) {
            this.minimumUser = minimumUser;
        }

        public int getMaximumUser() {
            return maximumUser;
        }

        public void setMaximumUser(int maximumUser) {
            this.maximumUser = maximumUser;
        }

        public double getSlabRate() {
            return slabRate;
        }

        public void setSlabRate(double slabRate) {
            this.slabRate = slabRate;
        }

        public String getCompanyPid() {
        return companyPid;
        }

        public void setCompanyPid(String companyPid) {
        this.companyPid = companyPid;
     }

    public String getCompanyLegalName() {
        return companyLegalName;
    }

    public void setCompanyLegalName(String companyLegalName) {
        this.companyLegalName = companyLegalName;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
        public String toString() {
            return "slab{" +
                    "id=" + id +
                    ", minimumUser=" + minimumUser +
                    ", maximumUser=" + maximumUser +
                    ", slabRate=" + slabRate +
                    '}';
        }
    }

