package pool;


import storage.Storage;

public class Route {

	private static Storage storage;
	
	
	
	public static String[] run(String msg) {
		return new String[] {msg};
	}
	
	
	/**
	 * 单例获取storage
	 * @return
	 */
	public static Storage getStorage() {
		
		if(storage != null) {
			return storage;
		}
		storage = new Storage();
		return storage;
	}
	
}
