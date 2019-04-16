package com.orderfleet.webapp.web.rest.dto;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TimeUtilizationView  implements Comparable<TimeUtilizationView> {

		private String date;
		private String unspecifiedTimeSpend;
		private String activityTimeSpend;

		public TimeUtilizationView() {
			super();
		}

		public TimeUtilizationView(String date, String unspecifiedTimeSpend, String activityTimeSpend) {
			super();
			this.date = date;
			this.unspecifiedTimeSpend = unspecifiedTimeSpend;
			this.activityTimeSpend = activityTimeSpend;
		}

		

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getUnspecifiedTimeSpend() {
			return unspecifiedTimeSpend;
		}

		public void setUnspecifiedTimeSpend(String unspecifiedTimeSpend) {
			this.unspecifiedTimeSpend = unspecifiedTimeSpend;
		}

		public String getActivityTimeSpend() {
			return activityTimeSpend;
		}

		public void setActivityTimeSpend(String activityTimeSpend) {
			this.activityTimeSpend = activityTimeSpend;
		}

		@Override
		public int compareTo(TimeUtilizationView o) {
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
			 return "TimeUtilizationView [date=" + date + ", unspecifiedTimeSpend=" + unspecifiedTimeSpend + ", activityTimeSpend=" + activityTimeSpend+"]";
		}

}