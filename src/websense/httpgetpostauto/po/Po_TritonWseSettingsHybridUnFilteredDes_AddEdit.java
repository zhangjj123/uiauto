package websense.httpgetpostauto.po;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import websense.httpgetpostauto.util.Log4j;

public class Po_TritonWseSettingsHybridUnFilteredDes_AddEdit {

	private By okButtonBy = By.id("idToolbarFormFacet:idActionApplyProxy");
	private By nameBy = By.id("hostedAddConfigConnectionForm:idName");
	private By typeSelectBy = By.cssSelector("select[id$=':idType']");
	private By ipInputBoxBy = By.cssSelector("input[id$=':idIP']");
	private By domainInputBy = By.cssSelector("input[id$=':idDomain']");
	private By subnetInputBy = By.cssSelector("input[id$=':idSubnetIP']");
	private By descriptionBy=By.cssSelector("textarea[id$=':idDesc'] ");
	
	// Selenium related objects
	private WebDriver driver;
	private WebDriverWait driverWait;
	// Log4j
	private Logger log4jLogger;
	
	
	public Po_TritonWseSettingsHybridUnFilteredDes_AddEdit(WebDriver driver) {
		// Initialize log4j
		this.log4jLogger = Log4j.logger(Po_TritonWseSettingsHybridUnFilteredDes_AddEdit.class.getName());
		// Initialize driver
		this.driver = driver;
		this.driverWait = new WebDriverWait(driver, 60);
		// Check page is navigated to Setting page.
		driverWait.until(ExpectedConditions.elementToBeClickable(okButtonBy));
	}
	
	
	/**
	 * input unfiltered destination name
	 * @param nameDes
	 * @return
	 */
    public Po_TritonWseSettingsHybridUnFilteredDes_AddEdit typeName(String nameDes){
		driverWait.until(ExpectedConditions.presenceOfElementLocated(nameBy));
    	driver.findElement(nameBy).clear();
    	driver.findElement(nameBy).sendKeys(nameDes);
    	return this;
    }
    
    
    public Po_TritonWseSettingsHybridUnFilteredDes_AddEdit inputDescription(String desText){
    	driver.findElement(descriptionBy).clear();
    	driver.findElement(descriptionBy).sendKeys(desText);
    	return this;
    }
    
    /**
     * select type
     * @param type
     * @return
     */
    public Po_TritonWseSettingsHybridUnFilteredDes_AddEdit selectType(String type){
        Select typeSelect=new Select(driver.findElement(typeSelectBy));
        typeSelect.selectByValue(type);
        return this;
    }
    
    
    /**
     * input ipAddress
     * @param ipAddress
     * @return
     */
    public Po_TritonWseSettingsHybridUnFilteredDes_AddEdit inputIPAddress(String ipAddress){
        selectType("Address");
        WebElement ipElement=driver.findElement(ipInputBoxBy);
        driverWait.until(ExpectedConditions.visibilityOf(ipElement));
        ipElement.clear();
        ipElement.sendKeys(ipAddress);
        return this;
    }

	/**
	 * Input domain address
	 * @param domainAddress: Domain Address
	 * @return
	 */
	public Po_TritonWseSettingsHybridUnFilteredDes_AddEdit inputDomain(String domainAddress) {
	
		selectType("Domain");
		WebElement domainElement = driver.findElement(domainInputBy);
		driverWait.until(ExpectedConditions.visibilityOf(domainElement));
		domainElement.clear();
		domainElement.sendKeys(domainAddress);
		return this;
	}
	
	
	/**
	 * Input subnet address
	 * @param subNetAddress: subnet Address
	 * @return
	 */
	public Po_TritonWseSettingsHybridUnFilteredDes_AddEdit inputSubnet(String subNetAddress) {
	
		selectType("Subnet");
		WebElement subNetElement = driver.findElement(subnetInputBy);
		driverWait.until(ExpectedConditions.visibilityOf(subNetElement));
		subNetElement.clear();
		subNetElement.sendKeys(subNetAddress);
		return this;
	}
	
	
	/**
	 * Click Ok button to return main unfiltered destination page
	 * @return
	 */
	public Po_TritonWseSettingsHybridUnFilteredDes clickOkButton() {
		driverWait.until(ExpectedConditions.presenceOfElementLocated(okButtonBy));
		driver.findElement(okButtonBy).click();
		return new  Po_TritonWseSettingsHybridUnFilteredDes(driver);
	}
	
}
