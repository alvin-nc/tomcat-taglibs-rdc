package org.apache.taglibs.rdc.struts;

/**
 * The application bean for the sample mortgage application
 * 
 * @author Rahul
 *
 */
public class MortgageAppBean {
	
	// The member number of the user
	String memberNumber;
	// The multiple listing service number of the property
	String mlsNumber;
	// The value of the property with the given M.L.S. number
	String propertyValue;
	// The down payment for the mortgage
	String downPayment;
	// The transaction number of the escrow account transfer
	String transactionNum;
	// The result code for the escrow account transfer
	String resultCode;
	
	/*
	 * Constructor
	 */
	public MortgageAppBean() {
		this.memberNumber = null;
		this.mlsNumber = null;
		this.propertyValue = null;
		this.downPayment = null;
		this.transactionNum = null;
		this.resultCode = null;
	}	

	/**
	 * Gets the down payment for the mortgage
	 * 
	 * @return String the down payment
	 */
	public String getDownPayment() {
		return downPayment;
	}

	/**
	 * Gets the member number
	 * 
	 * @return String the member number
	 */
	public String getMemberNumber() {
		return memberNumber;
	}

	/**
	 * Gets the multiple listing service number
	 * 
	 * @return String the MLS number
	 */
	public String getMlsNumber() {
		return mlsNumber;
	}

	/**
	 * Gets the property value
	 * 
	 * @return String the property value
	 */
	public String getPropertyValue() {
		return propertyValue;
	}
	
	/**
	 * Gets the property value
	 * 
	 * @return String the property value
	 */
	public String getTransactionNum() {
		return transactionNum;
	}
		
	/**
	 * Gets the result code
	 * 
	 * @return String the result code
	 */
	public String getResultCode() {
		return resultCode;
	}
	
	/**
	 * Sets the down payment for the mortgage
	 *
	 * @param String the new down payment
	 */	
	public void setDownPayment(String string) {
		downPayment = string;
	}

	/**
	 * Sets the member number
	 *
	 * @param String the new member number
	 */
	
	public void setMemberNumber(String string) {
		memberNumber = string;
	}
	
	/**
	 * Sets the multiple listing service number
	 *
	 * @param String the new MLS number
	 */
	public void setMlsNumber(String string) {
		mlsNumber = string;
	}
	
	/**
	 * Sets the property value
	 *
	 * @param String the new property value
	 */
	public void setPropertyValue(String string) {
		propertyValue = string;
	}
	
	/**
	 * Sets the transaction number
	 *
	 * @param String the new transaction number
	 */
	public void setTransactionNum(String string) {
		transactionNum = string;
	}

	/**
	 * Sets the result code
	 *
	 * @param String the new result code
	 */
	public void setResultCode(String string) {
		resultCode = string;
	}

}
