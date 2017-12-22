package pool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 实现收发功能
 * @author Administrator
 *
 */
public class SocketThread extends Thread {

	private Socket client;

	private Pool pool;

	public SocketThread(Pool pool, Socket client) {
		this.pool = pool;
		this.client = client;
		this.pool.addClient(this.client);
	}

	@Override
	public void run() {
		BufferedReader in;
		PrintWriter out;
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream());
		} catch (IOException e) {
			this.pool.closeClient(client);// 关闭链接
			e.printStackTrace();
			return;
		}

		try {
			while (true) {
				String info = in.readLine();
		     	this.pool.sendToAllClients(info);
//				out.println(info);
//				out.flush();
				System.out.println(info);

			}
		} catch (IOException e) {
			try {
				in.close();
				out.close();
			}catch(Exception e1) {
				
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}
}
