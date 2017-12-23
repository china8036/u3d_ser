package pool;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

public class Protocol {

	
	public static String getMsg(InputStream in) throws IOException {
		byte[] lenByte = new byte[4];
		int readLen = in.read(lenByte, 0, 4);
		int msgLen = byteArrayToInt(lenByte);
//		System.out.println("msg length:" + msgLen);
		char[] msgChar = new char[msgLen];
		readLen = new InputStreamReader(in).read(msgChar, 0, msgLen);
		System.out.println("rev msg Char Len:" + readLen);
//		System.out.println(msgByte);
//		if(readLen == -1) {
//			throw new Exception("read error");
//		}
//		System.out.println(msgChar);
		return String.valueOf(msgChar);
	}

	
	
	
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
}
