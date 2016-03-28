package websense.httpgetpostauto.util;

import java.io.IOException;import java.io.InputStream;   import java.io.InterruptedIOException;import java.io.OutputStreamWriter;import java.io.PrintWriter;import java.nio.channels.ClosedByInterruptException;import java.util.concurrent.Callable;import java.util.concurrent.FutureTask;
import org.apache.commons.net.telnet.TelnetClient;



import websense.httpgetpostauto.util.Log4j;



import org.apache.log4j.Logger;

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
	private int port;	private String cmdPattern;
	
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
	     * 2. Be careful to Non-English character, for example: "ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿?" != "dministrator". Integer of char should be used to match.
	     * @return  :response (string) from telnet server, until matching the pattern. 
	     */  
	public String readUntil( String pattern ) {   
		try {   			this.cmdPattern = pattern;
	        String rtnStr = null;	        //start a new sub thread to read telnet server return string with io blocked	        TelnetReadThread trt = new TelnetReadThread();	        FutureTask<String> trTask = new FutureTask<String>(trt);	        Thread trThread = new Thread(trTask);	        trThread.start();	        //the main thread calculate the time elapse, if io blocked exceed 60s, stop the sub thread	        long startLoopTime = System.currentTimeMillis();	        while (true){	        	Thread.sleep(1);	        	// Calculate the loop interval.	        	long endLoopTime = System.currentTimeMillis();	        	if (endLoopTime - startLoopTime > 120000) 	        		break; 	        	if(!trThread.isAlive()){	        		//if sub thread has completed normally, get its return value	        		rtnStr = trTask.get();	        		break;	        	}	        }	        if(rtnStr == null){	        	//interrupt sub thread if it's blocked and not completed normally over 60s	        	trThread.interrupt();	        	if(trThread.isAlive()){	        		telnetClientLogger.info("sub thread is not interupted in first try.\r\n");	        		Thread.sleep(200);	        		trThread.interrupt();	        	}	        	throw new Exception("Fail to match the pattern *" + pattern + "* in 60 seconds.\r\n");	        }	        return rtnStr;	        	
	    } catch ( Exception e ) {   
	        e.printStackTrace();   
	    }   
	    return null;   	}   	//embedded class used by function readUntil	private class TelnetReadThread implements Callable<String>{				public String call() throws IOException{			//read telnet server return string with io blocked	        try{		        char lastChar = cmdPattern.charAt(cmdPattern.length() - 1 );   		        StringBuffer sb = new StringBuffer();		        char ch = ( char ) in.read();		        while (true) {		        	// If returned String > 5120 bytes, throw exception. Otherwise keep returning String.		        	if (sb.length() < 10240)		        	{		        		if ((int)ch != -1 && (int)ch != 0) {		        			sb.append( ch );
		        			//System.out.println(sb.toString());						}		        	}else{						System.out.println("Returned string is too long (>10240 byte) to match the pattern *" + cmdPattern + "*");						return sb.toString();		        	}		        	// Match the pattern		        	String userPrompt = cmdPattern.substring(0, cmdPattern.length() - 1 );		        	if ((ch == lastChar && sb.toString().toLowerCase().endsWith( cmdPattern.toLowerCase() )) || (ch == lastChar && sb.toString().toLowerCase().contains(userPrompt.toLowerCase()))) {      		        		telnetClientLogger.info("The prompt * " + cmdPattern + " * is matched.");		        		telnetClientLogger.debug("The returned String from telnet server: " + ip +" is * " + sb.toString() + " *");		        		return sb.toString();   		        	}  		        	if(Thread.interrupted()){		                break;		            }		        	//read char by char		        	ch = ( char ) in.read();  		        } 		        return null;	        }catch(ClosedByInterruptException er){	        	System.out.println("sub thread is interrupted while read telnet server return string.\r\n");	        	return null;	        }catch(InterruptedIOException eio){	        	System.out.println("sub thread is interrupted while io read.\r\n");	        	return null;	        }catch(SecurityException es){	        	System.out.println("sub thread cannot be interrupted.\r\n");	        	es.printStackTrace();	        	return null;	        }catch(Exception e){	        	System.out.println("sub thread is interrupted.\r\n");	        	e.printStackTrace();	        	return null;	        }		}			}	
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
	     * 2. Be careful to Non-English character, for example: "ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿?" != "dministrator". Integer of char should be used to match.
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
	public String sendCommandsLastTimeOut( String[] commands, String commandPrompt, int persistInterval) throws Exception {   		this.prompt = commandPrompt;		int i = 0;				// execute commands (except last command) one by one and don't return execution result.		for (; i < commands.length - 1; i++) {			try {				write(commands[i]);				telnetClientLogger.debug("Send command: " + commands[i]);				readUntil(prompt);			}catch(Exception e){				throw (new Exception("Fail to execute command: " + commands[i]));			}		}				// execute the last command and wait time out 1 minute		try {			write(commands[i]);			Thread.sleep(persistInterval*1000);			return "success";			} catch (Exception e) {			throw (new Exception("Fail to execute command: " + commands[i]));		}	} 	  
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
	 * @param username	 * @param password	 * @param commandPrompt	 * @throws Exception
	 */
	public void loginWindowsAndRunCommands(String username, String password, String commandPrompt) throws Exception{
		try{			String localOsName = System.getProperty("os.name").toLowerCase();
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
	}		public void readUntilforNA( String pattern ) throws Exception {   		try {   			this.cmdPattern = pattern;	        String rtnStr = null;	        //start a new sub thread to read telnet server return string with io blocked	        TelnetReadThread trt = new TelnetReadThread();	        FutureTask<String> trTask = new FutureTask<String>(trt);	        Thread trThread = new Thread(trTask);	        trThread.start();	        //the main thread calculate the time elapse, if io blocked exceed 60s, stop the sub thread	        long startLoopTime = System.currentTimeMillis();	        while (true){	        	Thread.sleep(1);	        	// Calculate the loop interval.	        	long endLoopTime = System.currentTimeMillis();	        	if (endLoopTime - startLoopTime > 120000) 	        		break; 	        	if(!trThread.isAlive()){	        		//if sub thread has completed normally, get its return value	        		rtnStr = trTask.get();	        		break;	        	}	        }	        if(rtnStr == null){	        	//interrupt sub thread if it's blocked and not completed normally over 60s	        	trThread.interrupt();	        	if(trThread.isAlive()){	        		telnetClientLogger.info("sub thread is not interupted in first try.\r\n");	        		Thread.sleep(200);	        		trThread.interrupt();	        	}	        	throw new Exception("Fail to match the pattern *" + pattern + "* in 60 seconds.\r\n");	        }	               		    } catch ( Exception e ) {   	        e.printStackTrace();  	        throw e;	    }   	    	}   		/**	 * Login Windows operation system and execute commands	 * 	 * @param username	 * @param password	 * @param commands :Array includes commands	 * @param commandPrompt	 * @throws Exception	 */	public void loginWindowsAndRunCommandsforNA(String username, String password, String commandPrompt) throws Exception{		try{			//Enter username and password			this.readUntilforNA("login:");			this.write(username);			this.readUntilforNA("password:");			this.write(password);			this.readUntilforNA(commandPrompt);					} catch (Exception e){						e.printStackTrace();			throw e;		}			}

}