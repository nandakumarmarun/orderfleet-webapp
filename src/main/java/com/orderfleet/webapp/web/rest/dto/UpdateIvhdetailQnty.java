package com.orderfleet.webapp.web.rest.dto;

public class UpdateIvhdetailQnty {
 private Long id;
 private double qnty;
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public double getQnty() {
	return qnty;
}
public void setQnty(double qnty) {
	this.qnty = qnty;
}
@Override
public String toString() {
	return "UpdateIvhdetailQnty [id=" + id + ", qnty=" + qnty + "]";
}
}
