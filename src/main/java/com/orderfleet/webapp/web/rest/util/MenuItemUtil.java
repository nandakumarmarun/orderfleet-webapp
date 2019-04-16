package com.orderfleet.webapp.web.rest.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public final  class MenuItemUtil {
	
	private static List<Long> common = Arrays.asList(1L,2L,11L,68L,69L,4L,37L,5L,39L,41L,42L,10L,66L,67L,9L,60L,61L,62L,65L,17L,126L,127L,14L,300305L,107L,100L,8L,52L,1942L,131L,132L,19L,20L,25L);
	
	private static List<Long> sales = Arrays.asList(12L,3L,6L,72L,74L,75L,76L,77L,78L,21L,26L,31L,32L,33L,34L,43L,44L,45L,46L,301625L,56L);
	
	private static List<Long> accountings = Arrays.asList(3L,21L,27L,104L);
	
	private static List<Long> dynamics = Arrays.asList(3L,21L,25L,28L,29L,30L,105L);
	
	private static List<Long> employee_user = Arrays.asList(19L,20L,131L,132L);
	
	//for orderpro users
	private static List<Long> custom_menu_items = Arrays.asList(9L,19L,20L,131L,132L,62L,64L,65L,6L,43L,44L,45L,46L);
	
	public static Set<Long> adminMenuItemIds(){
		Set<Long> menuItemIds = new HashSet<>();
		menuItemIds.addAll(common);
		menuItemIds.addAll(accountings);
		menuItemIds.addAll(sales);
		menuItemIds.addAll(dynamics);
		return menuItemIds;
		
	}
	
	public static Set<Long> managerMenuItemIds(){
		Set<Long> menuItemIds = new HashSet<>();
		menuItemIds.addAll(common);
		menuItemIds.addAll(accountings);
		menuItemIds.addAll(sales);
		menuItemIds.addAll(dynamics);
		return menuItemIds;
	}
	
	public static Set<Long> employeeUserMenuItemIds(){
		Set<Long> menuItemIds = new HashSet<>();
		menuItemIds.addAll(employee_user);
		return menuItemIds;
	}
	
	//for orderpro users
	public static Set<Long> customMenuItemIds(){
		Set<Long> menuItemIds = new HashSet<>();
		menuItemIds.addAll(custom_menu_items);
		return menuItemIds;
	}
}
