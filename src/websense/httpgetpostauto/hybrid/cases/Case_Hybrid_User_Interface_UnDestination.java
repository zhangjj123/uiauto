package websense.httpgetpostauto.hybrid.cases;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import websense.httpgetpostauto.po.Po_Triton;
import websense.httpgetpostauto.po.Po_TritonWse;
import websense.httpgetpostauto.util.ExcelReaderForTestngDataProvider;
import wensense.httpgetpostauto.bo.Bo_AuditLog;
import wensense.httpgetpostauto.bo.Bo_HybridUnFilteredDestination;

@Test(groups={"hybrid.UI"})
@Listeners({websense.httpgetpostauto.util.SeleniumScreenShotOnFailure.class})
public class Case_Hybrid_User_Interface_UnDestination {
	private Po_Triton tritonManager;
	private Po_TritonWse wseManager;
	private Logger log4jLogger;
	private String unDestData;
	
	
	@BeforeClass(alwaysRun=true)
	public void logonManagerAndInitialize() throws Exception{
		//Logon Manager and return manager instance
		tritonManager=new Po_Triton();
		wseManager=tritonManager.logon();
	}
	
	
	/**
	 * Logoff Manager after running all cases
	 * @throws Exception 
	 */
	@AfterClass(alwaysRun=true)
	public void logoffManager() throws Exception{
		//delete configuraion
		Bo_HybridUnFilteredDestination boUnDes=new Bo_HybridUnFilteredDestination(wseManager);
        boUnDes.deleteAllUnDes();
				
		//Logoff Manager
		tritonManager.logoff().close();
	}
	
	
	
	
	
	
	@Test(enabled=true,dataProvider="dataProvider_testUnDesHybridIPAdd")
	private void testUnDesHybridIPAdd(String unDesName,String ip,String description) throws Exception{
		Bo_HybridUnFilteredDestination boUnDes=new Bo_HybridUnFilteredDestination(wseManager);
		boUnDes.addOneUnDesIP(unDesName, ip, description);
		
		Thread.sleep(1000);		
		
		Bo_AuditLog boAuditLog=new Bo_AuditLog(wseManager);
		String[] auditLog=boAuditLog.getAuditLog(0);
		
		assertEquals("Hybrid Settings",auditLog[4]);
		assertEquals(unDesName, auditLog[5]);
		assertEquals("Add", auditLog[6]);
		
		
		String[][] auditLogArray=boAuditLog.getSubLog(auditLog[8]);
		assertEquals("IP Address", auditLogArray[0][1]);
		assertEquals(ip, auditLogArray[4][1]);
		assertEquals("Hybrid",auditLogArray[7][1] );
		
		
	}

	@DataProvider(name="dataProvider_testUnDesHybridIPAdd")
	private Object[][] dataProvider_testUnDesHybridIPAdd() throws Exception{
		return ExcelReaderForTestngDataProvider.getDataFrom("Case_Hybrid_UI_UnDes.xlsx","testUnDesHybridIpAdd");
	}

}
