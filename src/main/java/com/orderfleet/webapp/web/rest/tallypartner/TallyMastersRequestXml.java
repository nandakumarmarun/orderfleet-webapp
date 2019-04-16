package com.orderfleet.webapp.web.rest.tallypartner;

/**
 * a tally master request xml.
 *
 * @author Sarath
 * @since Feb 13, 2018
 *
 */
public class TallyMastersRequestXml {

	public static String getCompanyNamesXml() {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<ENVELOPE><HEADER><VERSION>1</VERSION><TALLYREQUEST>Export</TALLYREQUEST><TYPE>Data</TYPE><ID>List of Company</ID></HEADER><BODY><DESC><STATICVARIABLES><EXPLODEFLAG>Yes</EXPLODEFLAG><SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT></STATICVARIABLES><TDL><TDLMESSAGE><REPORT NAME=\"List of Company\" ><FORMS>List of Company</FORMS></REPORT><FORM NAME =\"List of Company\" ><PARTS>List of Company</PARTS></FORM><PART NAME=\"List of Company\" ><LINES>List of Company</LINES><REPEAT>List of Company : Collection of Company</REPEAT><SCROLLED>Vertical</SCROLLED></PART><LINE NAME=\"List of Company\" ><XMLTAG>Company</XMLTAG><Fields>GUID</Fields><Fields>Name</Fields></LINE><FIELD NAME=\"GUID\" ><USE>Name Field</USE><SET>$$String:$GUID</SET></FIELD><FIELD NAME=\"Name\" ><USE>Name Field</USE><SET>$$String:$Name</SET></FIELD><COLLECTION NAME=\"Collection of Company\"><TYPE>Company</TYPE><fetch>*</fetch></COLLECTION></TDLMESSAGE></TDL></DESC></BODY></ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyStockGroupsXml(String companyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<ENVELOPE><HEADER><VERSION>1</VERSION><TALLYREQUEST>Export</TALLYREQUEST><TYPE>Data</TYPE><ID>List of Stock Groups</ID></HEADER><BODY><DESC><STATICVARIABLES><EXPLODEFLAG>Yes</EXPLODEFLAG><SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT><SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append(
				"</SVCURRENTCOMPANY></STATICVARIABLES><TDL><TDLMESSAGE><REPORT NAME=\"List of Stock Groups\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><FORMS>List of Stock Groups</FORMS></REPORT><FORM NAME=\"List of Stock Groups\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><TOPPARTS>List of Stock Groups</TOPPARTS> <XMLTAG>List of Stock Groups</XMLTAG></FORM><PART NAME=\"List of Stock Groups\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><TOPLINES>List of Stock Groups</TOPLINES> <REPEAT>List of Stock Groups : Collection of Stock Groups</REPEAT> <SCROLLED>Vertical</SCROLLED> </PART><LINE NAME=\"List of Stock Groups\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><LEFTFIELDS>List of Stock Groups</LEFTFIELDS> </LINE><FIELD NAME=\"List of Stock Groups\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><SET>$Name</SET> <XMLTAG>\"NAME\"</XMLTAG></FIELD><COLLECTION NAME=\"Collection of Stock Groups\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><TYPE>Stock Group</TYPE> </COLLECTION></TDLMESSAGE></TDL></DESC></BODY></ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyStockCategoryXml(String companyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<ENVELOPE><HEADER><VERSION>1</VERSION><TALLYREQUEST>Export</TALLYREQUEST><TYPE>Data</TYPE><ID>List of Stock Categories</ID></HEADER><BODY><DESC><STATICVARIABLES><EXPLODEFLAG>Yes</EXPLODEFLAG><SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT><SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append(
				"</SVCURRENTCOMPANY></STATICVARIABLES><TDL><TDLMESSAGE><REPORT NAME=\"List of Stock Categories\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><FORMS>List of Stock Categories</FORMS></REPORT><FORM NAME=\"List of Stock Categories\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><TOPPARTS>List of Stock Categories</TOPPARTS><XMLTAG>\"List of Stock Categories\"</XMLTAG></FORM><PART NAME=\"List of Stock Categories\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><TOPLINES>List of Stock Categories</TOPLINES><REPEAT>List of Stock Categories : Collection of Stock Categories</REPEAT><SCROLLED>Vertical</SCROLLED></PART><LINE NAME=\"List of Stock Categories\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><LEFTFIELDS>List of Stock Categories</LEFTFIELDS><FIELDS>Field Parent Stock Category</FIELDS></LINE><FIELD NAME=\"List of Stock Categories\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><SET>$Name</SET><XMLTAG>\"NAME\"</XMLTAG></FIELD><FIELD NAME=\"Field Parent Stock Category\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><SET>$Parent</SET><XMLTAG>PARENT</XMLTAG></FIELD><COLLECTION NAME=\"Collection of Stock Categories\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><TYPE>Stock Category</TYPE><NATIVEMETHOD>Parent</NATIVEMETHOD></COLLECTION></TDLMESSAGE></TDL></DESC></BODY></ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyAccountGroupsXml(String companyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<ENVELOPE><HEADER><VERSION>1</VERSION><TALLYREQUEST>Export</TALLYREQUEST><TYPE>Data</TYPE><ID>List of Groups</ID></HEADER><BODY><DESC><STATICVARIABLES><EXPLODEFLAG>Yes</EXPLODEFLAG><SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT><SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append(
				"</SVCURRENTCOMPANY></STATICVARIABLES><TDL><TDLMESSAGE><REPORT NAME=\"List of Groups\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><FORMS>List of Groups</FORMS></REPORT><FORM NAME=\"List of Groups\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><TOPPARTS>List of Groups</TOPPARTS><XMLTAG>List of Groups</XMLTAG></FORM><PART NAME=\"List of Groups\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><TOPLINES>Line Groups</TOPLINES><REPEAT>Line Groups : Collection of Groups</REPEAT><SCROLLED>Vertical</SCROLLED></PART><LINE NAME=\"Line Groups\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><XMLTAG>Groups</XMLTAG><FIELDS>Field Name Groups</FIELDS><FIELDS>Field Parent Groups</FIELDS></LINE><FIELD NAME=\"Field Name Groups\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><SET>$Name</SET><XMLTAG>NAME</XMLTAG></FIELD><FIELD NAME=\"Field Parent Groups\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><SET>$Parent</SET><XMLTAG>PARENT</XMLTAG></FIELD><COLLECTION NAME=\"Collection of Groups\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><TYPE>Groups</TYPE><NATIVEMETHOD>Parent</NATIVEMETHOD><NATIVEMETHOD>Name</NATIVEMETHOD></COLLECTION></TDLMESSAGE></TDL></DESC></BODY></ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyLedgersXmlUsingFetchWithFullAddress(String companyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<ENVELOPE> <HEADER> <VERSION>1</VERSION> <TALLYREQUEST>Export</TALLYREQUEST> <TYPE>Collection</TYPE> <ID>All Ledgers</ID> </HEADER> <BODY> <DESC> <STATICVARIABLES> <SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append(
				"</SVCURRENTCOMPANY> <SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT> </STATICVARIABLES> <TDL> <TDLMESSAGE> <COLLECTION NAME=\"All Ledgers\" ISMODIFY=\"No\"> <TYPE>Ledgers</TYPE> <FETCH>NAME,PARENT,ADDRESS,TAXTYPE,PRICELEVEL,SUBTAXTYPE</FETCH> </COLLECTION> </TDLMESSAGE> </TDL> </DESC> </BODY> </ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyVatLedgersXml(String companyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<ENVELOPE><HEADER><VERSION>1</VERSION><TALLYREQUEST>Export</TALLYREQUEST><TYPE>Data</TYPE><ID>List of Ledgers</ID></HEADER><BODY><DESC><STATICVARIABLES><EXPLODEFLAG>Yes</EXPLODEFLAG><SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT><SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append(
				"</SVCURRENTCOMPANY></STATICVARIABLES><TDL><TDLMESSAGE><REPORT NAME=\"List of Ledgers\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><FORMS>List of Ledgers</FORMS></REPORT><FORM NAME=\"List of Ledgers\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><TOPPARTS>List of Ledgers</TOPPARTS><XMLTAG>List of Ledgers</XMLTAG></FORM><PART NAME=\"List of Ledgers\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><TOPLINES>Line Ledgers</TOPLINES><REPEAT>Line Ledgers : Collection of Ledgers</REPEAT><SCROLLED>Vertical</SCROLLED></PART><LINE NAME=\"Line Ledgers\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><XMLTAG>Ledger</XMLTAG><FIELDS>Field Name Ledger</FIELDS><FIELDS>Field Parent Ledger</FIELDS><FIELDS>Field Address Ledger</FIELDS><FIELDS>Field TAXTYPE Ledger</FIELDS><FIELDS>Field SUBTAXTYPE Ledger</FIELDS><FIELDS>Field TAXCLASSIFICATIONNAME Ledger</FIELDS><FIELDS>Field VATCLASSIFICATIONRATE Ledger</FIELDS><FIELDS>Field PRICELEVEL</FIELDS></LINE><FIELD NAME=\"Field Name Ledger\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><SET>$Name</SET><XMLTAG>NAME</XMLTAG></FIELD><FIELD NAME=\"Field Parent Ledger\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><SET>$Parent</SET><XMLTAG>PARENT</XMLTAG></FIELD><FIELD NAME=\"Field Address Ledger\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><SET>$Address</SET><XMLTAG>Address</XMLTAG></FIELD><FIELD NAME=\"Field TAXTYPE Ledger\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><SET>$TAXTYPE</SET><XMLTAG>TAXTYPE</XMLTAG></FIELD><FIELD NAME=\"Field SUBTAXTYPE Ledger\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><SET>$SUBTAXTYPE</SET><XMLTAG>SUBTAXTYPE</XMLTAG></FIELD><FIELD NAME=\"Field TAXCLASSIFICATIONNAME Ledger\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><SET>$TAXCLASSIFICATIONNAME</SET><XMLTAG>TAXCLASSIFICATIONNAME</XMLTAG></FIELD><FIELD NAME=\"Field VATCLASSIFICATIONRATE Ledger\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><SET>$VATCLASSIFICATIONRATE</SET><XMLTAG>VATCLASSIFICATIONRATE</XMLTAG></FIELD><FIELD NAME=\"Field PRICELEVEL\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><SET>$PRICELEVEL</SET><XMLTAG>PRICELEVEL</XMLTAG></FIELD><COLLECTION NAME=\"Collection of Ledgers\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><TYPE>Ledger</TYPE><FILTERS>ofSpecificVchs</FILTERS></COLLECTION><SYSTEM TYPE=\"FORMULAE\" NAME=\"ofSpecificVchs\">($PARENT = \"Duties &amp; Taxes\")</SYSTEM></TDLMESSAGE></TDL></DESC></BODY></ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyStockItemsXml(String companyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				" <ENVELOPE> <HEADER> <VERSION>1</VERSION> <TALLYREQUEST>Export</TALLYREQUEST> <TYPE>Data</TYPE> <ID>List of Stock Items</ID> </HEADER> <BODY> <DESC> <STATICVARIABLES> <EXPLODEFLAG>Yes</EXPLODEFLAG> <SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT> <SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append(
				"</SVCURRENTCOMPANY> </STATICVARIABLES> <TDL> <TDLMESSAGE> <REPORT NAME=\"List of Stock Items\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <FORMS>List of Stock Items</FORMS> </REPORT> <FORM NAME=\"List of Stock Items\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <TOPPARTS>List of Stock Items</TOPPARTS> <XMLTAG>List of Stock Items</XMLTAG> </FORM> <PART NAME=\"List of Stock Items\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <TOPLINES>List of Stock Items</TOPLINES> <REPEAT>List of Stock Items : Collection of Stock Items</REPEAT> <SCROLLED>Vertical</SCROLLED> </PART> <LINE NAME=\"List of Stock Items\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <XMLTAG>Stock Item</XMLTAG> <Fields>Stock Item Name Field</Fields> <Fields>Stock Item Parent Field</Fields> <Fields>Stock Item Category Field</Fields> <Fields>Stock Item Mrp Field</Fields> <Fields>Stock Item Rate Field</Fields> <Fields>Stock Item StandardPrice Field</Fields> <Fields>Stock Item RateOfVat Field</Fields> <Fields>Stock Item BaseUnits Field</Fields> <Fields>Stock Item AdditionalUnits Field</Fields> <Fields>Stock Item Conversion Field</Fields> <Fields>Stock Item Denominator Field</Fields><Fields>Stock Item IsBatchWiseOn Field</Fields> </LINE> <FIELD NAME=\"Stock Item Name Field\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <USE>Name Field</USE> <SET>$Name</SET> <XMLTAG>NAME</XMLTAG> </FIELD> <FIELD NAME=\"Stock Item Parent Field\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <USE>Parent Field</USE> <SET>$Parent</SET> <XMLTAG>Group</XMLTAG> </FIELD> <FIELD NAME=\"Stock Item Category Field\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <USE>Category Field</USE> <SET>$Category</SET> <XMLTAG>Category</XMLTAG> </FIELD> <FIELD NAME=\"Stock Item Mrp Field\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <USE>RateOfMrp Field</USE> <SET>$RateOfMrp</SET> <XMLTAG>Mrp</XMLTAG> </FIELD> <FIELD NAME=\"Stock Item Rate Field\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <USE>OpeningRate Field</USE> <SET>$OpeningRate</SET> <XMLTAG>Rate</XMLTAG> </FIELD> <FIELD NAME=\"Stock Item StandardPrice Field\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <USE>StandardPrice Field</USE> <SET>$StandardPrice</SET> <XMLTAG>StandardPrice</XMLTAG> </FIELD> <FIELD NAME=\"Stock Item RateOfVat Field\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <USE>RateOfVat Field</USE> <SET>$RateOfVat</SET> <XMLTAG>RateOfVat</XMLTAG> </FIELD> <FIELD NAME=\"Stock Item BaseUnits Field\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <USE>BaseUnits Field</USE> <SET>$BaseUnits</SET> <XMLTAG>BaseUnits</XMLTAG> </FIELD> <FIELD NAME=\"Stock Item AdditionalUnits Field\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <USE>AdditionalUnits Field</USE> <SET>$AdditionalUnits</SET> <XMLTAG>AdditionalUnits</XMLTAG> </FIELD> <FIELD NAME=\"Stock Item Conversion Field\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <USE>Conversion Field</USE> <SET>$Conversion</SET> <XMLTAG>Conversion</XMLTAG> </FIELD> <FIELD NAME=\"Stock Item Denominator Field\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <USE>Denominator Field</USE> <SET>$Denominator</SET> <XMLTAG>Denominator</XMLTAG> </FIELD> <FIELD NAME=\"Stock Item IsBatchWiseOn Field\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"><USE>IsBatchWiseOn Field</USE><SET>$IsBatchWiseOn</SET><XMLTAG>IsBatchWiseOn</XMLTAG></FIELD><COLLECTION NAME=\"Collection of Stock Items\" ISMODIFY=\"No\" ISFIXED=\"No\" ISINITIALIZE=\"No\" ISOPTION=\"No\" ISINTERNAL=\"No\"> <TYPE>Stock Item</TYPE> </COLLECTION> </TDLMESSAGE> </TDL> </DESC> </BODY> </ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyStockItemsWithVatXml(String companyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				" <ENVELOPE><HEADER><VERSION>1</VERSION><TALLYREQUEST>Export</TALLYREQUEST><TYPE>Collection</TYPE><ID>All Stock Items</ID></HEADER><BODY><DESC><STATICVARIABLES><SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append(
				"</SVCURRENTCOMPANY><SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT></STATICVARIABLES><TDL><TDLMESSAGE><COLLECTION NAME = \"All Stock Items\"ISMODIFY=\"No\"><TYPE>stock item</TYPE><FETCH>name,parent,saleslist.list</FETCH></COLLECTION></TDLMESSAGE></TDL></DESC></BODY></ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyStockSummaryBatchWiseXml(String companyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<ENVELOPE><HEADER><VERSION>1</VERSION><TALLYREQUEST>Export</TALLYREQUEST><TYPE>Data</TYPE><ID>Stock Summary</ID></HEADER><BODY><DESC><STATICVARIABLES><EXPLODEFLAG>Yes</EXPLODEFLAG><SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT><SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append(
				"</SVCURRENTCOMPANY><IsItemWise>YES</IsItemWise></STATICVARIABLES><TDL><TDLMESSAGE><COLLECTION NAME=\"Collection of StockItem\" ISMODIFY=\"No\"><TYPE>Collection</TYPE><FETCH>BatchAllocations.*</FETCH></COLLECTION></TDLMESSAGE></TDL> </DESC></BODY></ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyPriceLevelsXml(String companyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<ENVELOPE><HEADER><VERSION>1</VERSION><TALLYREQUEST>Export</TALLYREQUEST><TYPE>Collection</TYPE><ID>All items under Groups</ID></HEADER><BODY><DESC><STATICVARIABLES><SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append(
				"</SVCURRENTCOMPANY><SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT></STATICVARIABLES><TDL><TDLMESSAGE><COLLECTION NAME=\"All items under Groups\" ISMODIFY=\"No\"><TYPE>stock item</TYPE><FETCH>FullPriceList,Name</FETCH></COLLECTION></TDLMESSAGE></TDL></DESC></BODY></ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyBillReceivableXml(String companyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<ENVELOPE><HEADER><VERSION>1</VERSION><TALLYREQUEST>Export</TALLYREQUEST><TYPE>Data</TYPE><ID>Bills Receivable</ID></HEADER><BODY><DESC><STATICVARIABLES><SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT><SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append("</SVCURRENTCOMPANY></STATICVARIABLES></DESC></BODY></ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyBillPayablesXml(String companyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<ENVELOPE><HEADER><VERSION>1</VERSION><TALLYREQUEST>Export</TALLYREQUEST><TYPE>Data</TYPE><ID>Bills Payable</ID></HEADER><BODY><DESC><STATICVARIABLES><SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT><SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append("</SVCURRENTCOMPANY></STATICVARIABLES></DESC></BODY></ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyTrialBalanceXml(String companyName) {

		StringBuilder builder = new StringBuilder();
		builder.append(
				"<ENVELOPE> <HEADER> <VERSION>1</VERSION> <TALLYREQUEST>Export</TALLYREQUEST> <TYPE>Data</TYPE> <ID>Simple Trial balance</ID> </HEADER> <BODY> <DESC> <STATICVARIABLES> <EXPLODEFLAG>Yes</EXPLODEFLAG> <SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT> <SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append(
				"</SVCURRENTCOMPANY> </STATICVARIABLES> <TDL> <TDLMESSAGE> <REPORT NAME=\"Simple Trial balance\"> <FORMS>Simple Trial balance</FORMS> <TITLE>Trial Balance</TITLE> </REPORT> <FORM NAME =\"Simple Trial balance\"> <TOPPARTS>Simple TB Part</TOPPARTS> <HEIGHT>100% Page</HEIGHT> <WIDTH>100% Page</WIDTH> </FORM> <PART NAME=\"Simple TB part\"> <TOPLINES>Simple TB Title,Simple TB Details</TOPLINES> <REPEAT>Simple TB Details : Simple TB Ledgers</REPEAT> <SCROLLED>Vertical</SCROLLED> <COMMONBORDERS>Yes</COMMONBORDERS> </PART> <LINE NAME=\"Simple TB Title\"> <USE>Simple TB Details</USE> <LOCAL>Field : Default : Type : String</LOCAL> <LOCAL>Field : Default : Align : Centre</LOCAL> <LOCAL>Field : Simple TB Name Field : Set as : \"Particulars\"</LOCAL> <LOCAL>Field : Simple TB Amount Field : Set as : \"Amount\"</LOCAL> <BORDER>Flush Totals</BORDER> </LINE> <LINE NAME=\"Simple TB Details\"> <LEFTFIELDS>Simple TB Name Field</LEFTFIELDS> <RIGHTFIELDS>Simple TB Amount Field</RIGHTFIELDS> </LINE> <FIELD NAME=\"Simple TB Name Field\"> <USE>Name Field</USE> <SET>$Name</SET> </FIELD> <FIELD NAME=\"Simple TB Amount Field\"> <USE>Amount Field</USE> <SET>$ClosingBalance</SET> <Border>Thin Left</Border> </FIELD> <COLLECTION ISFIXED=\"No\" ISINITIALIZE=\"No\" ISINTERNAL=\"No\" ISMODIFY=\"No\" ISOPTION=\"No\" NAME=\"Simple TB Ledgers\"> <TYPE>Ledger</TYPE> <FILTERS>dateSearch</FILTERS> </COLLECTION> <SYSTEM NAME=\"dateSearch\" TYPE=\"Formulae\"> Not $$IsLedgerProfit</SYSTEM> </TDLMESSAGE> </TDL> </DESC> </BODY> </ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyGSTProductGroupWiseXml(String companyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<ENVELOPE> <HEADER> <VERSION>1</VERSION> <TALLYREQUEST>Export</TALLYREQUEST> <TYPE>Data</TYPE> <ID>GST RATE SETUP</ID> </HEADER> <BODY> <DESC> <STATICVARIABLES> <SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append(
				"</SVCURRENTCOMPANY><SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT> </STATICVARIABLES> </DESC> </BODY> </ENVELOPE>");
		return builder.toString();
	}

	public static String getCompanyStockItemGSTRateXml(String companyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<ENVELOPE> <HEADER> <VERSION>1</VERSION> <TALLYREQUEST>Export</TALLYREQUEST> <TYPE>Collection</TYPE> <ID>All Stock Items</ID> </HEADER> <BODY> <DESC> <STATICVARIABLES> <SVCURRENTCOMPANY>");
		builder.append(companyName);
		builder.append(
				"</SVCURRENTCOMPANY> <SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT> </STATICVARIABLES> <TDL> <TDLMESSAGE> <COLLECTION NAME = \"All Stock Items\"ISMODIFY=\"No\"> <TYPE>stock item</TYPE> <FETCH>name,parent,GSTDETAILS.LIST</FETCH> </COLLECTION> </TDLMESSAGE> </TDL> </DESC> </BODY> </ENVELOPE>");
		return builder.toString();
	}
}
