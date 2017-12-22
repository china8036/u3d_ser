package pool;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

public class Protocol {

	public static String getMsg(InputStreamReader in) throws IOException {
		char[] lenChar = new char[4];
		byte[] lenByte = new byte[4];
		int readdLen = in.read(lenChar, 0, 4);
		System.out.println((int) lenChar[0]);
		System.out.println((int) lenChar[1]);
		System.out.println((int) lenChar[2]);
		System.out.println((int) lenChar[3]);
//		lenByte[0] = (byte) lenChar[0];
//		lenByte[1] = (byte) lenChar[1];
//		lenByte[2] = (byte) lenChar[2];
//		lenByte[3] = (byte) lenChar[3];
//		System.out.println(bytesToLong(lenByte));
		return "";
	}

	public static int bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();// need flip
		return buffer.getInt();
	}
}
