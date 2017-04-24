import java.util.HashMap;
import java.util.Map;

public class IRstock {
	/** "memory" for the program; variable/value pairs go here */
	static Map<String, Object> memory = new HashMap<String, Object>();
	
	public static void declStock(String value){
		Object obj = new String[4];
		memory.put(value, obj);
	}
	
	public static void setSymbol(String id, String symbol){
		String[] tempArray = (String[]) memory.get(id);
		memory.put(id, new String[] {symbol, tempArray[1], tempArray[2], tempArray[3]});
	}
	
	public static String getSymbol(String id){
		String[] value = (String[]) memory.get(id);
		return value[0];
	}
	
	public static void setCompanyName(String id, String companyName){
		String[] tempArray = (String[]) memory.get(id);
		memory.put(id, new String[] {tempArray[0], companyName, tempArray[2], tempArray[3]});
	}
	
	public static String getCompanyName(String id){
		String[] value = (String[]) memory.get(id);
		return value[1];
	}
	
	public static void setCurrentPrice(String id, Object currentPrice){
		String[] tempArray = (String[]) memory.get(id);
		memory.put(id, new String[] {tempArray[0], tempArray[1], currentPrice.toString(), tempArray[3]});
	}
	
	public static Double getCurrentPrice(String id){
		String[] value = (String[]) memory.get(id);
		return Double.valueOf(value[2]);
	}
	
	public static void setYearHigh(String id, Object yearHigh){
		String[] tempArray = (String[]) memory.get(id);
		memory.put(id, new String[] {tempArray[0], tempArray[1], tempArray[2], yearHigh.toString()});
	}
	
	public static Double getYearHigh(String id){
		String[] value = (String[]) memory.get(id);
		return Double.valueOf(value[3]);
	}
	
}
