package websense.httpgetpostauto.po;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import websense.httpgetpostauto.util.Log4j;

public class Po_TritonWseSettingsHybridUnFilteredDes {
	
	private By addButtonBy = By.id("hostedNonProxiedForm:addButton");
	private By deleButtonBy = By.id("hostedNonProxiedForm:deleteButton");
	private By okButtonBy = By.id("idToolbarFormFacet:idActionApplyProxy");
	private By unfilteredDestTableBy = By.id("hostedNonProxiedForm:idConnectionTable");
	private By deleConfirmBy = By.id("hostedNonProxiedForm:idConfirmDeleteOKButton");
	private By deleConfCancelBy=By.id("hostedNonProxiedForm:idConfirmDeleteCancelButton");
	private By deleConfTextBy=By.id("hostedNonProxiedForm:confirmDeleteText");
	private By selectAllBy=By.cssSelector("img[id='rowIndex']");
	
	// define driver
	private WebDriver driver;
	private WebDriverWait driverWait;
	// Log4j
	private Logger log4jLogger;

	public Po_TritonWseSettingsHybridUnFilteredDes(WebDriver driver) {
		this.driver=driver;
		this.driverWait=new WebDriverWait(driver,15);
		this.log4jLogger=Log4j.logger(Po_TritonWseSettingsHybridUnFilteredDes.class.getName());
		
		driverWait.until(ExpectedConditions.presenceOfElementLocated(addButtonBy));
	}
	
	
	/**
	 * click add button to go to add unfiltered destination page 
	 */
	public Po_TritonWseSettingsHybridUnFilteredDes_AddEdit clickAddButton(){
		driver.findElement(addButtonBy).click();
		log4jLogger.info("Go to Add unfiltered Destination...");
		return new Po_TritonWseSettingsHybridUnFilteredDes_AddEdit(driver);
	}
	
	
	/**
	 * Access Manager > Settings >Hybrid Configuration> UnFiltered Destination>Add
	 */
	public Po_TritonWseSettingsHybridUnFilteredDes_AddEdit toAddClients(){
		log4jLogger.info("Access Manager > Setting> Hybrid Configuration"
				+ ">UnFiltered Destination>Add unfiltered destination");
		return clickAddButton();
	}
	
	
	/**
	 * Click Ok button
	 * @return
	 */
	public Po_TritonWseSettingsHybridUnFilteredDes clickOkButton(){
		driverWait.until(ExpectedConditions.presenceOfElementLocated(okButtonBy));
		driver.findElement(okButtonBy).click();
		return this;
	}
	
	
	/**
	 * select all UnfilteredDestination items
	 * @return
	 */
	public Po_TritonWseSettingsHybridUnFilteredDes selectAllFL(){
		driverWait.until(ExpectedConditions.elementToBeClickable(selectAllBy));
		driver.findElement(selectAllBy).click();
		return this;
	}
	
	
	/**
	 * click delete button
	 * @return
	 */
	public Po_TritonWseSettingsHybridUnFilteredDes clickDeleButton(){
		driverWait.until(ExpectedConditions.elementToBeClickable(deleButtonBy));
		driver.findElement(deleButtonBy).click();
		return this;
	}
	
	
	public Po_TritonWseSettingsHybridUnFilteredDes clickDeleAndConfirmButton(){
		driverWait.until(ExpectedConditions.elementToBeClickable(deleButtonBy));
		driver.findElement(deleButtonBy).click();
		driverWait.until(ExpectedConditions.elementToBeClickable(deleConfirmBy));
		driver.findElement(deleConfirmBy).click();
		return this;
	}

}
