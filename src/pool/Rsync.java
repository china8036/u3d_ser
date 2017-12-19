package pool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Rsync {

	private int port;

	public Rsync(int port) {
		this.port = port;
	}

	public void start() {
		try {
			ServerSocket server = new ServerSocket(this.port);
			while (true) {//死循环
				System.out.println("wait for accept:");
				Socket client = server.accept();
				System.out.println("accept:" + client.getInetAddress().toString());
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(client.getOutputStream());
				BufferedReader userin = new BufferedReader(new InputStreamReader(System.in));

				new ReceiveTread(server, in, out, userin, client).start();
				new SendThread(out, userin, true).start();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
