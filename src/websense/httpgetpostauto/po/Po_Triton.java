package websense.httpgetpostauto.po;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import websense.httpgetpostauto.util.ClearWseConfig;
import websense.httpgetpostauto.util.Log4j;
import websense.httpgetpostauto.util.OSValidator;
import websense.httpgetpostauto.util.ProjectFile;
import websense.httpgetpostauto.util.SeleniumExtends;

public class Po_Triton {
	
	private By usernameBy=By.id("loginForm:idUserName");
	
	private By passwordBy = By.id("loginForm:idPassword");
	
	private By logonButtonBy = By.id("loginForm:idLoginButton");
	
	private By logoffButtonBy = By.id("topLogo:idActionLogoff");
	private By TechLibraryLinkBy=By.id("loginForm:idTechnicalLibraryLink");
	
	
	//wse Iframe
	private By wseIframeBy=By.cssSelector("iframe[id='eipApplicationFrame_webSecurityProduct_']");
	
	
	
	
	private String logonUsernameStr;
	private String logonPasswordStr;
	private String managerIP;
	
	
	
	private WebDriver driver=null;
	private static WebDriver staticDriver;
	private String driverType;
	private WebDriverWait driverWait;
	private DesiredCapabilities capabilities;
	
	
	private Logger tritonLogger;
	
	
	public Po_Triton() throws Exception{
		//c初始化 log4j
		this.tritonLogger=Log4j.logger(Po_Triton.class.getName());
		
		//settings
		HashMap<String,String> settingsMap=new HashMap<String,String>();
		
		//读取properties文件
		settingsMap.put("logonUsernameStr", ProjectFile.read("conf", "system.properties", "Username"));
		settingsMap.put("logonPasswordStr", ProjectFile.read("conf", "system.properties", "Password"));
		settingsMap.put("managerIP", ProjectFile.read("conf", "system.properties", "ManagerIP"));
		settingsMap.put("driverType", ProjectFile.read("conf", "system.properties", "DriverType"));

		initConstructor(settingsMap);
	}

	

	private void initConstructor(HashMap<String, String> settingsMap) throws Exception {
		// check the keys of setting map
		String[] settingsArray={"logonUsernameStr","logonPasswordStr","driverType"};
		for(int i=0;i<settingsArray.length;i++){
			if(!settingsMap.containsKey(settingsArray[i])){
				throw new RuntimeException("settingsMap the key "+settingsArray[i]+" is not defined!");
			}
		}
		
		
		 // Read parameters from property file
		this.logonUsernameStr=settingsMap.get("logonUsernameStr");
		this.logonPasswordStr=settingsMap.get("logonPasswordStr");
		this.managerIP=settingsMap.get("managerIP");
		
		this.driverType=settingsMap.get("driverType");
		
		
		if(driverType.equals("Firefox")){
			
			//bypass untrusted ssl certificate
			ProfilesIni profilesIni=new ProfilesIni();
			FirefoxProfile profile=profilesIni.getProfile("default");
			profile.setAcceptUntrustedCertificates(true);
			profile.setAssumeUntrustedCertificateIssuer(false);
			
			profile.setPreference("browser.helperapps.neverAsk.saveToDisk" , "application/csv;text/csv;");
            profile.setPreference("browser.helperApps.alwaysAsk.force", false); 
            profile.setPreference("browser.download.manager.showWhenStarting",false); 
            profile.setPreference("browser.download.folderList", 2); 
            profile.setPreference("browser.download.dir",System.getProperty("user.home").toString() + "\\Downloads"); 
		
            System.setProperty("webdriver.firefox.bin", ProjectFile.read("conf", "system.properties", "FirefoxDriverPath"));
		    //create driver
            driver=new FirefoxDriver(profile);
		
		}else if(driverType.equals("IE")){
			System.setProperty("webdriver.ie.driver",ProjectFile.read("conf", "system.properties", "IEDriverServerPath"));
		    //DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
            //ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);

			driver=new InternetExplorerDriver();
		}else if(driverType.equals("Chrome")){
			
			String chromeDriverPath=ProjectFile.read("conf", "system.properties", "ChromeDriverPath");
		    System.setProperty("webdriver.chrome.driver",chromeDriverPath);
		    
		    if(OSValidator.isWindows()){
		    	driver=new ChromeDriver();
		    }else{		    	
		    	ChromeOptions options=new ChromeOptions();
		    	options.addArguments("--no-sandbox");
		    	driver=new ChromeDriver(options);
		    }
		}else if(driverType.equals("HtmlUnit")){			
			driver=new HtmlUnitDriver();
		}else{
			throw new Exception("Invalid value of 'DriverType'. Must be 'Firefox | IE | Chrome | HtmlUnit'.");
		}
		
		staticDriver=driver;
		//driver=returnDriver();
		
		this.driverWait=new WebDriverWait(driver,15);
		
		driver.get("https://"+managerIP+":9443/triton/login/pages/loginPage.jsf");
		
        // For IE, click to accept untrusted certificate
        if (driverType.equals("IE")) {
            driver.get("javascript:document.getElementById('overridelink').click();");

        }
        
       // Ensure logon page is loaded
       driverWait.until(ExpectedConditions.presenceOfElementLocated(logonButtonBy));
       
       driver.manage().window().maximize();
	}
	
