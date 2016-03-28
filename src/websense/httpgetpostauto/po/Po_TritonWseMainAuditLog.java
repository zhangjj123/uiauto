package websense.httpgetpostauto.po;

import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import websense.httpgetpostauto.util.Log4j;

public class Po_TritonWseMainAuditLog {
	
	private By goButtonBy=By.id("auditLogform:exportall");
	private By tbodyBy=By.id("auditLogform:idDataTableLogs:tb");

	//Selenium related objects
	private WebDriver driver;
	private WebDriverWait driverWait;
					  		 
	//Log4j
	private Logger log4jLogger;
	
	
	/**
	 * Constructor
	 * @param driver
	 */
	public Po_TritonWseMainAuditLog(WebDriver driver) {
		//Initialize log4j
		this.log4jLogger=Log4j.logger(Po_TritonWseMainAuditLog.class.getName());
		
		//Initialize driver
		this.driver=driver;
		this.driverWait=new WebDriverWait(driver,60);
		
		//Check page is navigated to Main > Audit Log  page.
		driverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(goButtonBy));
	}
	
	
	public String[] getAuditDataForOneRow(int i){
		List<WebElement> RowElements=driver.findElement(tbodyBy).findElements(By.tagName("tr"));
		WebElement rowElement=RowElements.get(i);
		
		List<WebElement> colElements=rowElement.findElements(By.tagName("td"));
		
		String[] data=new String[9];
		System.out.println("*******"+colElements.size()+"******");
		for(int j=0;j<7;j++){
			data[j]=colElements.get(j).findElement(By.tagName("span")).getText();
			log4jLogger.info("column "+j+" 's data is "+data[j]);
		}
		
		try{
			data[7]=colElements.get(7).findElement(By.tagName("div")).findElement(By.tagName("a")).getAttribute("onclick");
			log4jLogger.info("column "+7+" 's data is "+data[7]);
		}catch(NoSuchElementException e){
			data[7]=colElements.get(7).findElement(By.tagName("span")).getText();
			log4jLogger.info("column 7's value is not a array.");
		}
		
		try{
			data[8]=colElements.get(8).findElement(By.tagName("div")).findElement(By.tagName("a")).getAttribute("onclick");
			log4jLogger.info("column "+8+" 's data is "+data[8]);
		}catch(NoSuchElementException e){
			data[8]=colElements.get(8).findElement(By.tagName("span")).getText();
			log4jLogger.info("column 8's value is not a array.");
		}
		
		return data;
	}
	


}
