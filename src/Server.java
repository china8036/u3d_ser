import pool.Rsync;

public class Server {

	public static void main(String[] args) {
		(new Rsync(8888)).start();

	}
}
