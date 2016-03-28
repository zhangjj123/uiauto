package websense.httpgetpostauto.util;
import static org.testng.AssertJUnit.assertTrue;

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
	 * Clear all configurations in policy database.
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
		}
	}
	
	/**
	 * Clean the session of manager, in order to avoid that the SA account cannot login.
	 */
	public void clearManagerSession() {
