package pool;


import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;



public class Route {

	static Map<String, String> msgMap = new HashMap<String, String>();
	

	
	/**
	 * 路由处理
	 * @param msg
	 * @return
	 */
	public static String[] run(String msg) {
//		String[] data = msg.split(" ");
//		if(data.length == 5 && data[0].equals("pos")) {//位置信息
//			msgMap.put(data[1], msg);
//		}
//		String[] retMsg = new String[msgMap.size()];
//		int index = 0;
//	    for (String key : msgMap.keySet()) {
//	         //System.out.println("key= "+ key + " and value= " + map.get(key));
//	    	 retMsg[index] = (String)msgMap.get(key);
//	         index++;
//	     }
	   
//		return retMsg;
		return new String[] {msg};
	}
	
	

	
}
