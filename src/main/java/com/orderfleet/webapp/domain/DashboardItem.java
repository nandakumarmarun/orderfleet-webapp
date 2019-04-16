package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.DashboardItemConfigType;
import com.orderfleet.webapp.domain.enums.DashboardItemType;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.TaskPlanType;

/**
 * A DashboardItem
 * 
 * @author Muhammed Riyas T
 * @since Jan 18, 2017
 */
@Entity
@Table(name = "tbl_dashboard_item")
public class DashboardItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_dashboard_item_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dashboard_item_id") })
	@GeneratedValue(generator = "seq_dashboard_item_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dashboard_item_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "dashboard_item_type", nullable = false)
	private DashboardItemType dashboardItemType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "dashboard_item_config_type", nullable = false, columnDefinition = "varchar(255) default 'DASHBOARD'")
	private DashboardItemConfigType dashboardItemConfigType = DashboardItemConfigType.DASHBOARD;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "task_plan_type", nullable = false)
	private TaskPlanType taskPlanType;

	@Enumerated(EnumType.STRING)
	@Column(name = "document_type")
	private DocumentType documentType;

	@ManyToMany
	@JoinTable(name = "tbl_dashboard_item_activity", joinColumns = @JoinColumn(name = "dashboard_item_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"))
	private Set<Activity> activities = new HashSet<>();
	
	@ManyToMany
	@JoinTable(name = "tbl_dashboard_item_document", joinColumns = @JoinColumn(name = "dashboard_item_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "document_id", referencedColumnName = "id"))
	private Set<Document> documents = new HashSet<>();

	@ManyToMany
	@JoinTable(name = "tbl_dashboard_product_group", joinColumns = @JoinColumn(name = "dashboard_item_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "product_group_id", referencedColumnName = "id"))
	private Set<ProductGroup> productGroups = new HashSet<>();
	
	@ManyToOne
	private SalesTargetGroup salesTargetGroup;
	
	@ManyToOne
	private SalesTargetBlock salesTargetBlock;

	@NotNull
	@ManyToOne
	private Company company;
	
	@Column(name = "sort_order", nullable = false, columnDefinition = "int DEFAULT 0")
	private int sortOrder;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DashboardItemType getDashboardItemType() {
		return dashboardItemType;
	}

	public void setDashboardItemType(DashboardItemType dashboardItemType) {
		this.dashboardItemType = dashboardItemType;
	}

	public TaskPlanType getTaskPlanType() {
		return taskPlanType;
	}

	public void setTaskPlanType(TaskPlanType taskPlanType) {
		this.taskPlanType = taskPlanType;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public Set<Activity> getActivities() {
		return activities;
	}

	public void setActivities(Set<Activity> activities) {
		this.activities = activities;
	}

	public Set<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<Document> documents) {
		this.documents = documents;
	}

	public Set<ProductGroup> getProductGroups() {
		return productGroups;
	}

	public void setProductGroups(Set<ProductGroup> productGroups) {
		this.productGroups = productGroups;
	}
	
	public SalesTargetGroup getSalesTargetGroup() {
		return salesTargetGroup;
	}

	public void setSalesTargetGroup(SalesTargetGroup salesTargetGroup) {
		this.salesTargetGroup = salesTargetGroup;
	}

	public SalesTargetBlock getSalesTargetBlock() {
		return salesTargetBlock;
	}

	public void setSalesTargetBlock(SalesTargetBlock salesTargetBlock) {
		this.salesTargetBlock = salesTargetBlock;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public DashboardItemConfigType getDashboardItemConfigType() {
		return dashboardItemConfigType;
	}

	public void setDashboardItemConfigType(DashboardItemConfigType dashboardItemConfigType) {
		this.dashboardItemConfigType = dashboardItemConfigType;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

}
