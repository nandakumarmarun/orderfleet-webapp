package com.orderfleet.webapp.domain.model;

import java.io.Serializable;

/**
 * This is not a normal entity. this is just a dependency for CompanySetting
 * persisted inside a JSON column
 * 
 * @author Shaheer
 * @since October 21, 2016
 * 
 * @see CompanySetting
 * @see ObjectType
 *
 */
public class SalesConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private boolean achievementSummaryTableEnabled = false;

	public SalesConfiguration() {
		super();
	}

	public boolean getAchievementSummaryTableEnabled() {
		return achievementSummaryTableEnabled;
	}

	public void setAchievementSummaryTableEnabled(boolean achievementSummaryTableEnabled) {
		this.achievementSummaryTableEnabled = achievementSummaryTableEnabled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (achievementSummaryTableEnabled ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SalesConfiguration other = (SalesConfiguration) obj;
		if (achievementSummaryTableEnabled != other.achievementSummaryTableEnabled)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SalesConfiguration {achievementSummaryTableEnabled=" + achievementSummaryTableEnabled + "}";
	}

}
