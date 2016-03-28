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
     * @param message �׳��Ĵ�����Ϣ
     * @param row �������
     * @param column �������
     * @return ���ش�����Ϣ��ĳ��ĳ��
     */
    public ExcelReaderForTestngDataProviderException(String message, int row, int column)
    {   
        super(message + ": " + row + "row; " + column + "column.");   
    }   
    
}  
