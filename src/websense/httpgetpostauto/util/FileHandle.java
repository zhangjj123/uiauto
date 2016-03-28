package websense.httpgetpostauto.util;

import java.io.BufferedReader;import java.io.BufferedWriter;import java.io.File;import java.io.FileInputStream;import java.io.FileWriter;import java.io.IOException;import java.io.InputStreamReader;import java.util.ArrayList;import java.util.Arrays;import java.util.Collections;import java.util.Comparator;import java.util.HashMap;import java.util.List;import java.util.Properties;

import org.jsoup.nodes.Document;import org.jsoup.nodes.Element;import org.jsoup.select.Elements;

/**
 * Handle file reading, writing and modifying.
 * @author Wang, Jiren
 *
 */
public class FileHandle {
	
	
	/**
	 * Constructor
	 */
	public FileHandle(){
		
	}
	
	
	/**
	 * modify the content of a property file
	 * @param filePath :property file path, for example: C:\\Program Files (x86)\\Websense\\Web Security\\bin or /opt/Websense/bin.
	 * @param fileName :property file name
	 * @param changedContent :A HashMap to include contents planned to change, for example:
	 * {WebsenseServerPort: 15868, DNSLookup: off} means that property file will be read line by line.
	 * if WebsenseServerPort is found in this file, its value will be changed to 15868.
	 * if DNSLookup is NOT found in this file, a new variable will be added - DNSLookup=off.
	 * @param sectionName :If section name is null, a key in HashMap changedContent, which is not found in property file, will be added to the end of this file.
	 * If section name is defined, a key in HashMap ChangeContent, which is not found in property file, will be added under this section.
	 * For example: sectionName=[WebsenseServer]. All new key will be added to below it.
	 */
	public static void modifyPropertyFile(String fileFolder, String fileName, HashMap<String, String> changedContent, String sectionName) throws Exception{
		//File location
		String filePath = null;
		if (fileFolder.endsWith(File.separator)) {
			filePath = fileFolder + fileName;
		} else {
			filePath = fileFolder + File.separator + fileName;
		}
		
		
		//Read file and match line by line
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		ArrayList<String> lines = new ArrayList<String>();
		try {
			//Load property file
			File propertyFile = new File(filePath);
			FileInputStream inputStream = new FileInputStream(propertyFile);
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			
			//read content line by line
			String line = null;
			while ((line=bufferedReader.readLine()) != null) {
				lines.add(line);		
			}
			
			//Match key in HashMap
			int matchedSignal = 0;
			for (String key : changedContent.keySet()) {
				for (int i = 0; i < lines.size(); i++) {
					//If variable (planned to change) is found in property file
					//Add the key-value pair to property file and remove the original key-value pair.					if( lines.get(i).startsWith(";") )						continue;						
					if (lines.get(i).contains(key)) {										    if( lines.get(i).split("=")[0].trim().equals(key) ){
						    matchedSignal = 1;
						    lines.add(i, key + "=" + changedContent.get(key));
						    lines.remove(i+1);
						    break;						}
					} 
				}
				
				//If no same variable is found in property file, add this key-value pair to property file.
				if (matchedSignal == 0) {
					//Section name is null. Add to the end of file.
					if (sectionName == null) {
						lines.add(key + "=" + changedContent.get(key));
					} 
					//If section name is not null. Add it near the section name.
					else {
						for (int i = 0; i < lines.size(); i++){
							if (lines.get(i).contains(sectionName)) {
								lines.add(i+1, key + "=" + changedContent.get(key));
								break;
							}
						}
					}
				}
				//reset value to next key
				matchedSignal = 0;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Fail to modify property file " + fileName);
		} finally{
			bufferedReader.close();
		}
		
		//Write changes to property file
		try {
			//Load property file
			File propertyFile = new File(filePath);
			FileWriter writer = new FileWriter(propertyFile);
			bufferedWriter = new BufferedWriter(writer);
			
			//Start to write
			for (String line : lines) {
				bufferedWriter.write(line);
				bufferedWriter.newLine();
			}
			//Save
			bufferedWriter.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Fail to write changes to property file " + fileName);
		} finally{
			bufferedWriter.close();
		}
	}
	
	/**
	 * Order files in a directory according to length.
	 * @param folderPath : path of folder, for example: "C:\\Software\\tmp"
	 * @return
	 */
	public static List<File> orderByLength(String folderPath) {  
        List<File> files = Arrays.asList(new File(folderPath).listFiles());  
        Collections.sort(files, new Comparator<File>() {  
            public int compare(File f1, File f2) {  
                long diff = f1.length() - f2.length();  
                if (diff > 0)  
                    return 1;  
                else if (diff == 0)  
                    return 0;  
                else  
                    return -1;  
            }  
            public boolean equals(Object obj) {  
                return true;  
            }  
        });
        
        return files;
    }
	
	/**
	 * Order files in a directory according to file name.
	 * @param folderPath : path of folder, for example: "C:\\Software\\tmp"
	 * @return
	 */
    public static List<File> orderByName(String folderPath) {  
        List<File> files = Arrays.asList(new File(folderPath).listFiles());  
        Collections.sort(files, new Comparator<File>() {  
            @Override  
            public int compare(File o1, File o2) {  
                if (o1.isDirectory() && o2.isFile())  
                    return -1;  
                if (o1.isFile() && o2.isDirectory())  
                    return 1;  
                return o1.getName().compareTo(o2.getName());  
            }  
        });  
       
        return files;
    } 
    
    /**
     * 
     * Order files in a directory according to date.
	 * @param folderPath : path of folder, for example: "C:\\Software\\tmp"
	 * @return
     */
    public static File[] orderByDate(String folderPath) {  
        File file = new File(folderPath);  
        File[] fs = file.listFiles();  
        Arrays.sort(fs,new Comparator<File>(){  
            public int compare(File f1, File f2) {  
                long diff = f1.lastModified() - f2.lastModified();  
                if (diff > 0)  
                    return 1;  
                else if (diff == 0)  
                    return 0;  
                else  
                    return -1;  
            }  
            public boolean equals(Object obj) {  
                return true;  
            }  
              
        });  
        
        return fs;
    }
    
    /**
     * Return latest file which includes specified keyword in file name (case sensitive).
     * @param fileIncludeKeyword : Keyword in file name.
     * @param folderPath
     * @return
     */
    public static String latestFile(String fileIncludeKeyword, String folderPath){
    	
    	File[] files = orderByDate(folderPath);
    	
    	for (int i = files.length - 1; i >= 0; i--) {
			if (files[i].getName().contains(fileIncludeKeyword)) {
				return files[i].getAbsolutePath();
			}
		}
    	
    	return null;
    }
    
    /**
     * Return latest file which includes specified regex in file name.
     * @param regex : regex to match file name, for example $_grp
     * @param folderPath
     * @return : File absolute path or string "null".
     */
    public static String latestFileMatchRegex(String regex, String folderPath){
    	
    	File[] files = orderByDate(folderPath);
    	
    	for (int i = files.length - 1; i >= 0; i--) {
			if (files[i].getName().matches(regex)) {
				return files[i].getAbsolutePath();
			}
		}
    	
    	return "null";
    }
    /**
     * If file  is updated in 2 minutes ,then will return true, else return false
     * @param fileAbsolutePath: the absolute file path
     * @return 
     * @throws Exception
     */
    
	public static boolean updateFile(String fileAbsolutePath)throws Exception{
    	File file=new File(fileAbsolutePath);
    	boolean update=false;
    	long time1=file.lastModified();
    	
    	for (int i=0;i<3;i++){
    		Thread.sleep(40000);
    		if(file.lastModified()>time1){
    			update=true;
    			break;
    		}
    		
    	}
    	
    	return update;
    }
    
    /**
     * Remove all files and sub-folders in a folder (not remove parent folder).
     * @param folderPath : absolute path of the folder.
     * @throws IOException
     */
    public static void delAllFilesInFolder(String folderPath) throws IOException{
    	File f = new File(folderPath);//�����ļ�·�� 
    	//�ж����ļ�����Ŀ¼  
    	if(f.exists() && f.isDirectory()){
    		//��Ŀ¼�����ļ���ɾ��  
    		if(f.listFiles().length!=0){
    			File delFile[]=f.listFiles();  
                int i =f.listFiles().length;
                for(int j=0;j<i;j++){
                	if(delFile[j].isDirectory()){
                		delAllFilesInFolder(delFile[j].getAbsolutePath());//�ݹ����del������ȡ����Ŀ¼·��  
                	}
                	delFile[j].delete();//ɾ���ļ� 
                }
    		}
    		
    	}
    }
    
    /**
     * Read content of file to a String, then remove special characters in String and write back to file.
     * @param filePath
     * @param specialChar : like "-\n"
     * @return
     * @throws IOException 
     */
    public static void removeSpecialChar(String filePath, String specialChar){
    	
    	//read content of file
    	String contentOfFile = readFile(filePath);
    	
    	//Remove special character in content
    	String newContent = contentOfFile.replace(specialChar, "");
    	
    	//Write new content back to file
    	BufferedWriter bufferedWriter = null;
    	try {
    		FileWriter writer = new FileWriter(new File(filePath));
    		bufferedWriter = new BufferedWriter(writer);
        	
    		//Start to write
    		bufferedWriter.write(newContent);
    		bufferedWriter.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Fail to write new content to file: " + filePath);
		} finally{
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Fail to close file: " + filePath);
			}
		}

    }
    
