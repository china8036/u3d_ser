package storage;


import java.util.Hashtable;



public class Storage {
	
	
	 static Hashtable msg = new Hashtable();
	 

	


	
	/**
	 * 设置 key 为 val
	 * @param key
	 * @param val
	 */
	public void set(String key, String val) {
		msg.put(key, val);
		
	}
	
	/**
	 * 获取key的val
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return (String)msg.get(key);
	}
	
	

	/**
	 * 关闭
	 */
	public void close() {
		
	}
	
	
	
}
