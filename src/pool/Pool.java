package pool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import storage.Storage;

public class Pool {

	private int port;

	private List<Socket> clientList = new ArrayList<Socket>();

	private Lock lock = new ReentrantLock();

	private final int MAX_CLIENT_NUM = 10;

	public Pool(int port) {
		this.port = port;
	}

	public void start() {
		try {
			ServerSocket server = new ServerSocket(this.port);
			while (true) {// 死循环
				System.out.println("wait for accept:");
				Socket client = server.accept();
				if (this.clientList.size() > MAX_CLIENT_NUM) {
					PrintWriter out = new PrintWriter(client.getOutputStream());
					out.println("[Error]too many client" + MAX_CLIENT_NUM);
					client.close();
				} else {
					new SocketThread(this, client).start();
					// addClient(client);
					// System.out.println("accept:" + client.getRemoteSocketAddress().toString());
					// BufferedReader in = new BufferedReader(new
					// InputStreamReader(client.getInputStream()));
					// PrintWriter out = new PrintWriter(client.getOutputStream());
					// BufferedReader userin = new BufferedReader(new InputStreamReader(System.in));

					// new ReceiveTread(server, in, out, userin, client).start();
					// new SendThread(out, userin, true).start();
				}

			}
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	/**
	 * 获取客服端列表
	 * @return
	 */
	public List<Socket> getClients() {
		return clientList;
	}
	
	
	/**
	 * 对所有客服端链接发送数据
	 * @param info
	 * @throws IOException
	 */
	public void sendToAllClients(String info) throws IOException {
		for(Socket client:clientList) {
			PrintWriter out = new PrintWriter(client.getOutputStream());
			out.println(info);
			out.flush();
			//out.close();
		}
		
	}
	
	/**
	 * 添加客服端记录
	 * 
	 * @param client
	 */
	public void addClient(Socket client) {
		lock.lock();
		if (this.clientList.contains(client)) {
			return;
		}
		this.clientList.add(client);
		lock.unlock();
	}

	/**
	 * 删除客服端记录
	 * 
	 * @param client
	 */
	public void delClient(Socket client) {
		this.clientList.remove(client);
	}
	
	
	
	/**
	 * 关闭客服端链接
	 * @param client
	 */
	public void closeClient(Socket client) {
		this.delClient(client);
		try {
			client.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
