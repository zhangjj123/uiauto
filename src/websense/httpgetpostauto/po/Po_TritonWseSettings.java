package websense.httpgetpostauto.po;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import websense.httpgetpostauto.util.Log4j;
import websense.httpgetpostauto.util.SeleniumExtends;

public class Po_TritonWseSettings {
	
	private By settingsSideTab = By.id("objMSM-settingsSideTabMenuItem-submenuCollapsed");
	private By hybridConfigurationBy = By.id("objMSM-hybridMenuItem-colExpLink");
	public static int hybridConfigurationUnfolded = 0;
	private By hybridUnFilteredDesBy = By.id("objMSM-nonProxiedDestMenuItem-cont");
	private By dataIframeBy = By.id("dataFrame");
	
	// define driver

	private WebDriver driver;
	private WebDriverWait driverWait;

	// Log4j
	private Logger tritonWseMainLogger;
	
	
	public Po_TritonWseSettings(WebDriver driver) {
		this.driver=driver;
		this.driverWait=new WebDriverWait(driver,15);
		this.tritonWseMainLogger=Log4j.logger(Po_TritonWseSettings.class.getName());
		
		driverWait.until(ExpectedConditions.presenceOfElementLocated(settingsSideTab));
		
	}
	
	
	/**
	 * click the 'Settings > Hybrid Configuration'
	 * @return
	 */
	public Po_TritonWseSettings clickHybridConfiguration(){
		try{
			driver.findElement(hybridConfigurationBy).click();
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Fail to locate Hybrid Configuration page."
					+ "/nPlease check if you have initiated hybrid connection.");
		}
		return this;
	}
	
	
	/**
	 * Unfold Hybrid Configuration
	 */
	public void unfoldHybridConfiguration(){
		if(hybridConfigurationUnfolded==0){
			clickHybridConfiguration();
			hybridConfigurationUnfolded=1;
			tritonWseMainLogger.info("Unfold Settings -> Hybrid Configuration on Manager.");
		}
		else{
			tritonWseMainLogger.info("Settings > Hybrid Configuration is already unfolded.");
		}
	}
	
	
	/**
	 * Click 'Settings>Hybrid configuration>Unfiltered Destination' page
	 * @return
	 * @throws Exception
	 */
	public Po_TritonWseSettingsHybridUnFilteredDes clickHybridUnFilteredDes() 
			throws Exception{		
		driver.findElement(hybridUnFilteredDesBy).click();
		//switch to data frame
		SeleniumExtends.switchToFrame(driver, dataIframeBy);
		return new Po_TritonWseSettingsHybridUnFilteredDes(driver);
	}

	
	/**
	 *  Access 'Settings>Hybrid configuration>Unfiltered Destination '
	 * @return
	 * @throws Exception 
	 */
	public Po_TritonWseSettingsHybridUnFilteredDes toHybridUnFilteredDes() 
			throws Exception{
		tritonWseMainLogger.info
		("Access to Settings > Hybrid Configuration > Unfiltered Destination page");
		unfoldHybridConfiguration();
		return clickHybridUnFilteredDes();
	}
}
