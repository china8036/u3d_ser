package pool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;

public class Protocol {

	/**
	 * buffer 长度
	 */
	private final int BUFFER_LEN = 1024;

	/**
	 * 消息提最长长度
	 */
	private final int MAX_MSG_LEN = 1024;

	/**
	 * 标识msg长度的信息的字节长度
	 */
	private final int LEN_BYTES_LENGTH = 4;

	/**
	 * 读取的字节
	 */
	private byte[] readByte = new byte[BUFFER_LEN];

	/**
	 * 标识newMsg还是oldMsg
	 */
	private boolean isNewMsg = true;

	/**
	 * 本次消息完整长度
	 */
	private int msgLen = 0;

	/**
	 * 本次消息还缺少多长
	 */
	private int msgLackLen = 0;

	/**
	 * 本次消息字节数组
	 */
	private byte[] msgByte = new byte[MAX_MSG_LEN];

	/**
	 * 待处理的
	 */
	private byte[] waitMsg;

	/**
	 * 输入流
	 */
	private InputStream in;

	/**
	 * 处理完成的msg队列
	 */
	private Queue<String> queue = new LinkedList<String>();

	/**
	 * 最后的心跳时间
	 */
	private long lastHeartbeat = new Date().getTime();

	/**
	 * 构造
	 * 
	 * @param in
	 */
	public Protocol(InputStream in) {
		this.in = in;
	}
	
	public Protocol () {
		
	}
	

	/**
	 * 解析msg
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public void decodeMsg() throws IOException {
		int readLen = in.read(readByte, 0, readByte.length);
		dealPackage(Arrays.copyOfRange(readByte, 0, readLen));
	}

	/**
	 * 获取消息列表
	 * 
	 * @return
	 */
	public List<String> getRspMsg() {
		int qSize = this.queue.size();
		List<String> msg = new ArrayList<String>();
		if (qSize < 1) {
			return null;
		}
		for (int i = 0; i < qSize; i++) {
			String tmpMsg = this.queue.poll();
			this.dealHeartbeat(tmpMsg);
			String[] tmpRsp = Route.run(tmpMsg);
			for (int tmpI = 0; tmpI < tmpRsp.length; tmpI++) {
				msg.add(tmpRsp[tmpI]);
			}
		}
		return msg;
	}

	/**
	 * 心跳更新
	 */
	private void dealHeartbeat(String msg) {
		System.out.println("recv msg: " + msg);
		if (msg.equals("heartbeat")) {
			System.out.println("heartbeat :" + new Date().toString());
			this.lastHeartbeat = new Date().getTime();
		}

	}

	/**
	 * 获取最后心跳时间
	 * 
	 * @return
	 */
	public long getHeartBeatTime() {
		return this.lastHeartbeat;

	}