	//type username
	public Po_Triton typeUsername(String username){
		driver.findElement(usernameBy).clear();
		driver.findElement(usernameBy).sendKeys(username);
		return this;
	}
	
	
	public Po_Triton typePassword(String password){
		driver.findElement(passwordBy).sendKeys(password);
		return this;
	}
	
	
	public Po_Triton clickLogonButton() throws Exception{
		driver.findElement(logonButtonBy).click();
		try{
			driverWait.until(ExpectedConditions.presenceOfElementLocated(wseIframeBy));
			SeleniumExtends.switchToFrame(driver, wseIframeBy);
		}catch(Exception e){
			throw new Exception("Fail to logon to WSE Manager.");
		}
		return this;
	}
	
	
	public Po_TritonWse logon(String...args) throws Exception{
		//clear manager session to avoid that SA cannot logon
		ClearWseConfig clearWseConfig=new ClearWseConfig();
		clearWseConfig.clearManagerSession();
		
		if(args.length!=0){
			return null;
		}
		else{
			typeUsername(logonUsernameStr);
			typePassword(logonPasswordStr);
			clickLogonButton();
			return new Po_TritonWse(driver);
		}
	}
	
	
	public Po_TritonWse logon(boolean clearManagerSession) throws Exception{
		if(clearManagerSession){
			return logon();
		}
		else{
			typeUsername(logonUsernameStr);
			typePassword(logonPasswordStr);
			clickLogonButton();
			return new Po_TritonWse(driver);
		}
	}
	
	
	public Po_Triton clickLogoffButton() throws Exception{
		try{
			driver.switchTo().defaultContent();
			driverWait.until(ExpectedConditions.elementToBeClickable(logoffButtonBy));
			
			driver.findElement(logoffButtonBy).click();
			try{
				//login form shows, just return.
				driverWait.until(ExpectedConditions.presenceOfElementLocated(TechLibraryLinkBy));
			  
			}catch(TimeoutException e){
				//check whether unsaved change alert is present.
				By popupFrameBy=By.id("popupDivContentIFrame");
				try{
					driverWait.until(ExpectedConditions.presenceOfElementLocated(popupFrameBy));
					tritonLogger.info("Unsaved changes alert shows");
				}catch(TimeoutException e1){
					throw new Exception("Login form does not show and neither does the unsaved changes alert.");
				}
				
				try{
					SeleniumExtends.switchToFrame(driver, popupFrameBy);
					List<WebElement> discardBtnList=driver.findElements(By.xpath("//span[@class='paButtonText'][text='Discard']/../parent::tr[@class='paButton']"));
					
					if(!discardBtnList.isEmpty()){
						WebElement discardBtn=(WebElement)discardBtnList.get(0);
						if(discardBtn.isDisplayed()){
							discardBtn.click();
							driver.switchTo().defaultContent();
							tritonLogger.warn("There are unsaved changes and are discarded before logging off.");
						    
							try{
								driverWait.until(ExpectedConditions.presenceOfElementLocated(TechLibraryLinkBy));
								
							}catch(TimeoutException e2){
                                throw new Exception("Login form does not show after clicked the discard change button.");

							}
						}
					}else{
                        throw new Exception("The discard change button was not found."); //no save or discard button present. May be other kind of alert.

					}
				}catch(Exception e3){
					e.printStackTrace();
				}
			}
			return this;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("Fail to logoff Manager");
		}
	}
	
	
	public Po_Triton logoff() throws Exception{
		tritonLogger.info("Logoff triton Manager");
		
		 // Rest some static variables
		
		return clickLogoffButton();
	}
	
	
	public void close(){
		driver.quit();
	}



	public static WebDriver getDriver() {
		// TODO Auto-generated method stub
		return staticDriver;
	}


}
