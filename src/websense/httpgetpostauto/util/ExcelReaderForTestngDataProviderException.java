package websense.httpgetpostauto.util;

/**
 * Description: Customized exception for the class "ExcelReaderForTestngDataProvider"
 * 
 * @author Wang Jiren
 */
public class ExcelReaderForTestngDataProviderException extends Exception {

	private static final long serialVersionUID = 3435456589196458401L;   

    public ExcelReaderForTestngDataProviderException (){}
    
    /**
     * 
     * @param message 抛出的错误信息
     * @param row 错误的行
     * @param column 错误的列
     * @return 返回错误信息，某行某列
     */
    public ExcelReaderForTestngDataProviderException(String message, int row, int column)
    {   
        super(message + ": " + row + "row; " + column + "column.");   
    }   
    
}  
