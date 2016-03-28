package websense.httpgetpostauto.util;

import java.io.File;



/**
 * Class to return absolute path of project file
 * @author Wang Jiren
 *
 */
public class ProjectFile {
	
	
	public static String returnPath(String folderRelativePath) {
	
	/**
	 * Get the value of key from property file.
	 * @param folderRelativePath :Relative folder path for file, for example: "conf", "postdata", ...
	 * @param fileName :Name of file. If name==null, return the path of folder.
	 * @param keyName
	 * @return
	 * @throws Exception
	 */
	public static String read(String folderRelativePath, String fileName, String keyName) throws Exception{		
		FileInputStream fileInputStream = null;
		
		try {
			Properties projectFileProperties = new Properties();
			fileInputStream = new FileInputStream(returnPath(folderRelativePath, fileName));
			projectFileProperties.load(fileInputStream);
			String value = projectFileProperties.getProperty(keyName);
			if (value != null){
				return value;
			} else {
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Fail to read the value of key: " + keyName + "; from file: " + fileName);
		} finally{
			fileInputStream.close();
		}
		
	}

}