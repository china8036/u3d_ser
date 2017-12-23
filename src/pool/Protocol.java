package pool;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Protocol {

	
	/**
	 * 解析msg
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String decodeMsg(InputStream in) throws IOException {
		byte[] lenByte = new byte[4];
		int readLen = in.read(lenByte, 0, 4);
		int msgLen = byteArrayToInt(lenByte);
		char[] msgChar = new char[msgLen];
		readLen = new InputStreamReader(in).read(msgChar, 0, msgLen);
		System.out.println("rev msg Char Len:" + readLen);
		System.out.println("msg:");
		System.out.println(msgChar);
		return String.valueOf(msgChar);
	}

	
	
	/**
	 * 发送消息
	 * @param out
	 * @param msg
	 * @throws IOException
	 */
	public static void sendMsg(OutputStream out, String msg) throws IOException {
		out.write(intToByteArray(msg.length()));
		out.write(msg.getBytes());
		out.flush();
	}
	
	
	/**
	 * 字节数组转整形
	 * @param b
	 * @return
	 */
	public static int byteArrayToInt(byte[] b) {
		if(b.length !=4 ) {
			return 0;
		}
		//java 是 Big endian 
		//Windos(x86,x64)和Linux(x86,x64)都是Little Endian操作系统  
		//C/C++语言编写的程序里数据存储顺序是跟编译平台所在的CPU相关的
		return   b[0] & 0xFF |   
		            (b[1] & 0xFF) << 8 |   
		            (b[2] & 0xFF) << 16 |   
		            (b[3] & 0xFF) << 24;   
		}   
	
	 
	//整形转字节数据 按 Little Endian
	public static byte[] intToByteArray(int a) {
		byte[] b = new byte[4];
		 
		b[0] = (byte)(a & 0xff);
		b[1] = (byte)(a>>8 & 0xff);
		b[2] = (byte)(a>>16 & 0xff);
		b[3] = (byte)(a>>24 & 0xff);
		return b;
	}
	
  
	
}
