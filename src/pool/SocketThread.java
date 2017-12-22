package pool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * 实现收发功能
 * 
 * @author Administrator
 *
 */
public class SocketThread extends Thread {

	private Socket client;

	private Pool pool;

	private long lastHeartbeat;

	public SocketThread(Pool pool, Socket client) {
		this.pool = pool;
		this.client = client;
		this.pool.addClient(this);
	}

	
	/**
	 * 获取最后的心跳时间
	 * @return
	 */
	public long getHeartBeatLastTime() {
		return this.lastHeartbeat;
	}
	
	/**
	 * 心跳更新
	 */
	private void heartbeat(String msg) {
		if(msg.equals("heartbeat")) {
			System.out.println("heartbeat :" + new Date().toString());
			this.lastHeartbeat = new Date().getTime();
		}
		
	}

	@Override
	public void run() {
		BufferedReader in;
		PrintWriter out;
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {
			while (true) {
				if(in == null) {
					return;
				}
				String info = in.readLine();
				this.heartbeat(info);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			in.close();
			out.close();
		} catch (Exception e1) {

			e1.printStackTrace();
		}

	}
	
	public void close() {
		
	}
}
