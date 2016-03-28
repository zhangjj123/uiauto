package websense.httpgetpostauto.po;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import websense.httpgetpostauto.util.Log4j;
import websense.httpgetpostauto.util.SeleniumExtends;

public class Po_TritonWse {
	
    // Location of WebElements after logon
    private By mainSettingsBy = By.cssSelector("span[class='msmTabRight']");
    private By saveAllOnBy = By.id("saveAllForm:save_changes_button_on");
    private By saveAllYellowColorBy = By.id("saveAllForm:idSaveChangesDivOn");
    
    private By wseIframeBy=By.id("eipApplicationFrame_webSecurityProduct_");
    private By TritonIframeBy = By.id("applicationsContainer");
    
    private By collapsePaneBy = By.id("objMSM-CollapsedPane-arrow");
    
    // Selenium related objects

    private WebDriver driver;

    private WebDriverWait driverWait;

    // Log4j

    private Logger tritonWseLogger;

	public Po_TritonWse(WebDriver driver) {

		this.tritonWseLogger=Log4j.logger(Po_TritonWse.class.getName());
		this.driver=driver;
		this.driverWait=new WebDriverWait(driver,300);
		
		driverWait.until(ExpectedConditions.presenceOfElementLocated(mainSettingsBy));
	}
	
	
	public Po_TritonWse backToWseFrame() throws Exception{
		driver.switchTo().defaultContent();
		SeleniumExtends.switchToFrame(driver, wseIframeBy);
		return this;
	}
	
	
	/**
	 * Access 'Manager > Main' page
	 * @return
	 * @throws Exception
	 */
	public Po_TritonWseMain toMain() throws Exception{
		this.backToWseFrame();
		
		driver.findElements(mainSettingsBy).get(0).click();
		tritonWseLogger.info("Access 'Manager > Main' page.");
        // Judge if main tab is collapsed, then click to extend it.
		if(driver.findElement(collapsePaneBy).isDisplayed()){
			driver.findElement(collapsePaneBy).click();
		}
		
		Thread.sleep(2000);
		return new Po_TritonWseMain(driver);		
	}
	
	
	/**
	 * Access 'Manager > Settings' page
	 * @return
	 * @throws Exception
	 */
	public Po_TritonWseSettings toSettings() throws Exception{
		this.backToWseFrame();
		
		driver.findElements(mainSettingsBy).get(1).click();
		tritonWseLogger.info("Access 'Manager > Settings' page.");
        // Judge if settings tab is collapsed, then click to extend it.
		if(driver.findElement(collapsePaneBy).isDisplayed()){
			driver.findElement(collapsePaneBy).click();
		}
		
		Thread.sleep(2000);
		return new Po_TritonWseSettings(driver);		
	}
	
	
	public Po_TritonWse saveAll() throws Exception{
		// Check "Save All" button becomes yellow color.
		this.backToWseFrame();
		driverWait.until(ExpectedConditions.presenceOfElementLocated(saveAllYellowColorBy));
		
		Thread.sleep(3000);
		 if (driver.findElement(saveAllYellowColorBy).getAttribute("style")
	                .equals("display: none;")) {

	            // If doesn't change color

	            throw new Exception(
	                    "No any configuration change, so 'Save All' button doesn't become yellow color.");

	        } else {

	            // If change color. click the button.

	            driver.findElement(saveAllOnBy).click();

	            // Until the color of this button becomes gray

	            driverWait.until(ExpectedConditions
	                    .invisibilityOfElementLocated(saveAllYellowColorBy));

	            return this;

	        }
		
	}

}
