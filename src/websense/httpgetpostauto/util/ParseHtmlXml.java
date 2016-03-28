package websense.httpgetpostauto.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;

/**
 * Class to parse HTML or XML
 * 
 * @author Wang Jiren
 *
 */
public class ParseHtmlXml {
	
	/**
	 * Parse HTML by Jsoup and return Jsoup Document object for further handling.
	 * 
	 * @param htmlContent :String of HTML source code
	 * @return Jsoup Document object, which can be used to analyze the HTML
	 *         source code by cssselector, for example:
	 *         docObject.select("a").text()
	 */
	public static Document HTML(String htmlContent){
		if (htmlContent != null) {
			return Jsoup.parse(htmlContent);
		} else {
			return null;
		}
	}
	
	/**
	 * Parse XML by Jsoup and return Jsoup Document object for further handling.
	 * 
	 * @param xmlContent :String of XML source code
	 * @return Jsoup Document object, which can be used to analyze the HTML
	 *         source code by cssselector, for example:
	 *         docObject.select("a").text()
	 */
	public static Document XML(String xmlContent){
		if (xmlContent != null) {
			return Jsoup.parse(xmlContent, "UTF-8", new Parser(new XmlTreeBuilder()));
		} else {
			return null;
		}
	}
}
