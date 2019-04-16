package com.orderfleet.webapp.geolocation.model;

import java.util.ArrayList;
import java.util.List;

public class GTowerRequest {

	private List<CellTower> cellTowers = new ArrayList<>();

	public List<CellTower> getCellTowers() {
		return cellTowers;
	}

	public void setCellTowers(List<CellTower> cellTowers) {
		this.cellTowers = cellTowers;
	}

}
