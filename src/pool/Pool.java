package pool;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import pool.Timer.TimerTaskList;
import pool.Timer.TimerListener;

public class Pool implements TimerListener {

	private int port;

	private List<SocketThread> clientList = new ArrayList<SocketThread>();

	private Lock lock = new ReentrantLock();

	private final int MAX_CLIENT_NUM = 10;
	
	private final int TIMER_SECOND = 1;

	public Pool(int port) {
		this.port = port;
	}

	public void start() {
		try {
			ServerSocket server = new ServerSocket(this.port);
			this.runTimmer();
			while (true) {// 死循环
				System.out.println("wait for accept:");
				Socket client = server.accept();
				if (this.clientList.size() > MAX_CLIENT_NUM) {
					PrintWriter out = new PrintWriter(client.getOutputStream());
					out.println("[Error]too many client" + MAX_CLIENT_NUM);
					client.close();
				} else {
					new SocketThread(this, client).start();
				}

			}
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	
	/**
	 * 运行定时任务
	 */
	public void runTimmer() {
        // schedules the task to be run in an interval  
        new Timer().scheduleAtFixedRate(new TimerTaskList(this), 0, TIMER_SECOND * 1000);  
	}
	
	
	/**
	 * 定时执行
	 */
	public void onTimer() {
		for(SocketThread client:clientList) {
			
		}
		//System.out.println("Hello im pool on timer");
	}
	
	
	
	/**
	 * 获取客服端列表
	 * @return
	 */
	public List<SocketThread> getClients() {
		return clientList;
	}
	
	
	/**
	 * 对所有客服端链接发送数据
	 * @param info
	 * @throws IOException
	 */
	public void sendToAllClients(String info) throws IOException {
		for(SocketThread client:clientList) {
		}
		
	}
	
	/**
	 * 添加客服端记录
	 * 
	 * @param client
	 */
	public void addClient(SocketThread client) {
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
	public void delClient(SocketThread client) {
		this.clientList.remove(client);
	}
	
	
	
	/**
	 * 关闭客服端链接
	 * @param client
	 */
	public void closeClient(SocketThread client) {
		this.delClient(client);
		try {
			client.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
