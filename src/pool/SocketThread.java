package pool;


import java.io.IOException;
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
		this.lastHeartbeat = new Date().getTime();
	}

	/**
	 * 获取最后的心跳时间
	 * 
	 * @return
	 */
	public long getHeartBeatLastTime() {
		return this.lastHeartbeat;
	}

	/**
	 * 心跳更新
	 */
	private void heartbeat(String msg) {
		if (msg.equals("heartbeat")) {
			System.out.println("heartbeat :" + new Date().toString());
			this.lastHeartbeat = new Date().getTime();
		}

	}

	@Override
	public void run() {

		try {
			while (true) {
				if (client == null) {
					return;
				}
				String revMsg = Protocol.decodeMsg(client.getInputStream());
				this.heartbeat(revMsg);
				String[] rspMsg = Route.run(revMsg);
				Protocol.sendMsg(client.getOutputStream(), rspMsg);

			}
		} catch (IOException e) {
			System.out.println("exception:" + e.getMessage());
		}

	}

	public void close() {
		try {
			this.client.close();
		} catch (Exception e1) {

			e1.printStackTrace();
		}
	}
}