    /**
     * Get the content of a file.
     * @param filePath : Absolute path of a file.
     * @return content of file.
     */
    public static String readFile (String filePath){
   
    	String fileContent = "";
		File file = new File(filePath);	
		BufferedReader bufferedReader = null;
		
    	try {
        	bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        	String line = "";
    		while ((line=bufferedReader.readLine()) != null) {	
    			fileContent = fileContent + line + "\n";
    		}
    		return fileContent;
		
    	} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Fail to read file: " + filePath);
		
		} finally{
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Fail to close bufferedReader.");
			}
		}
		
    }        public static String readFileUTF8 (String filePath){    	       	String fileContent = "";		File file = new File(filePath);			BufferedReader bufferedReader = null;		    	try {        	bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));        	String line = "";    		while ((line=bufferedReader.readLine()) != null) {	    			fileContent = fileContent + line + "\n";    		}    		return fileContent;		    	} catch (IOException e) {			e.printStackTrace();			throw new RuntimeException("Fail to read file: " + filePath);				} finally{			try {				bufferedReader.close();			} catch (IOException e) {				e.printStackTrace();				throw new RuntimeException("Fail to close bufferedReader.");			}		}		    }
    /**
     * Create new file under filepath
     * @param filepath:absolute file path
     * @param name: file name
     * @return
     * @throws IOException
     */
    public static boolean creatFile(String filepath) throws IOException { 
    	boolean flag = false; 
    	File filename = new File(filepath); 
    	if (!filename.exists()) { 
    	filename.createNewFile(); 
    	flag = true; 
    	} 
    	return flag; 
    	}
    
    /**
     * Remove a file or a empty folder.
     * @param filepath
     * @return
     * @throws IOException
     */
    public static boolean deleFile(String filepath) throws IOException { 
    	boolean flag = false; 
        try{
    	   File filename = new File(filepath); 
    	   if (filename.exists()) { 
    	   filename.delete(); 
    	   flag = true; 
    	   } 
    	 
        }catch (Exception e) {
    	    e.printStackTrace();
	    }   
         return flag;
    }
    
    /**
     * Remove a folder which is empty or NOT empty.
     * @param folderPath
     * @throws IOException
     */
    public static void deleFolder(String folderPath) throws IOException{
    	//Remove all files in this folder
    	delAllFilesInFolder(folderPath);
    	
    	//Remove this empty folder
    	deleFile(folderPath);
    }
    
    /**
     * Write content to file
     * 
     * @param filePath:absolute path including file name
     * @param content
    */
    public static void write(String filePath, String content) {
        BufferedWriter bw = null;
        
        try {
            // Create write buffer based on file path
            bw = new BufferedWriter(new FileWriter(filePath));
            // write content
            bw.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           // close stream
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    bw = null;
                }
           }
      }
    }
    /**
     * Update logs-test file with url  including new log file name
     * @param filePath:absolute file path
     * @param logName: the full url including log name
     * @throws Exception
     */
    public static void updateFileLogstest(String filePath,String logName)throws Exception{
    	String content=readFile(filePath);
    	Document doc= ParseHtmlXml.XML(content);
    	Elements elements=doc.getElementsByTag("log");
    	Element ele=elements.get(0);
    	ele.attr("url", logName);
    	content=doc.toString();
    	write(filePath, content);	
    }
    
    /**
     * Number of sub-files and sub-folders in a designated folder.
     * @param folderPath
     * @return     * @throws InterruptedException 
     */
    public static int numberOfFilesAndFolders(String folderPath) throws InterruptedException{
    	File file = new File(folderPath);    	Thread.sleep(6000);
    	return file.list().length;
    }    public static int numberOfFilesWithSpecialType(String folderPath, String fileType){    	File file = new File(folderPath);    	    	String[] fileList = file.list();    	    	int number = 0;    	        for( int i =0; i< fileList.length; i++){        	if( fileList[i].endsWith(fileType))        		number++;        }    	return number;    }
    /**   * return the number of file, including sub folders.   * @param filePath   * @return   * @throws InterruptedException   */
     public static int numberOfFilesIncludeSubFolder(String filePath) throws InterruptedException{    	File file = new File(filePath);    	int num = 0;    	Thread.sleep(2000);    	File[] files = file.listFiles();    	if( files != null){    		for( File f: files){    			if( f.isDirectory())    				num = num + numberOfFilesIncludeSubFolder(f.getAbsolutePath()) ;    			else    				num++;    		}    			    	}     	return num;    }
    /**
     * Check if a file or folder exists
     * @param folderPath
     * @param fileName
     * @return
     */
    public static boolean checkIfFileExists(String folderPath, String fileName){
    	String filePath = returnAbsolutePath(folderPath, fileName);
    	File file = new File(filePath);
    	if (file.exists()) {
			return true;
		} else {
			return false;
		}	
    }
    
    /**
     * Check if a file or folder exists
     * @param filePath: the absolute path of the file, like "C:\\tmp\\testng-results1.xml".
     * @return
     */
    public static boolean checkIfFileExists(String filePath){
    	File file = new File(filePath);
    	if (file.exists()) {
			return true;
		} else {
			return false;
		}	
    }
    
    /**
     * Return absolute path of a file or folder.
     * @param folderPath
     * @param fileName
     * @return
     */
    public static String returnAbsolutePath(String folderPath, String fileName){
    	if (folderPath.endsWith(File.separator)) {
			return folderPath + fileName;
		} else {
			return folderPath + File.separator + fileName;
		}
    }
    
    /**
     * Return absolute path of a file or folder.
     * @param folderPath
     * @param fileName
     * @return
     */
    public static String returnAbsolutePathWithSpecialSeperator(String folderPath, String fileName, String fileSeperator){
    	if (folderPath.endsWith(fileSeperator)) {
			return folderPath + fileName;
		} else {
			return folderPath + fileSeperator + fileName;
		}
    }
    
    /**
	 * Get the value of key from property file.
	 * @param propertyFilePath : The absolute path of property file.
	 * @param keyName
	 * @return
	 * @throws Exception
	 */
	public static String readPropertyFile(String propertyFilePath, String keyName) throws Exception{		
		FileInputStream fileInputStream = null;
		
		try {
			Properties projectFileProperties = new Properties();
			fileInputStream = new FileInputStream(propertyFilePath);
			projectFileProperties.load(fileInputStream);
			String value = projectFileProperties.getProperty(keyName);
			if (value != null){
				return value;
			} else {
				throw new Exception("NULL: the value of key: " + keyName + "; in file: " + propertyFilePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Fail to read the value of key: " + keyName + "; from file: " + propertyFilePath);
		} finally{
			fileInputStream.close();
		}
	}
	/**
	 * append string to existed file
	 * @param filePath
	 * @param append
	 * @throws Exception
	 */
	public static void appendToFile(String filePath, String append)throws Exception{
		String content=readFile(filePath);
	    content=content+"\n"+append+"\n";
	    write(filePath,content);
	}
	
	/**
	 * The DiagClient.exe can get the settings of a specific service, for example: 
	 * "DiagClient.exe -ip 127.0.0.1 -component WebsenseDAService -inspector DAService -level general -file dasinit.txt"
	 * 
	 * The content of the output file is like:
	 * "User scope: SubTree
     	Group scope: SubTree
		Level: 1
		Include group users: True
    	Contexts (Excluded):"
	 * 
	 * By using this function, we can get the value of specific keyword, for example:
	 * the return of readDiagClientIniOutput("C:\\tmp\\dasini.txt", "User scope") is "SubTree"
	 * 
	 * @param folderPath: the absolute path of file folder.
	 * @param fileName: the name of file.
	 * @param keyword: the name of setting we want to get value.
	 * @return the value of keyword. (If null, this keyword is not found.)
	 */
	public static String readDiagClientIniOutput(String folderPath, String fileName, String keyword){
		//get absolute file path
		String filePath = returnAbsolutePath(folderPath, fileName);
		
		//read content
		String  contentOfFile = FileHandle.readFile(filePath);
		String[] contentArray = contentOfFile.split("\n");
		
		//search keyword line by line and read the value.
		for (int i = 0; i < contentArray.length; i++) {
			if (contentArray[i].contains(keyword)) {
				return (contentArray[i].split(": "))[1];
			}
		}
		
		//If this keyword is not found, return Null.
		return null;
	}	/**	 * Insert some string to file	 * @param absoluteFilePath:The file absolute path	 * @param offset:The insert location	 * @param subString:The insert String	 * @throws Exception	 */	public static void insertStringToFile(String absoluteFilePath,int offset,String subString)throws Exception{		StringBuffer originalStringBuffer=new StringBuffer(readFile(absoluteFilePath));		originalStringBuffer.insert(offset, subString);		write(absoluteFilePath, originalStringBuffer.toString());			}	/**	 * 	 * @param absoluteFilePath:the file absolute path	 * @param stringLocation: insert string after stringLocation	 * @param subString:The insert string	 * @throws Exception	 */	public static void insertStringToFile(String absoluteFilePath,String stringLocation,String subString)throws Exception{		String content=readFile(absoluteFilePath);		int offset=content.indexOf(stringLocation)+stringLocation.length();		System.out.println(offset);		insertStringToFile(absoluteFilePath,offset , subString);	}    /**     * check the file contains substring or not     * @param absoluteFilePaht     * @param subString     * @return     * @throws Exception     */	public static boolean isStringInFile(String absoluteFilePaht,String subString)throws Exception{		String content=readFile(absoluteFilePaht);		return content.contains(subString);	}		/**	 *create direction 	*/	public static void createDir( String absoluteDirPath ) throws Exception{				File file = new File( absoluteDirPath );				file.mkdirs();			}		/**	 *copy all files under sourcePath to target path 	 *@param sPath: absolute path of the source folder. not contain file name.	 *@param tPath: absolute path of the target folder	 *@author Samantha	 */	public static void copyAllFilesInFolder(String sPath, String tPath) throws Exception{			File f = new File(sPath);      	if( f.exists() && f.isDirectory() ){    	    		File copyFile[]=f.listFiles();      		    		int i =copyFile.length;    		if( i != 0 ){                for(int j=0;j<i;j++){                	if(copyFile[j].isDirectory()){                		copyAllFilesInFolder(copyFile[j].getAbsolutePath(), tPath );                	}                	                	String tFile = returnAbsolutePath(tPath, copyFile[j].getName());                	                	creatFile(tFile);                	                	write( tFile, readFile( copyFile[j].getAbsolutePath() ) );                }    		}    	}	}		/**	 * get the network path. just for windows	 * @param ip: the remote ip	 * @param path: the remote path. not contain file name. for example: C:\save	 * @return string: \\ip\path. for example: \\10.226.181.6\c$\save.	 * @author Samantha	 */	public static String getNetPath( String ip, String path){				return "\\\\" + ip +"\\" + path.replace(":", "$"); 			}		/**	  * get bin path for logserver machine. just the local path, not network path.	 	  * @author Samantha 	  */	 public static String getLogServerBinPath() throws Exception{		 		String managerIP = ProjectFile.read("conf", "system.properties", "ManagerIP");				String logserverIP = ProjectFile.read("conf", "system.properties", "LogServerIP");		 		String path;		 		if( managerIP.equals(logserverIP) ){			 			 path = "C:\\Program Files (x86)\\Websense\\Web Security\\bin";			 		}		else {			path = "C:\\Program Files\\Websense\\Web Security\\bin";		}		 		  return path;		 	}		/**	  * delete files which includes specified regex in file name.	  * @param regex	  * @param folderPath	  * @author Samantha	  */    public static void delFilesMatchRegex(String regex, String folderPath){    	File[] files = (new File(folderPath)).listFiles();    	for (int i = files.length - 1; i >= 0; i--){    		if (files[i].getName().matches(regex)){    			files[i].delete();    		}    	}    			    }			/**	 * use "net use  \\ip" command to connect remote machine, then we can use \\ip\c$\filename 	 * to read the file in remote machine.	 * @param ip	 * @param user	 * @param pwd	 * @param os	 * @return	 * @throws Exception	 * @author Samantha	 */	public static void connectRemoteMachine(String ip, String user, String pwd,String os) throws Exception{				if( os.toLowerCase().equals("windows")){					String cmdCreateCredential = "net use \\\\" + ip + "\\c$ /user:"+ user + " " + pwd;				    /*String output = runLocalCommand.executeWithOutputFromErrorStream(cmdCreateCredential);		    if( !output.equals("") && (!output.contains("Multiple connection to a server") || 		    		!output.contains("1219"))) {		    	System.out.println("Fail  to connect "+ip+", the error message is:"+output.replace("\n", "."));		    }*/		    			String output = runLocalCommand.executeWithOutput(cmdCreateCredential);			if(!output.contains("success") || !output.contains("Multiple connection to a server") || 				!output.contains("1219"))			System.out.println("Fail  to connect "+ip+", the error message is:"+output.replace("\n", "."));		}		else			System.out.println("if the machine install in linux, please configure samba to make share file work");	}	
}
