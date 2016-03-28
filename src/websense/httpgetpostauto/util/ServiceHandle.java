package websense.httpgetpostauto.util;
import java.io.File;
public class ServiceHandle {
	private static String websenseBinInstalledPath;
	private static String[] servicePID;	public static final int FilterService = 1;	public static final int LogonAgent = 2;	public static final int DCAgent = 3;	public static final int LogServer = 4;		/**	 * start/stop DCAgent	 */	public static int startDCAgent(boolean bStart) throws Exception	{		String[] cmdStart = new String[]{"net start \"Websense DC Agent\""};		String[] cmdStop = new String[]{"net stop \"Websense DC Agent\""};		String DCAgentIP = ProjectFile.read("conf", "xidconfig.properties", "DCAgentIP");		String DCTelUser = ProjectFile.read("conf", "xidconfig.properties", DCAgentIP + "_Admin");		String DCTelPwd = ProjectFile.read("conf", "xidconfig.properties", DCAgentIP + "_Pwd");		TelnetClientWFAT telnetClient = new TelnetClientWFAT(DCAgentIP, 23);		String commandPrompt = "Administrator>";		String outputString = "";		try {			telnetClient.loginWindowsAndRunCommands(DCTelUser, DCTelPwd, commandPrompt);			if(bStart) //start service			{				//Connect to remote Windows client by telnet protocol and execute commands				outputString = telnetClient.sendCommands(cmdStart, commandPrompt);				System.out.println("wait 10s for DC agent start...");				Thread.sleep(10000);				if ((outputString.contains("successfully")) || (outputString.contains("already been started") || outputString.equals(""))){					System.out.println("start service succesfully");					return 0;				}else{					System.out.println("fail to start service");					return -1;				}			}				else //stop service			{				outputString = telnetClient.sendCommands(cmdStop, commandPrompt);				System.out.println("wait 10s for DC agent stop...");				Thread.sleep(10000);				if ((outputString.contains("successfully")) || (outputString.contains("is not started")) || outputString.equals("")){					System.out.println("Stop service successfully");					return 0;				}else{					System.out.println("fail to stop service");					return -1;				}			}		} catch (Exception e) {			e.printStackTrace();			throw new RuntimeException("Fail to telnet Websense component box or start/stop service");		} finally {			telnetClient.disconnect();		}	}	
	/**
	 * start/stop LogServer
	 */
	public static int startLogServer(boolean bStart) throws Exception
	{
		String[] cmdStart = new String[]{"net start \"Websense Log Server\""};
		String[] cmdStop = new String[]{"net stop \"Websense Log Server\""};
		String LogServerIP = ProjectFile.read("conf", "system.properties", "LogServerIP");
		String LSTelUser = ProjectFile.read("conf", "system.properties", "LSUser");
		String LSTelPwd = ProjectFile.read("conf", "system.properties", "LSPwd");
		TelnetClientWFAT telnetClient = new TelnetClientWFAT(LogServerIP, 23);
		String commandPrompt = "Administrator>";
		String outputString = "";
		try {
			telnetClient.loginWindowsAndRunCommands(LSTelUser, LSTelPwd, commandPrompt);
			if(bStart) //start LS
			{
				//Connect to remote Windows client by telnet protocol and execute commands
				outputString = telnetClient.sendCommands(cmdStart, commandPrompt);
				if ((outputString.contains("successfully")) || (outputString.contains("already been started"))){
					System.out.println("start LS succesfully");
					return 0;
				}else{
					System.out.println("fail to start LS");
					return -1;
				}
			}	
			else //stop LS
			{
				outputString = telnetClient.sendCommands(cmdStop, commandPrompt);
				if ((outputString.contains("successfully")) || (outputString.contains("is not started"))){
					System.out.println("Stop LS successfully");
					return 0;
				}else{
					System.out.println("fail to stop LS");
					return -1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Fail to telnet LogServer box or start/stop LogServer");
		} finally {
			telnetClient.disconnect();
		}
	}		public static int startLogServerDebug() throws Exception{				String[] cmdStartDebug = new String[]{"sc start WebsenseLogServer -debug"};		String LogServerIP = ProjectFile.read("conf", "system.properties", "LogServerIP");		String LSTelUser = ProjectFile.read("conf", "system.properties", "LSUser");		String LSTelPwd = ProjectFile.read("conf", "system.properties", "LSPwd");		TelnetClientWFAT telnetClient = new TelnetClientWFAT(LogServerIP, 23);		/**		 * If telnet log server box using user administrator, then telnet will check return values if it ends with administrator>		 * else log server using user DomainName\administrator, then telnet will check return values if it ends with administrator.DomainName>		 */		String commandPrompt = LSTelUser.toLowerCase().equals("administrator") ? "Administrator>" : 			"administrator."+LSTelUser.substring(0,LSTelUser.toLowerCase().indexOf("administrator")-1).toUpperCase()+">";		String outputString = "";		try {			telnetClient.loginWindowsAndRunCommands(LSTelUser, LSTelPwd, commandPrompt);			//Connect to remote Windows client by telnet protocol and execute commands			outputString = telnetClient.sendCommands(cmdStartDebug, commandPrompt);			if ((outputString.contains("STATE              : 2  START_PENDING")) || (outputString.contains("already running"))){				System.out.println("start LS in debug mode succesfully");				return 0;			}else{				System.out.println("fail to start LS in debug mode");				return -1;			}		} catch (Exception e) {			e.printStackTrace();			throw new RuntimeException("Fail to telnet LogServer box or start LogServer");		} finally {			telnetClient.disconnect();		}	}		public static void StartPolicyServer(String PS_ip, int OS_type,			String username, String password) throws Exception {		ServiceControlPolicyServer(PS_ip, OS_type, username, password, true);	}	public static void StopPolicyServer(String PS_ip, int OS_type,			String username, String password) throws Exception {		ServiceControlPolicyServer(PS_ip, OS_type, username, password, false);	}	/**	 * @param PS_ip	 * @param OS_type	 *            0 for win, 1 for Linux, but V-series not supported	 * @param username	 * @param password	 * @param bStart	 *             true for start, false for stop	 * @return	 * @throws Exception	 */	private static void ServiceControlPolicyServer(String PS_ip, int OS_type,			String username, String password, boolean bStart) throws Exception {		String WinCP = ">";		String LinuxCP = "#";		String[] cmdStartDebug = new String[] { "" };		String outputString = "";		switch (OS_type) {		case 0:			if (bStart)				cmdStartDebug[0] = "sc start WebsensePolicyServer";			else				cmdStartDebug[0] = "sc stop WebsensePolicyServer | taskkill /F /IM PolicyServer.exe";			TelnetClientWFAT telnetClient = new TelnetClientWFAT(PS_ip, 23);			try {				telnetClient.loginWindowsAndRunCommands(username, password,						WinCP);				// Connect to remote Windows client by telnet protocol and				// execute commands				outputString = telnetClient.sendCommands(cmdStartDebug, WinCP);				System.out.println(outputString);				break;			} catch (Exception e) {				e.printStackTrace();			} finally {				telnetClient.disconnect();			}		case 1:			SSHClientWFAT ssh = new SSHClientWFAT(PS_ip, 22, username, password);			if (bStart)				cmdStartDebug[0] = "/opt/Websense/WebsenseAdmin start";			else				cmdStartDebug[0] = "killall PolicyServer";			try {				ssh.connectAndLogin();				outputString = ssh.sendCommand(cmdStartDebug, LinuxCP);				System.out.println(outputString);				break;			} catch (Exception e) {				e.printStackTrace();			} finally {				ssh.disconnect();			}		}	}
	/**	 * stop remote websense service, serviceType can be 1-EIM, 2-Logon, 6-Policy Server, 7-Network Agent	 */	public static int stopRemoteWebsenseService(int serviceType, String serverIP, String serverOS, String serverAdmin, String serverPwd, String clientOsName) throws Exception{		String serviceName = "";		String websenseName = "";		switch(serviceType){		case 0:{			serviceName = "XidDCAgent";			websenseName = "Websense DC Agent";			break;		}		case 1:{			serviceName = "EIMServer";			websenseName = "Websense Filtering Service";			break;		}		case 2:{			serviceName = "AuthServer";			websenseName = "Websense Logon Agent";			break;		}		case 3:{			serviceName = "UserService";			websenseName = "Websense User Service";			break;		}		case 4:{			serviceName = "eDirectoryAgent";			websenseName = "Websense eDirectory Agent";			break;		}		case 5:{			serviceName = "RadiusAgent";			websenseName = "Websense RADIUS Agent";			break;		}		case 6:{			serviceName = "PolicyServer";			websenseName = "Websense Policy Server";			break;		}		case 7:{			serviceName = "NetworkAgent";			websenseName = "Websense Network Agent";			break;		}
		case 8:{
			serviceName = "WebsenseManagerTomcat";
			websenseName = "Websense TRITON - Web Security";
			break;
		}		default:			break;		}		if(serverOS.toLowerCase().contains("windows")){			TelnetClientWFAT telnetClient = new TelnetClientWFAT(serverIP, 23);			String[] admins = serverAdmin.split("\\\\");			String commandPrompt = admins[admins.length-1] + ">";			String outputString = "";
			
			String[] cmdKillProcess = new String[]{"taskkill /f /im " + serviceName + ".exe"};
			if (clientOsName.equalsIgnoreCase("linux")) {
				cmdKillProcess[0] = cmdKillProcess[0] + "\r";
			} 
						try{				telnetClient.loginWindowsAndRunCommands(serverAdmin, serverPwd, commandPrompt);				outputString = telnetClient.sendCommands(cmdKillProcess, commandPrompt);				if ((outputString.contains("SUCCESS")) || (outputString.contains("not found"))){					System.out.println("Stop " + serviceName + " successfully");					return 0;				}				else{					System.out.println("fail to stop " + serviceName + " on Windows");					return -1;				}			}catch(Exception e){				e.printStackTrace();				throw new RuntimeException("fail to stop " + serviceName + " on " + serverIP);			}finally{				telnetClient.disconnect();			}		}		else{			String[] cmdGetPID = new String[]{"ps aux | grep /opt/Websense/bin/" + serviceName + " | grep -v grep | awk '{print $2}'"};	    	SSHClientWFAT ssh = new SSHClientWFAT(serverIP, 22, serverAdmin, serverPwd);	        String commandPrompt = "#";	        String output = "";	    	try{		        ssh.connectAndLogin();		        if(serverOS.toLowerCase().contains("appliance")){		        	String command[] = {""};		        	if (serviceName.contains("NetworkAgent")){						command[0] = "ssh na";					}					else{						command[0] = "ssh wse";					}		        	ssh.sendCommand2(command, commandPrompt);		        }		        output = ssh.sendCommand2(cmdGetPID, commandPrompt);		        String[] outputPID = output.split("\r\n");		        String servicePID = "";		        for(int j = 0; j<outputPID.length; j++){		        	if(outputPID[j].matches("^[0-9]+$")){		        		servicePID = outputPID[j];		        		break;		        	}		        }		        if(servicePID.equals("")){	    	    	System.out.println(serviceName + " already terminated");	    	    	return 0;		        }		        System.out.println(serviceName + " PID = " + servicePID);		        String[] cmdStop = new String[]{"kill -9 " + servicePID, "/opt/Websense/WebsenseAdmin status | grep \"" + websenseName + "\""};		        String outputString = ssh.sendCommand2(cmdStop, commandPrompt);	    	    if(outputString.contains("not running")){	    	    	System.out.println("Stop " + serviceName + " successfully");	    	    	return 0;	    	    }else{	    	    	System.out.println("fail to stop " + serviceName + " on Linux");	    	    	return -1;	    	    }			}catch(Exception e){				e.printStackTrace();				throw new RuntimeException("fail to stop " + serviceName + " on " + serverIP);			}finally{				ssh.disconnect();			}		}		}	/**	 * start remote websense service, serviceType can be 1-EIM, 2-Logon	 */	public static int startRemoteWebsenseService(int serviceType, String serverIP, String serverOS, String serverAdmin, String serverPwd, String clientOsName) throws Exception{		String serviceName = "";		String websenseName = "";		switch(serviceType){		case 0:{			serviceName = "Websense DC Agent";			websenseName = "Websense DC Agent";			break;		}		case 1:{			serviceName = "Websense EIM Server";			websenseName = "Websense Filtering Service";			break;		}		case 2:{			serviceName = "Websense Logon Agent";			websenseName = "Websense Logon Agent";			break;		}		case 3:{			serviceName = "Websense User Service";			websenseName = "Websense User Service";			break;		}		case 4:{			serviceName = "Websense eDirectory Agent";			websenseName = "Websense eDirectory Agent";			break;		}		case 5:{			serviceName = "Websense RADIUS Agent";			websenseName = "Websense RADIUS Agent";			break;		}		case 6:{			serviceName = "WebsensePolicyServer";			websenseName = "Websense Policy Server";			break;		}		case 7:{			serviceName = "Websense Network Agent";			websenseName = "Websense Network Agent";			break;		}
		case 8:{
			serviceName = "WebsenseManagerTomcat";
			websenseName = "Websense TRITON - Web Security";
			break;
		}		default:			break;		}		if(serverOS.toLowerCase().contains("windows")){			TelnetClientWFAT telnetClient = new TelnetClientWFAT(serverIP, 23);			String[] admins = serverAdmin.split("\\\\");			String commandPrompt = admins[admins.length-1] + ">";			String outputString = "";			String[] cmdStartService = new String[]{"net start \"" + serviceName + "\""};			try{				telnetClient.loginWindowsAndRunCommands(serverAdmin, serverPwd, commandPrompt);				
				if (clientOsName.equalsIgnoreCase("linux")) {
					cmdStartService[0] = cmdStartService[0] + "\r";
				} 
				outputString = telnetClient.sendCommands(cmdStartService, commandPrompt);		
				
				if ((outputString.contains("successfully")) || (outputString.contains("already been started"))){					System.out.println("Start " + serviceName +" successfully");					return 0;				}				else{					System.out.println("fail to start " + serviceName + " on Windows");					return -1;				}			}catch(Exception e){				e.printStackTrace();				throw new RuntimeException("fail to start " + serviceName + " on " + serverIP);			}finally{				telnetClient.disconnect();			}		}		else{	    	SSHClientWFAT ssh = new SSHClientWFAT(serverIP, 22, serverAdmin, serverPwd);	        String commandPrompt = "#";	    	try{		        ssh.connectAndLogin();		        if(serverOS.toLowerCase().contains("appliance")){		        	String command[] = {""};		        	if (serviceName.contains("Network Agent")){						command[0] = "ssh na";					}					else{						command[0] = "ssh wse";					}		        	ssh.sendCommand2(command, commandPrompt);		        }		        String[] cmdStart = new String[]{"/opt/Websense/WebsenseAdmin start", "/opt/Websense/WebsenseAdmin status | grep \"" + websenseName + "\""};		        String outputString = ssh.sendCommand2(cmdStart, commandPrompt);	    	    if(!outputString.contains("not running")){	    	    	System.out.println("Start " + serviceName + " successfully");	    	    	return 0;	    	    }else{	    	    	System.out.println("fail to start " + serviceName + " on Linux");	    	    	return -1;	    	    }			}catch(Exception e){				e.printStackTrace();				throw new RuntimeException("fail to start " + serviceName + " on " + serverIP);			}finally{				ssh.disconnect();			}		}		}		
	/**
	 * Stop the designated Websense service on Windows or Linux machine.
	 * 
	 * @param windowsServiceName : name of service on Windows. 
	 * 							   Please check "Service Name" (not "Display Name") from services.msc, 
	 * 						       For Example "Websense EIM Server" (not "Websense Filtering Service").
	 * @param windowsProcessName : name of process on Windows.
	 * 							   Please check it by task manager,
	 * 							   For example "EIMServer.exe".
	 * @param linuxServiceName : name of service on Linux.
	 * 							  please check it from the output of the "WebsenseDaemonControl" tool,
	 * 						   	  For Example "Filtering Service".
	 * @param linuxProcessName : name of process on Linux
	 * 							 please check it from the output of the command of "ps aux" or "netstat -tulpn".
	 * 							 For example: "EIM".
	 */
	private static void stopWebsenseService(String windowsServiceName, String windowsProcessName, String linuxServiceName, String linuxProcessName) throws Exception{
		if (checkServiceExist(windowsServiceName, linuxServiceName, linuxProcessName)) {
			//Stop service on Windows
			String commandOutput = "";
			if ("\\".equals(File.separator)){
				runLocalCommand.executeWithOutput("TASKKILL /F /IM " + windowsProcessName + " /T");
				System.out.println("Stopping " + windowsServiceName + " Service...");
				for (int i = 0; i < 10; i++) {
					Thread.sleep(3000);
					commandOutput = runLocalCommand.executeWithOutput("sc query \"" + windowsServiceName + "\"");
					if (commandOutput.contains("STOPPED")) {
						System.out.println("successfully stop " + windowsServiceName + " service on this Windows machine.");
						break;
					}
				}
				
				//Special case: sometime Sync service is not stopped, even if the output of the "sc query ..." command includes the keyword "STOPPED".
				if (windowsServiceName.equalsIgnoreCase("WebsenseSyncService")) {
					
					//Get the listening port of Sync Service
					String binfolderPath = ProjectFile.read("conf", "system.properties", "WsFeedbackPath");
					String syncServiceListeningPort = FileHandle.readPropertyFile(FileHandle.returnAbsolutePath(binfolderPath, "syncservice.ini"), "SyncServiceHTTPPort");
					
					boolean sucessfulToStopSyncService = false;
					for (int i = 0; i < 5; i++) {
						//Check if this port is listening
						boolean ifSyncServicePortListening = runLocalCommand.executeWithOutput("netstat -ano | findstr " + syncServiceListeningPort + "  | findstr LISTENING").contains(syncServiceListeningPort);

						//If this port is listening, kill the process
						if (ifSyncServicePortListening) {
					
							String commandToCheckListeningPort = runLocalCommand.matchRegexLineByLine("netstat -ano | findstr " + syncServiceListeningPort + " | findstr LISTENING", ".*:" + syncServiceListeningPort + ".*");
							String[] outputTemp = commandToCheckListeningPort.split("\\s+");
							String pidSyncService = outputTemp[outputTemp.length - 1];
							
							runLocalCommand.executeWithoutOutput("TASKKILL /F /PID " + pidSyncService + " /T");
							Thread.sleep(5000);
							
						} else {
							sucessfulToStopSyncService = true;
							break;
						}
					}
					
					if (sucessfulToStopSyncService == false) {
						throw new RuntimeException("Fail to stop the Sync Service and it is still listening on port " + syncServiceListeningPort);
					}
				}
				
				if (!commandOutput.contains("STOPPED")) {
					throw new Exception("Fail to stop " + windowsServiceName + " service on this Windows machine.");
				} 
			}
			
			//Check on Linux machine
			else if ("/".equals(File.separator)) {
				for(int i=0;i<servicePID.length;i++){				String commandToRun = "kill -9 " + servicePID[i] 
							+ "; export LD_LIBRARY_PATH=" + websenseBinInstalledPath 
							+ "; cd " + websenseBinInstalledPath 
							+ "; ../WebsenseAdmin status | grep \"" + linuxServiceName + "\"";
				commandOutput = runLocalCommand.executeWithOutput(commandToRun);				// runLocalCommand.executeWithOutput(commandToRun);				}
				if (commandOutput.contains("not running")) {               // if(!checkServiceStatus(windowsServiceName, linuxServiceName)){
					System.out.println("successfully stop " + linuxServiceName + " service on this Linux machine.");
				} else {
					throw new Exception("Fail to stop " + linuxServiceName + " service on this Linux machine.");
				}
			} 			
		} else {
			throw new Exception(windowsServiceName + " service is not installed on this machine or not started.");
		}
	}
	
	/**
	 * Start a designated Websense service on Windows or Linux machine.
	 * @param windowsServiceName : name of service on Windows. 
	 * 							   Please check "Service Name" (not "Display Name") from services.msc, 
	 * 						       For Example "Websense EIM Server" (not "Websense Filtering Service").
	 * @param linuxServiceName : name of service on Linux.
	 * 							  please check it from the output of the "WebsenseDaemonControl" tool,
	 * 						   	  For Example "Filtering Service".
	 * @param linuxProcessName : name of process on Linux
	 * 							 please check it from the output of the command of "ps aux" or "netstat -tulpn".
	 * 							 For example: "EIM".
	 */
	private static void startWebsenseService(String windowsServiceName, String linuxServiceName, String linuxProcessName) throws Exception{
		if (checkServiceExist(windowsServiceName, linuxServiceName, linuxProcessName)) {
			//Start EIM service on Windows
			if ("\\".equals(File.separator)){
				runLocalCommand.executeWithOutput("sc start \"" + windowsServiceName + "\"");
				System.out.println("Starting " + windowsServiceName + " Service...");
				
				boolean isStarted = false;
				for (int i = 0; i < 10; i++) {
					Thread.sleep(3000);
					if (checkServiceStatus(windowsServiceName, linuxServiceName) == true) {
						isStarted = true;
						break;
					} 
				}
				
				if (isStarted == true) {
					System.out.println("successfully start " + windowsServiceName + " on this Windows machine.");
				} else{
					throw new Exception("Fail to start " + windowsServiceName + " on this Windows machine.");
				}
				
			}
			//Start EIM Service on Linux machine
			else if ("/".equals(File.separator)) {      				if(linuxProcessName.equals("WsSyncService")){					runLocalCommand.executeWithOutput("export LD_LIBRARY_PATH=" 								+ websenseBinInstalledPath 								+ "; cd " + websenseBinInstalledPath 								+ "; ./WebsenseSyncService.sh start");				System.out.println("Starting WebsenseSyncService.sh ...");					}else{
				runLocalCommand.executeWithOutput("export LD_LIBRARY_PATH=" 
								+ websenseBinInstalledPath 
								+ "; cd " + websenseBinInstalledPath 
								+ "; ./" + linuxProcessName + " -r");
				System.out.println("Starting " + linuxServiceName + " ...");				}
				Thread.sleep(10000);
				if (checkServiceStatus(windowsServiceName, linuxServiceName) == true) {
					System.out.println("successfully start " + linuxServiceName + " on this Linux machine.");
				} else {
					throw new Exception("Fail to start " + linuxServiceName + " on this Linux machine.");
				}
			} 
		} else {
			throw new Exception(windowsServiceName + " is not installed on this machine.");
		}
	}
	
	/**
	 * Check if a service is running
	 * @param windowsServiceName : name of service on Windows. 
	 * 							   Please check "Service Name" (not "Display Name") from services.msc, 
	 * 						       For Example "Websense EIM Server" (not "Websense Filtering Service").
	 * @param linuxServiceName : name of service on Linux.
	 * 							  please check it from the output of the "WebsenseDaemonControl" tool,
	 * 						   	  For Example "Filtering Service".
	 * @return true: running or false : not running
	 * @throws Exception
	 */
	public static boolean checkServiceStatus(String windowsServiceName, String linuxServiceName) throws Exception{
		//Windows
		if (File.separator.equals("\\")) {
			String commandOutput = runLocalCommand.executeWithOutput("sc query \"" + windowsServiceName + "\"");
			if (commandOutput.contains("RUNNING")) {
				return true;
			} else {
				return false;
			} 
		}
		//Linux
		else if (File.separator.equals("/")) {
			String commandOutput = runLocalCommand.executeWithOutput("export LD_LIBRARY_PATH=" 
					+ websenseBinInstalledPath 
					+ "; cd " + websenseBinInstalledPath 
					+ "; ../WebsenseAdmin status | grep \"" + linuxServiceName + "\"");
			 if (!(commandOutput.contains("not running"))) {
				 	return true;
			 } else {
				 	return false;
			 }
		}
		
		else {
			throw new RuntimeException("Fail to judge operating system type.");
		}
		
	}
	
	/**
	 * Restart EIM service
	 *
	 
	 * @param windowsServiceName : name of service on Windows. 
	 * 							   Please check "Service Name" (not "Display Name") from services.msc, 
	 * 						       For Example "Websense EIM Server" (not "Websense Filtering Service").
	 * @param windowsProcessName : name of process on Windows.
	 * 							   Please check it by task manager,
	 * 							   For example "EIMServer.exe".
	 * @param linuxServiceName : name of service on Linux.
	 * 							  please check it from the output of the "WebsenseDaemonControl" tool,
	 * 						   	  For Example "Filtering Service".
	 * @param linuxProcessName : name of process on Linux
	 * 							 please check it from the output of the command of "ps aux" or "netstat -tulpn".
	 * 							 For example: "EIM".
	 */
	private static void restartWebsenseService(String windowsServiceName, String windowsProcessName, String linuxServiceName, String linuxProcessName) throws Exception{
		stopWebsenseService(windowsServiceName, windowsProcessName, linuxServiceName, linuxProcessName);
		Thread.sleep(3000);
		startWebsenseService(windowsServiceName, linuxServiceName, linuxProcessName);
	}
	
	/**
	 * Check the designated service exists on this machine.
	 * @param windowsServiceName : name of service on Windows. 
	 * 							   Please check "Service Name" (not "Display Name") from services.msc, 
	 * 						       For Example "Websense EIM Server" (not "Websense Filtering Service"). 
	 * @param  linuxServiceName : name of service on Linux.
	 * 							  please check it from the output of the "WebsenseDaemonControl" tool,
	 * 						   	  For Example "Filtering Service".
	 * @param linuxProcessName : name of process on Linux
	 * 							 please check it from the output of the command of "ps aux" or "netstat -tulpn".
	 * 							 For example: "EIMServer".
	 * @return true - Service is correctly installed; false - Service is not correctly installed.
	 * @throws Exception
	 */	public static boolean checkStatusLocalSyncService1() throws Exception{		return checkServiceStatus("WebsenseSyncService", "SyncService","WsSyncService");	}			public static boolean checkServiceStatus(String windowsServiceName, String linuxServiceName,String LinuxProcessName) throws Exception{		//Windows		if (checkServiceExist(windowsServiceName, linuxServiceName, LinuxProcessName)) {					if (File.separator.equals("\\")) {			String commandOutput = runLocalCommand.executeWithOutput("sc query \"" + windowsServiceName + "\"");			if (commandOutput.contains("RUNNING")) {				return true;			} else {				return false;			} 		}		//Linux		else if (File.separator.equals("/")) {			String commandOutput = runLocalCommand.executeWithOutput("export LD_LIBRARY_PATH=" 					+ websenseBinInstalledPath 					+ "; cd " + websenseBinInstalledPath 					+ "; ../WebsenseAdmin status | grep \"" + linuxServiceName + "\"");			 if (!(commandOutput.contains("not running"))) {				 	return true;			 } else {				 	return false;			 }		}				else {			throw new RuntimeException("Fail to judge operating system type.");		}		}		return false;			}		
	public static boolean checkServiceExist(String windowsServiceName, String linuxServiceName, String linuxProcessName) throws Exception{
		try{
			//Check on Windows machine
			if ("\\".equals(File.separator)){
				String commandOutput = runLocalCommand.executeWithOutput("sc query \"" + windowsServiceName + "\"");
				if (commandOutput.contains("FAILED")) {
					System.out.println(windowsServiceName + ": is NOT installed on this Windows machine.");
					return false;
				} else {
					System.out.println(windowsServiceName + ": is installed on this Windows machine.");
					return true;
				} 
			}
			//Check on Linux machine
			else if ("/".equals(File.separator)) {
				websenseBinInstalledPath = ProjectFile.read("conf", "system.properties", "WsFeedbackPath");
				String commandToRun = "export LD_LIBRARY_PATH=" + websenseBinInstalledPath 
						+ "; cd " + websenseBinInstalledPath 
						+ "; ../WebsenseAdmin status | grep \"" + linuxServiceName + "\"";
				String commandOutput = runLocalCommand.executeWithOutput(commandToRun);
				if (commandOutput.contains(linuxServiceName)) {
					
					String getEimPID = "";
					if(linuxProcessName.equals("WsSyncService")){
						getEimPID = "ps aux | grep SyncService | grep -v grep | awk '{print $2 \"x\"}'";
					} else {
						getEimPID = "ps aux | grep " + linuxProcessName + " | grep -v grep | awk '{print $2 \"x\"}'";
					}
															String outPut=runLocalCommand.executeWithOutput(getEimPID);					System.out.println("The pid is:"+outPut);					servicePID=outPut.split("x");
					System.out.println(linuxServiceName + ": is installed on this Windows machine.");
					return true;
				} else {
					System.out.println(linuxServiceName + ": is NOT installed on this Windows machine.");
					return false;
				}
			} 
			else {
				throw new Exception("Fail to recognize operation system type (Must Windows or Linux).");
			} 
		} catch(Exception e){
			e.printStackTrace();
			throw new Exception("Fail to check service status on Windows or Linux machine.");
		}	
	}
	
	/**
	 * Restart local log server on test machine. Don't support remote log server.
	 */
	public static void restartLocalLogServer() throws Exception{
		restartWebsenseService("WebsenseLogServer", "LogServer.exe", "", "");
	}
	
	/**
	 * Stop local log server on test machine. Don't support remote log server.
	 * @throws Exception
	 */
	public static void stopLocalLogServer() throws Exception{
		stopWebsenseService("WebsenseLogServer", "LogServer.exe", "", "");
	}
	
	/**
	 * Start local log server on test machine. Don't support remote log server.
	 * @throws Exception
	 */
	public static void startLocalLogServer() throws Exception{
		startWebsenseService("WebsenseLogServer", "", "");
	}	public static void startEIMService() throws Exception{		startWebsenseService("Websense EIM Server", "Filtering Service", "EIMServer");	}		public static void startPolicyDB() throws Exception{		startWebsenseService("WebsensePolicyDB", "Policy Database", "PgSetup");	}				public static void startPolicyBroker() throws Exception{		startWebsenseService("WebsensePolicyBroker", "Policy Broker", "BrokerService");	}
	/**
	 * Start local DAS service. Don't support the remote DAS.
	 * @throws Exception
	 */
	public static void startLocalDAS() throws Exception{
		startWebsenseService("WebsenseDAService", "Directory Agent", "DAS");
	}
	
	/**
	 * Stop local DAS service. Don't support the remote DAS.
	 * @throws Exception
	 */
	public static void stopLocalDAS() throws Exception{
		stopWebsenseService("WebsenseDAService", "DAS.exe", "Directory Agent", "DAS");
	}
	
	/**
	 * Restart local DAS server on test machine. Don't support remote DAS server.
	 * @throws Exception
	 */
	public static void restartLocalDAS() throws Exception{
		restartWebsenseService("WebsenseDAService", "DAS.exe", "Directory Agent", "DAS");
	}
	
	/**
	 * Start local sync service. Don't support remote Sync Service.
	 * @throws Exception
	 */
	public static void startLocalSyncService() throws Exception{
		startWebsenseService("WebsenseSyncService", "SyncService", "WsSyncService");
	}
	
	/**
	 * Stop local sync service. Don't support remote Sync Service.
	 * @throws Exception
	 */
	public static void stopLocalSyncService() throws Exception{
		stopWebsenseService("WebsenseSyncService", "WebsenseSyncService.exe", "SyncService", "WsSyncService");
	}
	
	/**
	 * Restart local Sync Service server on test machine. Don't support remote SS.
	 * @throws Exception
	 */
	public static void restartLocalSyncService() throws Exception{
		restartWebsenseService("WebsenseSyncService", "WebsenseSyncService.exe", "SyncService", "WsSyncService");
	}

	
	/**
	 * Return the running status of local sync service. Don't support remote SS.
	 * @return true : Running or false : Stopped.
	 * @throws Exception
	 */
	public static boolean checkStatusLocalSyncService() throws Exception{
		return checkServiceStatus("WebsenseSyncService", "SyncService");
	}
	
	/**
	 * Return the running status of local filtering service. Don't support remote SS.
	 * @return true : Running or false : Stopped.
	 * @throws Exception
	 */
	public static boolean checkStatusLocalEimService() throws Exception{
		return checkServiceStatus("Websense EIM Server", "Filtering Service");
	}
	
	/**
	 * Return the running status of local DAS. Don't support remote DAS.
	 * @return true : Running or false : Stopped.
	 * @throws Exception
	 */
	public static boolean checkStatusLocalDasService() throws Exception{
		return checkServiceStatus("WebsenseDAService", "Directory Agent");
	}
	
	/**
	 * Restart local Filtering Service on test machine. Don't support remote Filtering Service.
	 * @throws Exception
	 */
	public static void restartLocalEIM() throws Exception{
		restartWebsenseService("Websense EIM Server", "EIMServer.exe", "Filtering Service", "EIMServer");
	}
	
	/**
	 * Check WSE services status
	 * @return true(all services are started)|false(not all services are started)
	 * @throws Exception
	 */
	public static boolean checkWebsenseServiceStatus() throws Exception{
		String commandToRun = null;
		String commandOutput = null;
		boolean bStarted = false;

		String path = ProjectFile.readFromSystemProperties("WsInstallPath");		String libraryPath = ProjectFile.readFromSystemProperties("WsBackupPath");
		try{
			//Check services status on Windows
			if ("\\".equals(File.separator)){
				commandToRun = "cd \"" + path + "\" && WebsenseAdmin.exe status";
				commandOutput = runLocalCommand.executeWithOutput(commandToRun);
				if (commandOutput.contains("not running")) {
					bStarted = false;
					System.out.println("Not all services are started.");
				} else {
					bStarted = true;
					System.out.println("All services are started.");
				} 
			}
			//Check on Linux machine
			else if ("/".equals(File.separator)) {
				commandToRun = "export LD_LIBRARY_PATH=" + libraryPath + "; cd " + path 
							+ "; ./WebsenseAdmin status";
				commandOutput = runLocalCommand.executeWithOutput(commandToRun);
				if (commandOutput.contains("not running")) {
					bStarted = false;
					System.out.println("Not all services are started.");
				} else {
					bStarted = true;
					System.out.println("All services are started.");
				} 
			} 
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("Failed to check services status on this server.");
		}
		return bStarted;	
	}
	public static boolean startAllWebsenseService() throws Exception{
		String commandToRun = null;
		boolean bStart = false;

		String path = ProjectFile.readFromSystemProperties("WsInstallPath");				String libraryPath = ProjectFile.readFromSystemProperties("WsBackupPath");
		try{
			//Check services status on Windows
			if ("\\".equals(File.separator)){
				commandToRun = "cd \"" + path + "\" && WebsenseAdmin.exe start";			
				runLocalCommand.executeWithOutput(commandToRun);
			}
			//Check on Linux machine
			else if ("/".equals(File.separator)) {
				commandToRun = "export LD_LIBRARY_PATH=" + libraryPath + "; cd " + path 
							+ "; ./WebsenseAdmin start";
				runLocalCommand.executeWithOutput(commandToRun);
			} 
			bStart = checkWebsenseServiceStatus();
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("Failed to check services status on this server.");
		}
		return bStart;	
	}
	public static boolean stopAllWebsenseService() throws Exception{
		String commandToRun = null;
		boolean bStopped = false;

		String path = ProjectFile.readFromSystemProperties("WsInstallPath");		String libraryPath = ProjectFile.readFromSystemProperties("WsBackupPath");
		try{
			//Check services status on Windows
			if ("\\".equals(File.separator)){
				commandToRun = "cd \"" + path + "\" && WebsenseAdmin.exe stop";			
				runLocalCommand.executeWithOutput(commandToRun);
			}
			//Check on Linux machine
			else if ("/".equals(File.separator)) {
				commandToRun = "export LD_LIBRARY_PATH=" + libraryPath + "; cd " + path 
							+ "; ./WebsenseAdmin stop";
				runLocalCommand.executeWithOutput(commandToRun);
			} 
			bStopped = checkWebsenseServiceStatus();
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("Failed to check services status on this server.");
		}
		return bStopped;	
	}		/**	 * Stop Manager on local box.	 * @throws Exception	 */	public static void stopLocalManager() throws Exception{		stopWebsenseService("WebsenseManagerTomcat", "WebsenseManagerTomcat.exe", "", "");	}	/**	 * Start Manager on local box.	 * @throws Exception	 */	public static void startLocalManager()throws Exception{		startWebsenseService("WebsenseManagerTomcat", "", "");	}    /**     * Restart local manager     * @throws Exception     */	public static void restartLocalManager()throws Exception{		restartWebsenseService("WebsenseManagerTomcat", "WebsenseManagerTomcat.exe", "", "");	}
	
	/**
	 * Judge if PB locates on local test machine or remote machine.
	 * 
	 * @return true: local machine; false: remote machine.
	 */
	public static boolean isPbExist() throws Exception{
		
		return ServiceHandle.checkServiceExist("WebsensePolicyBroker", "Policy Broker", "BrokerService");
	}	/**	 * start testLogServer	*/	public static int startTestLogServer() throws Exception{		String cmd1 = "\"C:\\Program Files (x86)\\Websense\\Web Security\\bin\\TestLogServer.exe\" -port 55805";				String[] cmdStartDebug = new String[]{cmd1};						String LogServerIP = ProjectFile.read("conf", "system.properties", "LogServerIP");		String LSTelUser = ProjectFile.read("conf", "system.properties", "LSUser");		String LSTelPwd = ProjectFile.read("conf", "system.properties", "LSPwd");		TelnetClientWFAT telnetClient = new TelnetClientWFAT(LogServerIP, 23);		String commandPrompt = "Administrator>";		String outputString = "";		try {			telnetClient.loginWindowsAndRunCommands(LSTelUser, LSTelPwd, commandPrompt);			//Connect to remote Windows client by telnet protocol and execute commands			 			outputString = telnetClient.sendCommandsLastTimeOut(cmdStartDebug, commandPrompt,3);			if ((outputString.contains("success")) ){				System.out.println("start TestLogServer succesfully");								//Thread.sleep(10*1000);								if( startLogServerDebug() < 0){					throw new Exception("fail to start logserver in debug mode.");				}								System.out.println("wait 10s, wait logserver write debug.txt ");								Thread.sleep(10*1000);								return 0;			}else{				System.out.println("fail to start TestLogServer");				return -1;			}		} catch (Exception e) {			e.printStackTrace();			throw new RuntimeException("Fail to telnet LogServer box or start TestLogServer");		} finally {			telnetClient.disconnect();		}	}	/*	* start/stop Websense Reporter Scheduler	*/	public static int startReporterScheduler(boolean bStart) throws Exception	{		String[] cmdStart = new String[]{"net start \"Websense Reporter Scheduler\""};		String[] cmdStop = new String[]{"net stop \"Websense Reporter Scheduler\""};		String LogServerIP = ProjectFile.read("conf", "system.properties", "LogServerIP");		String LSTelUser = ProjectFile.read("conf", "system.properties", "LSUser");		String LSTelPwd = ProjectFile.read("conf", "system.properties", "LSPwd");		TelnetClientWFAT telnetClient = new TelnetClientWFAT(LogServerIP, 23);		String commandPrompt = "Administrator>";		String outputString = "";		try {			telnetClient.loginWindowsAndRunCommands(LSTelUser, LSTelPwd, commandPrompt);			if(bStart) //start Reporter Scheduler			{				//Connect to remote Windows client by telnet protocol and execute commands				outputString = telnetClient.sendCommands(cmdStart, commandPrompt);				if ((outputString.contains("successfully")) || (outputString.contains("already been started"))){					System.out.println("start Websense Reporter Scheduler succesfully");					return 0;				}else{					System.out.println("fail to start Websense Reporter Scheduler");					return -1;				}			}				else //stop Websense Reporter Scheduler			{				outputString = telnetClient.sendCommands(cmdStop, commandPrompt);				if ((outputString.contains("successfully")) || (outputString.contains("is not started"))){					System.out.println("Stop Websense Reporter Scheduler successfully");					return 0;				}else{					System.out.println("fail to stop Websense Reporter Scheduler");					return -1;				}			}		} catch (Exception e) {			e.printStackTrace();			throw new RuntimeException("Fail to telnet LogServer box or start/stop Websense Reporter Scheduler");		} finally {			telnetClient.disconnect();		}	}
}
