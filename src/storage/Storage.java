package storage;


import redis.clients.jedis.Jedis;

public class Storage {

	private Jedis jedis;

	public Storage() {
		this.jedis = new Jedis("127.0.0.1", 6379);
        System.out.println("连接成功");
	}
	
	/**
	 * 设置 key 为 val
	 * @param key
	 * @param val
	 */
	public void set(String key, String val) {
		this.jedis.set(key, val);
		
	}
	
	/**
	 * 获取key的val
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return this.jedis.get(key);
	}
	
	public void close() {
		this.jedis.close();
	}
	
	
	
}
