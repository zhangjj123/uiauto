package websense.httpgetpostauto.util;

import java.io.IOException;










/**
 * Description: class used to connect telnet server, then read the response from telnet server.
 * 
 * @author Administrator
 *
 */
public class TelnetClientWFAT {

	private TelnetClient telnet = new TelnetClient();   
	private InputStream in;   
	private PrintWriter out;   
	private String prompt; 
	private Logger telnetClientLogger;
	private String ip;
	private int port;
	
	/**
	 * Description: initialize this class. 
	 * 
	 * @param ip :telnet server ip address
	 * @param port :telnet server port 
	 */
	public TelnetClientWFAT( String serverIp, int serverPort) {   
		try {
			this.ip = serverIp;
			this.port = serverPort;
			
			//invoke log4j to log
	        telnetClientLogger = Log4j.logger(TelnetClientWFAT.class.getName());      
			
			telnet.connect( ip, port );
	        // InputStream to deliver command
	        this.in = telnet.getInputStream();
	        // OutStream to accept telnet server response
	        this.out = new PrintWriter(new OutputStreamWriter(telnet.getOutputStream(),"UTF-8"));
	        telnetClientLogger.info("Connect to server: " + ip);
	        this.cmdPattern = ">";
	    } catch ( Exception e ) { 
	    	telnetClientLogger.error("Fail to connect to: " + ip + " or initialize class.");
	        e.printStackTrace();  
	    }   
	    }   
	  
	  
	    /**  
	     * Description: keep reading response of telnet server, until matching the pattern.
	     *   
	     * @param pattern : Keyword to be matched from telnet server response. When defining paterns, please note:
	     * 1. No need to include ending blank space, for example "password: ";
	     * 2. Be careful to Non-English character, for example: "���������������?" != "dministrator". Integer of char should be used to match.
	     * @return  :response (string) from telnet server, until matching the pattern. 
	     */  
	public String readUntil( String pattern ) {   
		try {   
	        String rtnStr = null;
	    } catch ( Exception e ) {   
	        e.printStackTrace();   
	    }   
	    return null;   
		        			//System.out.println(sb.toString());
	    /**  
	     * Description: send a string to telnet server. This function can be used with "readUntil" method, for example:
	     * 
	     * 	readUntil("login:")
	     *  write("jiren")
	     *  readUntil("password:")
	     *  write("123456")
	     *   
	     * @param requeString  : String to be sent
	     */  
	public void write( String requeString ) {   
	    try {   
	       out.println( requeString );
	        telnetClientLogger.info("The command * " + requeString + " * is sent.");
	        out.flush();   
	    } catch ( Exception e ) {
	    	telnetClientLogger.error("Fail to send command * " + requeString +" *");
	        e.printStackTrace();   
	    }   
	    }   
	  
	    /**  
	     * Description: send commands to telnet server, then return the result (String) of last command. Don't use this method with readUntil.
	     *   
	     * @param commands :commands sent to telnet server. They will be executed one by one. 
	     * @param commandPrompt :After login the telnet server, the prompt (String) of command line, for example: "#", "# ", "$", ">",  ": ", etc.
	     * When using commandPrompt parameter, be careful:
	     * 1. No need to include ending blank space, for example "password: ";
	     * 2. Be careful to Non-English character, for example: "���������������?" != "dministrator". Integer of char should be used to match.
	     * @return  The execution result (String) of last command
	     * @exception which command is failed to be executed
	     */  
	public String sendCommands( String[] commands, String commandPrompt) throws Exception {   
		
		this.prompt = commandPrompt;
		int i = 0;
		
		// execute commands (except last command) one by one and don't return execution result.
		for (; i < commands.length - 1; i++) {
			
			try {
				write(commands[i]);
				telnetClientLogger.debug("Send command: " + commands[i]);
				readUntil(prompt);
			}catch(Exception e){
				throw (new Exception("Fail to execute command: " + commands[i]));
			}
		}
		
		// execute the last command and return the result (String). 
		try {
			write(commands[i]);
			return readUntil(prompt);	
		} catch (Exception e) {
			throw (new Exception("Fail to execute command: " + commands[i]));
		}
	    }   
	public String sendCommandsLastTimeOut( String[] commands, String commandPrompt, int persistInterval) throws Exception {   
	    /**  
	     * Close connection to telnet server.  
	     */  
	public void disconnect() {   
	    try {   
	        telnet.disconnect();
	        telnetClientLogger.info("Disconnect telnet connection from " + ip);
	    } catch ( Exception e ) {   
	        telnetClientLogger.error("Fail to close telnet connection: " + ip);
	    	e.printStackTrace();   
	    }   
	}
	
	/**
	 * Login Windows operation system and execute commands
	 * 
	 * @param username
	 */
	public void loginWindowsAndRunCommands(String username, String password, String commandPrompt) throws Exception{
		try{
			if (localOsName.startsWith("windows")) {
				//Enter username and password
				this.readUntil("login:");
				this.write(username);
				this.readUntil("password:");
				this.write(password);
				this.readUntil(commandPrompt);
			} else if (localOsName.startsWith("linux")){
				//Enter username and password
				this.readUntil("login:");
				this.write(username + "\r");
				this.readUntil("password:");
				this.write(password + "\r");
				this.readUntil(commandPrompt);
				//this.readUntil("Failed");
			} else {
				throw new RuntimeException("Only support execute this function in Windows and Linux. The mac os is not verified.");
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}