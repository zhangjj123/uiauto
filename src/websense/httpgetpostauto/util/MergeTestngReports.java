package websense.httpgetpostauto.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.htmlunit.corejs.javascript.ast.ThrowStatement;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



/**
 * Class to merge two TestNG reports together. 
 * 
 * NOTE: The second testNG report must be the result to run the testng-failed.xml file.
 * 
 * @author Wang, Jiren
 *
 */
public class MergeTestngReports {
	
	public static void main(String[] args) throws Exception {
		
			mergeTestngReports(args[0], args[1], args[2], args[3]);
	}
	
	/**
	 * If we failed to run some cases, we may want to run all those failed or skipped cases again.
	 * This method help to merge the testNG reports of the first and second time testings together.
	 * 
	 * @param firstRunReportPath: the absolute path of the testNG report of the first time testing,
	 * 							  like "C:\\tmp\\testng-results1.xml";
	 * @param secondRunReportPath: the absolute path of the testNG report of the second time testing,
	 * 							  like "C:\\tmp\\testng-results2.xml";
	 * @param outputFolder: the path of folder which is used to output merged TestNG report, like "C:\\tmp\\".
	 * @param outputFileName: the name of merged TestNG report file.
	 * @return
	 */
	public static void mergeTestngReports(String firstRunReportPath, String secondRunReportPath, String outputFolder, String outputFileName){
		
		//Read file contents of TestNG reports.
		System.out.println("#####################################");
		System.out.println("# Read testNG reports");
		System.out.println("#####################################\n");
		
		System.out.println("Browse current folder: " + System.getProperty("user.dir"));
		System.out.println("TestNG report: " + firstRunReportPath + "; and " + secondRunReportPath);
		
		if (!FileHandle.checkIfFileExists(firstRunReportPath) || !FileHandle.checkIfFileExists(secondRunReportPath)) {
			throw new RuntimeException("Cannot find the testNG reports at " + firstRunReportPath + " or " + secondRunReportPath);
		}
		String report1_Contents = FileHandle.readFile(firstRunReportPath);
		String report2_Contents = FileHandle.readFile(secondRunReportPath);
		//System.out.println("-------------------\n" + report2_Contents + "\n-------------------\n");
		
		//Jsoup automatically remove the "<![CDATA[" label. We need to retain this label.
		//So we replace the <![CDATA[xxxxx]]> to $$$CDATA$xxxxx$$$ first
		report2_Contents = report2_Contents.replace("<![CDATA[", "$$$CDATA$");
		report2_Contents = report2_Contents.replace("]]>", "$$$END");
		report1_Contents = report1_Contents.replace("<![CDATA[", "$$$CDATA$");
		report1_Contents = report1_Contents.replace("]]>", "$$$END");
		
		//Replace the "<" and ">" labels between the "$$$CDATA$" and "$$$END" labels to avoid JSOUP from adding closing tags.
		report1_Contents = replaceBracketsBetweenSpecialCharacters(report1_Contents, "\\$\\$\\$CDATA\\$[\\s\\S]*?\\$\\$\\$END");
		report2_Contents = replaceBracketsBetweenSpecialCharacters(report2_Contents, "\\$\\$\\$CDATA\\$[\\s\\S]*?\\$\\$\\$END");
		
		
		//Judge if the second report is the result of running the testng-failed.xml file.
		if (!(report2_Contents.contains("Failed suite ["))) {
			throw new RuntimeException("The report: " + secondRunReportPath + " is not the result of running the testng-failed.xml file.");
		}
		
		Document report1_Document = ParseHtmlXml.XML(report1_Contents);
		Document report2_Document = ParseHtmlXml.XML(report2_Contents);

		//Modify the attributes of the tag "testng-results".
		System.out.println("#####################################");
		System.out.println("# Start to merge TestNG reports......");
		System.out.println("#####################################\n");
		
		System.out.println("Handle the [testng-results] tag\n");
		Element report2_testng_results = report2_Document.select("testng-results").first();
		Element report1_testng_results = report1_Document.select("testng-results").first();
		
		report1_testng_results.attr("skipped", report2_testng_results.attr("skipped"));
		report1_testng_results.attr("failed", report2_testng_results.attr("failed"));
		report1_testng_results.attr("passed", String.valueOf(Integer.valueOf(report1_testng_results.attr("total")) 
				- ((Integer.valueOf(report1_testng_results.attr("skipped")) + Integer.valueOf(report1_testng_results.attr("failed"))))));
		
		//Modify the attributes of the tag "reporter-output"
		System.out.println("Handle the [reporter-output] tag\n");
		Element report2_reporter_output = report2_Document.select("reporter-output").first();
		Element report1_reporter_output = report1_Document.select("reporter-output").first();
		
		report1_reporter_output.html(report2_reporter_output.html());
		
		//Modify the attributes of the tag "suite"
		System.out.println("Handle the [suite] tag\n");
		Elements report2_suite_elements = report2_Document.select("suite");
		
		for (Element report2_suite_element : report2_suite_elements) {
			String suiteName = report2_suite_element.attr("name");
			System.out.println("***********************************");
			System.out.println("** Handle the suite: " + suiteName);
			System.out.println("***********************************");
			
			String suiteName1 = suiteName.substring(suiteName.indexOf("[")+1, suiteName.lastIndexOf("]"));
			Element report1_suite_element = report1_Document.select("suite[name=" + suiteName1 +"]").first();
			
			report1_suite_element.attr("duration-ms", String.valueOf(Integer.valueOf(report1_suite_element.attr("duration-ms")) + Integer.valueOf(report2_suite_element.attr("duration-ms"))));
			report1_suite_element.attr("finished-at", report2_suite_element.attr("finished-at"));
			
			//Modify the attributes of the tag "test"
			Elements report2_test_elements = report2_Document.select("suite[name=" + suiteName + "] > test");
			
			for (Element report2_test_element : report2_test_elements) {
				String testName = report2_test_element.attr("name");
				System.out.println("*** Handle the test: " + testName);
				
				String testName1 = testName.substring(0, testName.indexOf("("));
				Element report1_test_element = report1_Document.select("suite[name=" + suiteName1 + "] > test[name=" + testName1 + "]").first();
				
				report1_test_element.attr("duration-ms", String.valueOf(Integer.valueOf(report1_test_element.attr("duration-ms")) + Integer.valueOf(report2_test_element.attr("duration-ms"))));
				report1_test_element.attr("finished-at", report2_test_element.attr("finished-at"));
				
				//No need to modify the attributes of the tag "class".
				Elements report2_class_elements = report2_Document.select("suite[name=" + suiteName + "] > test[name=" + testName + "] > class");
				
				for (Element report2_class_element : report2_class_elements){
					String className = report2_class_element.attr("name");
					System.out.println("** Handle the class: " + className);
					
					//If the class, which exists in the report2, doesn't exist in the report1, we will not search this class in report1.
					Element report1_class_element = report1_Document.select("suite[name=" + suiteName1 + "] > test[name=" + testName1 + "] > class[name=" + className + "]").first();
					if (report1_class_element == null) {
						System.out.println("WARN!! The class " + className + " in the report 2 doesn't exist in the report 1");
						continue;
					}
					
					//Modif the attributes of the "test-method" tag.
					Elements report2_test_method_elements = report2_Document.select("suite[name=" + suiteName + "] > test[name=" + testName + "] > class[name=" + className + "] > test-method");
					
					//Count the number of methods which have same method name in report2.
					HashMap<String, Integer> methodsNumberMap = new HashMap<String, Integer>();
					for (Element report2_test_method : report2_test_method_elements) {
						String methodName1 = report2_test_method.attr("name"); 
						if (!methodsNumberMap.containsKey(methodName1)) {
							methodsNumberMap.put(methodName1, 0);
						} else {
							methodsNumberMap.put(methodName1, methodsNumberMap.get(methodName1) + 1);
						}
					}
					
					//Start modify the attributes of the "test-method" tag.
					for (Element report2_test_method_element : report2_test_method_elements) {
						String methodName = report2_test_method_element.attr("name");
						System.out.println("* Handle the method: " + methodName);
						
						Elements report1_test_method_elements = report1_Document.select("suite[name=" + suiteName1 + "] > test[name=" + testName1 + "] > class[name=" + className + "] > test-method[name=" + methodName + "]");
						
						for (Element report1_test_method_element : report1_test_method_elements) {
							//Don't replace any attribute of the PASSED or already handled method in the report1
							if (!(report1_test_method_element.attr("status").equalsIgnoreCase("PASS")) || !(report1_test_method_element.attr("started-at").equals("2011-11-11T11:11:11Z"))) {
								
								//Replace all attributes of the FAILED and SKIPPED test cases in the report 1 and change the "started-at" attribute to "2011-11-11T11:11:11Z" in order to label this cases is already handled.
								report1_test_method_element.attr("status", report2_test_method_element.attr("status"));
								report1_test_method_element.attr("duration-ms", report2_test_method_element.attr("duration-ms"));
								report1_test_method_element.attr("finished-at", report2_test_method_element.attr("finished-at"));
								report1_test_method_element.attr("started-at", "2011-11-11T11:11:11Z");
								report1_test_method_element.html(report2_test_method_element.html());
								
							}
						}			
					}
				}
			}	
		}
		
		//Generate the final report which combines report1 and report2.
		System.out.println("#####################################");
		System.out.println("# Generate report......");
		System.out.println("#####################################\n");
		
		if (!(FileHandle.checkIfFileExists(outputFolder))) {
			throw new RuntimeException("The output folder doesn't exist! please help to check.");
		}
		
		String finalReportPath = FileHandle.returnAbsolutePath(outputFolder, outputFileName); 
		try {
			FileHandle.creatFile(finalReportPath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Fail to create an empty testNG report to write the combined result!");
		}
		
		//Change the $$$CDATA$xxxxx$$$ back to <![CDATA[xxxxx]]>
		String originalHtml = report1_Document.html().replace("$$$CDATA$", "<![CDATA[");
		originalHtml = originalHtml.replace("$$$END", "]]>");
		originalHtml = originalHtml.replace("@#START", "<");
		originalHtml = originalHtml.replace("@#END", ">");
		
		FileHandle.write(finalReportPath, originalHtml);
		System.out.println("The new report is generated to: " + finalReportPath);
		
	}
	
	/**
	 * Replace the "<" and ">" label in the String which matches the "regexToMatchString"
	 * @param stringToReplace
	 * @param regexToMatchString: Match a sub-string from the "stringToReplace" by regex.
	 * @return
	 */
	private static String replaceBracketsBetweenSpecialCharacters(String stringToReplace, String regexToMatchString){
		
		String result = stringToReplace;
		
		Pattern pattern = Pattern.compile(regexToMatchString);
		Matcher matcher = pattern.matcher(stringToReplace);
		
		//Start replacing.
		 while (matcher.find()) {
	    	   String beforeReplace = matcher.group();
	    	   String afterReplace = beforeReplace.replace("<", "@#START");
	    	   afterReplace = afterReplace.replace(">", "@#END");
	    	   result = result.replace(beforeReplace, afterReplace);
	      } 
		 
		 return result;
	}
}
