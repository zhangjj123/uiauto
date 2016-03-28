package wensense.httpgetpostauto.bo;

import org.apache.log4j.Logger;

import websense.httpgetpostauto.po.Po_TritonWse;
import websense.httpgetpostauto.po.Po_TritonWseSettingsHybridUnFilteredDes;
import websense.httpgetpostauto.util.Log4j;

public class Bo_HybridUnFilteredDestination {
	
	//Log4j
	private Logger log4jLogger;
	
	//WSE Manager after logon Triton Manager
	//It can be used to access WSE Main/Settings > related pages
	private Po_TritonWse wseManager;
	private Po_TritonWseSettingsHybridUnFilteredDes poUnDes;
	
	
	/**
	 * constructor
	 */
	public Bo_HybridUnFilteredDestination(Po_TritonWse wseManager){
		//initialize WseManager 
		this.wseManager=wseManager;
		//initialize log4j
		this.log4jLogger=Log4j.logger(Bo_HybridUnFilteredDestination.class.getName());		
	}

	
	public void addOneUnDesIP(String nameIp,String IPAddress,String description) 
			throws Exception{
		try{
			//Access "Manager >Settings > Hybrid Unfiltered Destination"
			Po_TritonWseSettingsHybridUnFilteredDes boUnFilteredDes=wseManager.toSettings().toHybridUnFilteredDes();
			
			//add one UnFiltered Destination.
			boUnFilteredDes.toAddClients()
			.typeName(nameIp).inputDescription(description).inputIPAddress(IPAddress).clickOkButton().clickOkButton();
		
		    //save configuration
			wseManager.backToWseFrame().saveAll();
			log4jLogger.info("Successfully add one UnFiltered Destination as IP: "+IPAddress);
			}catch(Exception e){
			    e.printStackTrace();
			    throw new Exception("Failed to add one UnFiltered Destination as IP: "+IPAddress);
			}
		
	}
	
	
	
	public void deleteAllUnDes() throws Exception{
		try
		{
			//Access 'Manager > Settings > Hybird Unfitered Destinaton'
			Po_TritonWseSettingsHybridUnFilteredDes boUnFilteredDes=wseManager.toSettings().toHybridUnFilteredDes();
			
			//delete all UnDes items
			boUnFilteredDes.selectAllFL().clickDeleAndConfirmButton().clickOkButton();;
			
			//save configuration
			wseManager.backToWseFrame().saveAll();
			
			log4jLogger.info("Successfully delete  all unfiltered Destination.");
		}catch(Exception e){
			e.printStackTrace();
			log4jLogger.info("Failed to delete all unfiltered Destination.");
		}
		
	}
}