	/**
	 * 解决粘包 分包问题 如果一条信息里面刚好含有一个4字节长msgLen后方跟了msgLen + (1-3)字节长度的信息 那么下次处理的 包不包含完整的
	 * msgLen协议定义的4个字节长度
	 * 
	 * @param msgByte
	 */
	public void dealPackage(byte[] msgByte) {
		int len = msgByte.length;
		if (this.isNewMsg) {// 新的消息
			System.out.println("Memory:");
			System.out.println(Runtime.getRuntime().freeMemory());

			// 处理未满4个字节的数据
			if (this.waitMsg != null) {
				byte[] tmpMsg = arrayMerge(this.waitMsg, msgByte);
				this.waitMsg = null;
				this.dealPackage(tmpMsg);
				return;
			}
			if (len < LEN_BYTES_LENGTH) {// 少于4个字节长度标示
				this.waitMsg = msgByte;
				return;// 终止

			}
			// 满4个字节后的处理逻辑
			this.msgLen = byteArrayToInt(new byte[] { msgByte[0], msgByte[1], msgByte[2], msgByte[3] });// 暂未跟随LEN_BYTES_LENGTH处理
			if (this.msgLen > MAX_MSG_LEN) {
				System.out.println("the len out of range max mag len");
				System.out.println(this.msgLen);
				return;
			}
			if (this.msgLen + LEN_BYTES_LENGTH <= len) {// 此msgByte里面含有完整的此条信息
				this.isNewMsg = true;
				pushMsg(new String(Arrays.copyOfRange(msgByte, LEN_BYTES_LENGTH, msgLen + LEN_BYTES_LENGTH)));
				//queue.add(new String(Arrays.copyOfRange(msgByte, LEN_BYTES_LENGTH, msgLen + LEN_BYTES_LENGTH)));
				if ((this.msgLen + LEN_BYTES_LENGTH) == len) {// 正好相等处理结束
					return;// 完成此次拼接
				}
				this.dealPackage(Arrays.copyOfRange(msgByte, msgLen + LEN_BYTES_LENGTH, len));// 递归处理剩余的字节
			} else {// 长度小于msgLen 下一条按未完成消息处理
				this.isNewMsg = false;
				this.msgLackLen = this.msgLen - len + LEN_BYTES_LENGTH;
				System.arraycopy(msgByte, LEN_BYTES_LENGTH, this.msgByte, 0, len - LEN_BYTES_LENGTH);
			}

		} else {// 未完成拼接的消息

			if (this.msgLackLen > len) {// 本次仍然长度不够
				for (int i = 0; i < len; i++) {
					this.msgByte[this.msgLen - this.msgLackLen + i] = msgByte[i];// 赋值给tMsgByte 等下次消息继续拼接
				}
				this.msgLackLen -= len;
			} else {
				this.isNewMsg = true;// 下次按newMsg处理
				System.arraycopy(msgByte, 0, this.msgByte, this.msgLen - this.msgLackLen, this.msgLackLen);
				pushMsg(new String(Arrays.copyOfRange(this.msgByte, 0, this.msgLen)));
				//queue.add(new String(Arrays.copyOfRange(this.msgByte, 0, this.msgLen)));
				;// 完成拼接 并把此消息加入队列
				if (this.msgLackLen == len) {
					this.msgLen = this.msgLackLen = 0;
					return;// 完成
				}
				this.dealPackage(Arrays.copyOfRange(msgByte, this.msgLackLen, len));
			}
		}

	}
	
	/**
	 * 
	 * @param msg
	 */
	protected void pushMsg(String msg) {
		System.out.println("decode msg :" + msg);
		//queue.add(msg);
	}

	/**
	 * 发送消息
	 * 
	 * @param out
	 * @param msg
	 * @throws IOException
	 */
	public static void sendMsg(OutputStream out, List<String> msg) throws IOException {
		if (msg == null) {
			return;
		}
		int length = msg.size();
		for (int i = 0; i < length; i++) {
			out.write(intToByteArray(msg.get(i).length()));
			out.write(msg.get(i).getBytes());
		}
		out.flush();
	}
	
	/**
	 * 根据协议encode
	 * @param msg
	 * @return
	 */
	public static byte[]  encodeMsg(String msg) {
		return arrayMerge(intToByteArray(msg.length()), msg.getBytes());
	}
	

	public static byte[] arrayMerge(byte[] a, byte[] b) {
		byte[] tmpMsg = new byte[a.length + b.length];
		System.arraycopy(a, 0, tmpMsg, 0, a.length);
		System.arraycopy(b, 0, tmpMsg, a.length, b.length);
		return tmpMsg;
	}

	/**
	 * 字节数组转整形
	 * 
	 * @param b
	 * @return
	 */
	public static int byteArrayToInt(byte[] b) {
		if (b.length != 4) {
			return 0;
		}
		// java 是 Big endian
		// Windos(x86,x64)和Linux(x86,x64)都是Little Endian操作系统
		// C/C++语言编写的程序里数据存储顺序是跟编译平台所在的CPU相关的
		return b[0] & 0xFF | (b[1] & 0xFF) << 8 | (b[2] & 0xFF) << 16 | (b[3] & 0xFF) << 24;
	}

	// 整形转字节数据 按 Little Endian
	public static byte[] intToByteArray(int a) {
		byte[] b = new byte[4];

		b[0] = (byte) (a & 0xff);
		b[1] = (byte) (a >> 8 & 0xff);
		b[2] = (byte) (a >> 16 & 0xff);
		b[3] = (byte) (a >> 24 & 0xff);
		return b;
	}

}
