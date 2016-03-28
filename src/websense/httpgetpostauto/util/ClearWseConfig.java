package websense.httpgetpostauto.util;
import static org.testng.AssertJUnit.assertTrue;import java.io.File;import java.net.Inet4Address;import java.net.UnknownHostException;import org.apache.log4j.Logger;

public class ClearWseConfig {

	private Logger log4jLogger;
	private String binFolderPath;
	private boolean isLocalPB;

	public ClearWseConfig() throws Exception {

		log4jLogger = Log4j.logger(ClearWseConfig.class.getName());

		// Find the path of manager configuration file
		try {
			binFolderPath = ProjectFile.read("conf", "system.properties",
					"WsFeedbackPath");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"Fail to read the value of WsFeedbackPath parameter from the system.properties file.");
		}
		
		//Judge if PB locates on test machine.
		try {
//			isLocalPB = ServiceHandle.isPbExist();
		} catch (Exception e) {
			throw new RuntimeException("Fail to judge if Policy Broker locates on local machine.");
		}
	}

	/**
	 * Clear all configurations in policy database.	 * @throws Exception 
	 */
	public void clearPolicyDatabase(){
		
		//If Policy Broker doesn't exist on local test machine, don't clear policy database.
		if (!isLocalPB) {
			return;
		}			
		
		// Get IP address
		String ipAddr = "";
		try {
			ipAddr = Inet4Address.getLocalHost().getHostAddress();
			log4jLogger.debug("Loca IP address is " + ipAddr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new RuntimeException("Fail to get local IPv4 address");
		}

		// Commands
		String commandClear = "";
		String commandResetToken = "";
		if (File.separator.equals("/")) {
			String commandSwitchFoler = "export LD_LIBRARY_PATH="
					+ binFolderPath + "; cd " + binFolderPath + "; ";
			commandClear = commandSwitchFoler + "./PgSetup --format > 1.txt; cat 1.txt";
			commandResetToken = commandSwitchFoler + "./PgSetup --reset-token "
					+ ipAddr + " > 2.txt; cat 2.txt";
		} else if (File.separator.equals("\\")) {
			String commandSwitchFoler = "cd " + binFolderPath + " && ";
			commandClear = commandSwitchFoler + "PgSetup.exe --format";
			commandResetToken = commandSwitchFoler
					+ "PgSetup.exe --reset-token " + ipAddr;
		} else {
			throw new RuntimeException("Only support Windows and Linux");
		}

		// Execute commands
		String commandOutput;
		try {
			commandOutput = runLocalCommand.executeWithOutput(commandClear);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Fail to execute the command: "
					+ commandClear);
		}

		if (commandOutput.contains("Policy Database Update Complete")) {
			log4jLogger.info("Successfully clear the policy database");

			try {
				commandOutput = runLocalCommand
						.executeWithOutput(commandResetToken);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Fail to execute the command: "
						+ commandResetToken);
			}

			if (commandOutput.contains("Set this Token in config.xml")) {
				log4jLogger
						.info("Successfully to reset the token in policy database");
			} else {
				throw new RuntimeException(
						"Fail to reset the token in policy database!");
			}

		} else {
			throw new RuntimeException("Fail to clear the policy database!");
		}				try{					boolean PBstatus=ServiceHandle.checkServiceStatus("WebsensePolicyDB", "Policy Database");				if (!PBstatus){						ServiceHandle.startPolicyDB();						log4jLogger.info("PolicyDB was down,start it #####");					}		}catch(Exception e){					}
	}
	
	/**
	 * Clean the session of manager, in order to avoid that the SA account cannot login.
	 */
	public void clearManagerSession() {		if (isLocalPB) {			clearLocalManagerSession();					}		else			clearRemoteManagerSession();	}	public void clearLocalManagerSession(){				//Get the path of the psql file.		String postgresPath = FileHandle.returnAbsolutePath(binFolderPath, "postgres");		String postgresBinPath = FileHandle.returnAbsolutePath(postgresPath, "bin");		String psqlPath = FileHandle.returnAbsolutePath(postgresBinPath, "psql");		log4jLogger.info("The path of the psql file is: " + psqlPath);					//The psql commands		String psqlCommand = "psql -U postgres -d wspolicy -p 6432 -c \"SELECT \\\"FreeChangelist\\\"(\\\"ID\\\") FROM \\\"Changelists\\\"\"";		if (File.separator.equals("/")) {			psqlCommand = "export LD_LIBRARY_PATH=" + binFolderPath + "; cd " + postgresBinPath + "; ./" + psqlCommand;		} else if (File.separator.equals("\\")) {			psqlCommand = "cd " + postgresBinPath + " && " + psqlCommand;		}				//Run the psql		boolean cleanSuccessful = false;		for (int i = 0; i < 5; i++) {			try {				String commandOutput = runLocalCommand.executeWithOutput(psqlCommand);				if (commandOutput.contains("0 rows")) {					//If successful, break the loop.					cleanSuccessful = true;					log4jLogger.info("Successfully clean the manager session.");					break;				} 			} catch (Exception e) {				e.printStackTrace();				throw new RuntimeException("Fail to clean the manager session.");			}		}				//If fail to clean session, throw exception		if (!cleanSuccessful) {			throw new RuntimeException("Fail to clean the manager session.");		}	}	public void clearRemoteManagerSession(){				try{			String PBIP = ProjectFile.read("conf", "system.properties", "PrimaryPBIP");			String PBTelUser = ProjectFile.read("conf", "system.properties", "PrimaryPBMachineUser");			String PBTelPwd = ProjectFile.read("conf", "system.properties", "PrimaryPBMachinePassword");			String PBOS = ProjectFile.read("conf", "system.properties", "PrimaryPBOS");			String PBBinPath = ProjectFile.read("conf", "system.properties", "PrimaryPBMachineBinPath");			if( PBIP==null || PBTelUser==null || PBTelPwd==null || PBOS==null ||PBBinPath==null ){				log4jLogger.warn("Please add PrimaryPBIP,PrimaryPBMachineUser,PrimaryPBMachinePassword,"						+ "PrimaryPBOS,PrimaryPBMachineBinPath these 5 items to your properties file.");				return;			}							if( PBBinPath.endsWith("/") || PBBinPath.endsWith("\\") )				PBBinPath = PBBinPath.substring(0,PBBinPath.length()-1);						String postgresBinPath;						String psqlCommand = " -U postgres -d wspolicy -p 6432 -c \"SELECT \\\"FreeChangelist\\\"(\\\"ID\\\") FROM \\\"Changelists\\\"\"";						boolean cleanSuccessful = false;								if( PBOS.equals("0")){				// windows				postgresBinPath = PBBinPath+"\\postgres\\bin";				psqlCommand = "\""+postgresBinPath + "\\psql.exe\"" + psqlCommand;					String[] cmdCommand = new String[]{psqlCommand};				TelnetClientWFAT telnetClient = new TelnetClientWFAT(PBIP, 23);				String[] user = PBTelUser.split("\\\\");				String commandPrompt = user[user.length-1]+">";							String outputString = "";								try {									telnetClient.loginWindowsAndRunCommands(PBTelUser, PBTelPwd, commandPrompt);					for (int i = 0; i < 5; i++) {						outputString = telnetClient.sendCommands(cmdCommand, commandPrompt);						log4jLogger.debug(outputString);						if (outputString.contains("0 rows")) {							//If successful, break the loop.							cleanSuccessful = true;							log4jLogger.info("Successfully clean the manager session.");							break;						}					}				}catch(Exception e) {					e.printStackTrace();				}finally{					telnetClient.disconnect();				}			}			else if(PBOS.equals("1")){						// linux         				postgresBinPath = PBBinPath+"/postgres/bin";				psqlCommand = "export LD_LIBRARY_PATH=" + PBBinPath + "; cd " +				               postgresBinPath + "; ./" +"psql "+ psqlCommand;				SSHClientWFAT ssh = new SSHClientWFAT(PBIP, 22, PBTelUser, PBTelPwd);				String[] cmdCommand = new String[]{psqlCommand};		        String commandPrompt = "#";		        try {		        	ssh.connectAndLogin();		        	for (int i = 0; i < 5; i++) {	        			       				        String outputString = ssh.sendCommand2(cmdCommand, commandPrompt);						log4jLogger.debug(outputString);				        if (outputString.contains("0 rows")) {							//If successful, break the loop.							cleanSuccessful = true;							log4jLogger.info("Successfully clean the manager session.");							break;						} 					}				} catch (Exception e) {					e.printStackTrace();				}finally{					ssh.disconnect();				}							}			else if(PBOS.equals("2")){				//appliance				//TODO						}						if (!cleanSuccessful) {				throw new RuntimeException("Fail to clean the manager session.");			}		}catch(Exception e){			e.printStackTrace();				}	}
	public static void clearCustomPolicyManagemenData(String pbIp, String username, String password) {		// mac, linux , unix use the same path format	String toolsBase = "/opt";	String binaryfile="WsPolicyManagement";	if (OSValidator.isWindows()) {		 toolsBase = "C:";		 binaryfile = "WsPolicyManagement.exe";	} 	String binaryPath = toolsBase+File.separator+"WSE_Auto_Tools"+File.separator+"Supplemental"+File.separator+binaryfile;			//above tools automatically prepared by Jenkins in CI 	//for debug using, you could download Supplemental package , extract to C:\WSE_Auto_Tools\Supplemental or /opt/WSE_Auto_Tools/Supplemental	//the WebsensePing path is C:\WSE_Auto_Tools\Supplemental\WebsensePing or /opt/WSE_Auto_Tools/Supplemental/WebsensePing		File f= new File(binaryPath);	assertTrue("check "+binaryPath+" exists.",f.exists());				String cmd = binaryPath + "  --host "+pbIp+" --username "+ username+" --password "+password+ " --nuke" ;	String outputString= "";	try {		outputString = runLocalCommand.executeWithOutput(cmd, 5);			} catch (Exception e) {		e.printStackTrace();	}finally{		System.out.print(outputString);	}			}}
