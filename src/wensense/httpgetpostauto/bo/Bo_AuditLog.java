package wensense.httpgetpostauto.bo;

import org.apache.log4j.Logger;

import websense.httpgetpostauto.po.Po_TritonWse;
import websense.httpgetpostauto.po.Po_TritonWseMainAuditLog;
import websense.httpgetpostauto.util.Log4j;
import websense.httpgetpostauto.util.SeleniumExtends;

public class Bo_AuditLog {
	private Po_TritonWse wseManager;
    private Logger log4jLogger;
    
   
	/**
     * Constructor
     */
	public Bo_AuditLog(Po_TritonWse wseManager){
		//Initialize log4j
		this.log4jLogger=Log4j.logger(Bo_AuditLog.class.getName());
		
		//initialize wse Manager page
		this.wseManager=wseManager;
	}
	
	
	public String[] getAuditLog(int i) throws Exception{
		Po_TritonWseMainAuditLog poAuditLog=wseManager.toMain().toAuditLog();
		String[] auditData=poAuditLog.getAuditDataForOneRow(i);
		
		wseManager.backToWseFrame();
		
		return auditData;
	}
	
	public String[][] getSubLog(String text){
		String[] data=null;
		if(text!=null){
			data=text.split("\"");
		}
		data=data[3].split("_______WBSN_RPLC_______");
		
		int size=data.length;
		String[][] resultData=new String[size][2];
		for(int j=0;j<size;j++){
			resultData[j]=data[j].split(":");
		}
		
		return resultData;
	}
}