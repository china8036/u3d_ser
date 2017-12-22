import pool.Pool;

public class Server {

	public static void main(String[] args) {
		(new Pool(8888)).start();

	}
}
