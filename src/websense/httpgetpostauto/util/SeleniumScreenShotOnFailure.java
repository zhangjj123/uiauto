package websense.httpgetpostauto.util;

/**import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import mx4j.tools.config.DefaultConfigurationBuilder.New;

import org.apache.commons.io.FileUtils;
**/

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

import websense.httpgetpostauto.po.Po_Triton;

public class SeleniumScreenShotOnFailure extends TestListenerAdapter{
	
	@Override
	public void onTestFailure(ITestResult tr){
		WebDriver driver = Po_Triton.getDriver();
		
		//if: don't need to take snapshot, because Selenium is not used.
		if ((driver == null) || (driver.toString().contains("null"))){
			super.onTestFailure(tr);
		} 
		//else: take snapshot.
		else {
			//Read the type of web driver.
			String WebDriverExecutionType = "";
			try {
				WebDriverExecutionType = ProjectFile.read("conf",
						"system.properties", "WebDriverExecutionType");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//To take snapshot on RemoteWebDriver. For local driver, we don't need to use the "Augmenter" class. 
			if (WebDriverExecutionType.equalsIgnoreCase("GRID")) {
				driver = new Augmenter().augment(driver);
			}
			
			//Take Snapshot
			String screenshotBase64 = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BASE64);
			
			String dataUri="data:image/png;base64," + screenshotBase64;
		
			
			/** not save as local file
			File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			DateFormat dateFormat = new SimpleDateFormat("dd_MMM_yyyy__hh_mm_ssaa");
			Date date = new Date();
			String destFile = dateFormat.format(date) + ".png";
			
			try {
				FileUtils.copyFile(srcFile, new File(ProjectFile.returnPath("test-output", destFile)));
			} catch (Exception e) {
				e.printStackTrace();
			} 
			**/
			
			Reporter.setEscapeHtml(false);
			//Reporter.log("Saved <a href=./" + destFile + ">Screenshot</a>");
			Reporter.log("<img alt=\"Captured Screenshot\" src=\"" + dataUri + "\" />");
		}
		
	}
}
