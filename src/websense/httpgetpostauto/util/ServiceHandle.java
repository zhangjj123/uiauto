package websense.httpgetpostauto.util;
import java.io.File;
public class ServiceHandle {
	private static String websenseBinInstalledPath;
	private static String[] servicePID;
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
	}
	/**
		case 8:{
			serviceName = "WebsenseManagerTomcat";
			websenseName = "Websense TRITON - Web Security";
			break;
		}
			
			String[] cmdKillProcess = new String[]{"taskkill /f /im " + serviceName + ".exe"};
			if (clientOsName.equalsIgnoreCase("linux")) {
				cmdKillProcess[0] = cmdKillProcess[0] + "\r";
			} 
			
		case 8:{
			serviceName = "WebsenseManagerTomcat";
			websenseName = "Websense TRITON - Web Security";
			break;
		}
				if (clientOsName.equalsIgnoreCase("linux")) {
					cmdStartService[0] = cmdStartService[0] + "\r";
				} 
				outputString = telnetClient.sendCommands(cmdStartService, commandPrompt);
				
				if ((outputString.contains("successfully")) || (outputString.contains("already been started"))){
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
				for(int i=0;i<servicePID.length;i++){
							+ "; export LD_LIBRARY_PATH=" + websenseBinInstalledPath 
							+ "; cd " + websenseBinInstalledPath 
							+ "; ../WebsenseAdmin status | grep \"" + linuxServiceName + "\"";
				commandOutput = runLocalCommand.executeWithOutput(commandToRun);
				if (commandOutput.contains("not running")) {
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
			else if ("/".equals(File.separator)) {
				runLocalCommand.executeWithOutput("export LD_LIBRARY_PATH=" 
								+ websenseBinInstalledPath 
								+ "; cd " + websenseBinInstalledPath 
								+ "; ./" + linuxProcessName + " -r");
				System.out.println("Starting " + linuxServiceName + " ...");
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
	 */
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
	}
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

		String path = ProjectFile.readFromSystemProperties("WsInstallPath");
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

		String path = ProjectFile.readFromSystemProperties("WsInstallPath");
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

		String path = ProjectFile.readFromSystemProperties("WsInstallPath");
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
	}
	
	/**
	 * Judge if PB locates on local test machine or remote machine.
	 * 
	 * @return true: local machine; false: remote machine.
	 */
	public static boolean isPbExist() throws Exception{
		
		return ServiceHandle.checkServiceExist("WebsensePolicyBroker", "Policy Broker", "BrokerService");
	}
}