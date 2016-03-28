package websense.httpgetpostauto.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;import java.util.ArrayList;import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;


/**
 * Run local system command and get outputs
 * 
 * @author Wang, Jiren
 *
 */
public class runLocalCommand {
	
	private static Logger runLocalCommandLogger;
	
	/**
	 * Run command and return command output
	 * @param command :Command to be executed. 
	 * 		Note: 
	 * 		1. The format of absolute path of command should be like:
	 * 		"cd \"C:\\Program Files (x86)\\1.bat\""
	 * @return : command output
	 * @throws Exception
	 */
	public static String executeWithOutput(String command) throws Exception{
		//Execute command
		Process process = executeWithoutOutput(command);
		
		//Read output of command
		try{	
			InputStream fis = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(fis,"GBK"));
			
			String commandOutput = "";
			String lineString = null;
			//read very line of command ouput
			while ((lineString = br.readLine()) != null) {
				runLocalCommandLogger.debug("Read the line: " + lineString);				
				commandOutput += lineString; 			
			}
			
			process.waitFor();
			return commandOutput;
		}catch (Exception e){
			e.printStackTrace();
			throw new Exception("Fail to read output of command: " + command);
		}		
	}
	
	/**
	 * Run command and return command output from ERROR stream, instead of INPUT stream.
	 * For example the output of the command "java -version".
	 * @param command :Command to be executed. 
	 * 		Note: 
	 * 		1. The format of absolute path of command should be like:
	 * 		"cd \"C:\\Program Files (x86)\\1.bat\""
	 * @return : command output
	 * @throws Exception
	 */
	public static String executeWithOutputFromErrorStream(String command) throws Exception{
		//Execute command
		Process process = executeWithoutOutput(command);
		//Read output of command
		try{	
			InputStream fis = process.getErrorStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			
			String commandOutput = "";
			String lineString = null;
			//read very line of command ouput
			while ((lineString = br.readLine()) != null) {
				runLocalCommandLogger.debug("Read the line: " + lineString);
				commandOutput += lineString; 			
			}
			return commandOutput;
		}catch (Exception e){
			e.printStackTrace();
			throw new Exception("Fail to read output of command: " + command);
		}
	}
	
	/**	 * Run command and return command output	 * @param command :Command to be executed. 	 * 		Note: 	 * 		1. The format of absolute path of command should be like:	 * 		"cd \"C:\\Program Files (x86)\\1.bat\""	 * @return : command output lines arrayList	 * @throws Exception	 */	public static ArrayList<String> executeWithOutputLines(String command) throws Exception{		//Execute command		Process process = executeWithoutOutput(command);		//Read output of command		try{				InputStream fis = process.getInputStream();			BufferedReader br = new BufferedReader(new InputStreamReader(fis));			ArrayList<String> commandOutput = new ArrayList<String>();			String lineString = null;			//read very line of command ouput			while ((lineString = br.readLine()) != null) {				runLocalCommandLogger.debug("Read the line: " + lineString);				commandOutput.add(lineString);						}			return commandOutput;		}catch (Exception e){			e.printStackTrace();			throw new Exception("Fail to read output of command: " + command);		}	}
	/**
	 * execute local cmd and wait specified time for output
	 */
	public static String executeWithOutput(String command, int Wait) throws Exception{
		//Execute command
		Process process = executeWithoutOutput2(command);
		//Read output of command
		String commandOutput = "";
		String lineString = null;
		try{	
			InputStream fis = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			for(int i=0; i<4; i++)
			{
				if((lineString = br.readLine()) != null)
				{
					commandOutput += lineString;
					break;
				}
				Thread.sleep(Wait*1000);
				fis = process.getInputStream();
				br = new BufferedReader(new InputStreamReader(fis));
			}
			//read very line of command ouput
			while ((lineString = br.readLine()) != null) {
				//runLocalCommandLogger.debug("Read the line: " + lineString);
				commandOutput += lineString; 			
			}
			return commandOutput;
		}catch (Exception e){
			e.printStackTrace();
			throw new Exception("Fail to read output of command: " + command);
		}
	}
	/**
	 * Run command and match command output by regex line by line
	 * @param command :Command to be executed. 
	 * 		Note: The format of absolute path of command should be like:
	 * 		"cd \"C:\\Program Files (x86)\\1.bat\""
	 * @param regex : Regex to match output
	 * @return :Matched String
	 * @throws Exception
	 */
	public static String matchRegexLineByLine(String command, String regex)throws Exception{
		//Execute command
		Process process = executeWithoutOutput(command);
				
		//Read output of command
		try{	
			InputStream fis = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
					
			String matchedOutput = null;
			String lineString = null;
			
			Matcher myMatcher = null;
			Pattern myPattern = Pattern.compile(regex);
			
			//Match very line of command ouput
			while ((lineString = br.readLine()) != null) {
				runLocalCommandLogger.debug("Read the line: " + lineString);
				myMatcher = myPattern.matcher(lineString);
				
				if (myMatcher.find()) {
					matchedOutput = myMatcher.group();
					//System.out.println(myMatcher.group());
					break;	
				}			
			}
			return matchedOutput;
		}catch (Exception e){
			e.printStackTrace();
			throw new Exception("Fail to match output of command: " + command);
		}
	}
		
	public static Process executeWithoutOutput(String command)throws Exception{
		
		//log4j
		runLocalCommandLogger = Log4j.logger(runLocalCommand.class.getName());
		Process process = null;
		
		try{
			//Windows
			if ("\\".equals(File.separator)){
				String[] commandArray = {"cmd", "/c", command};
				//Run local command
				process = Runtime.getRuntime().exec(commandArray);
				runLocalCommandLogger.info("Run command: " + command.replace("\"", "'") + " on Windows");
			} 
			else if ("/".equals(File.separator))
			{
				String[] commandArray = {"/bin/sh", "-c", command};
				//Run local command
				process = Runtime.getRuntime().exec(commandArray);
				
				runLocalCommandLogger.info("Run command: " + command + " on Linux");
			} else {
				throw new Exception("Unknown operation system.");
			}
			
			return process;
		} 
		
		catch (Exception e){
			e.printStackTrace();
			throw new Exception("Fail to run command: " + command);
		}		
	}
	private static Process executeWithoutOutput2(String command)throws Exception{
		//log4j
		runLocalCommandLogger = Log4j.logger(runLocalCommand.class.getName());
		Process process = null;
		
		try{
			//Windows
			if ("\\".equals(File.separator)){
				//String[] commandArray = {"cmd", "/c", command};
				//Run local command
				//process = Runtime.getRuntime().exec(commandArray);
				process = Runtime.getRuntime().exec("cmd /c \""+command +"\"");
				runLocalCommandLogger.info("Run command: " + command.replace("\"", "'") + " on Windows");
			} 
			else if ("/".equals(File.separator))
			{
				String[] commandArray = {"/bin/sh", "-c", command};
				//Run local command
				process = Runtime.getRuntime().exec(commandArray);
				
				runLocalCommandLogger.info("Run command: " + command + " on Linux");
			} else {
				throw new Exception("Unknown operation system.");
			}
			
			return process;
		} 
		
		catch (Exception e){
			e.printStackTrace();
			throw new Exception("Fail to run command: " + command);
		}		
	}
	
}
