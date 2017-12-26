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



	@Override
	public void run() {
		try {
			Protocol ptl = new Protocol(client.getInputStream());
			while (true) {
				if (client == null) {
					return;
				}
				ptl.decodeMsg();
				String[] revMsg = ptl.getRevMsg();
				this.lastHeartbeat = ptl.getHeartBeatTime();
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
