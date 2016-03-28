package websense.httpgetpostauto.po;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import websense.httpgetpostauto.util.Log4j;
import websense.httpgetpostauto.util.SeleniumExtends;

public class Po_TritonWseMain {

	private By dataIframeBy = By.id("dataFrame");
	private By auditlogBy = By.id("objMSM-auditLogMenuItem-cont");
	
	//Selenium related objects
	private WebDriver driver;
	private WebDriverWait driverWait;	

	//Log4j
	private Logger tritonWseMainLogger;
	

	public Po_TritonWseMain(WebDriver driver) {
		//initialize log4j
		this.tritonWseMainLogger=Log4j.logger(Po_TritonWseMain.class.getName());
		
		//initialize driver
		this.driver=driver;
		this.driverWait=new WebDriverWait(driver,15);
		
		driverWait.until(ExpectedConditions.presenceOfElementLocated(auditlogBy));
		
	}

	
	/**
	 * click 'Manager > Main > Audit Log'
	 * @return
	 * @throws Exception 
	 */
	public Po_TritonWseMainAuditLog clickAuditLog() throws Exception{
		driver.findElement(auditlogBy).click();
		//Switch to data frame
		SeleniumExtends.switchToFrame(driver, dataIframeBy);
		return new Po_TritonWseMainAuditLog(driver);
	}
	
	
	/**
	 * Access 'Manager > Main > Audit Log'
	 * @return
	 * @throws Exception 
	 */
	public Po_TritonWseMainAuditLog toAuditLog() throws Exception{
		tritonWseMainLogger.info("Access Manager > Main > Audit Log.");		
		
		return clickAuditLog();
	}
}
