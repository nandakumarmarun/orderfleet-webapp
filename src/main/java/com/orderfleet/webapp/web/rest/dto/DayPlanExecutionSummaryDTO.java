package com.orderfleet.webapp.web.rest.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Sarath
 * @since Jan 28, 2017
 */
public class DayPlanExecutionSummaryDTO implements Comparable<DayPlanExecutionSummaryDTO> {

	private String Date;
	private Long scheduled;
	private Long achieved;
	private double percentage;
	private String alias;

	public DayPlanExecutionSummaryDTO() {
		super();
	}
	
	public DayPlanExecutionSummaryDTO(String date, Long scheduled, Long achieved, double percentage,String alias) {
		super();
		Date = date;
		this.scheduled = scheduled;
		this.achieved = achieved;
		this.percentage = percentage;
		this.alias=alias;
	}

	public DayPlanExecutionSummaryDTO(String date, Long scheduled, Long achieved, double percentage) {
		super();
		Date = date;
		this.scheduled = scheduled;
		this.achieved = achieved;
		this.percentage = percentage;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

	public Long getScheduled() {
		return scheduled;
	}

	public void setScheduled(Long scheduled) {
		this.scheduled = scheduled;
	}

	public Long getAchieved() {
		return achieved;
	}

	public void setAchieved(Long achieved) {
		this.achieved = achieved;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public int compareTo(DayPlanExecutionSummaryDTO o) {
		return getDate().compareTo(o.getDate());
	}

	//don't remove this
	@Override
	public String toString() {
		 ObjectMapper mapper = new ObjectMapper();
		 try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "DayPlanExecutionSummeryDTO [Date=" + Date + ", scheduled=" + scheduled + ", achieved=" + achieved+ ", percentage=" + percentage +"]";
	}
	
	
}
