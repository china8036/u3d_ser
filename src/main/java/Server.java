
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import pool.Protocol;
//import pool.Pool;

public class Server {

	public static void main(String[] args) {
		//(new Pool(8888)).start();
		int bytesEchoed = 0;   
		ServerSocketChannel serverSocketChannel;
		Protocol pcl  = new Protocol();
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(8888));
			serverSocketChannel.configureBlocking(false);
			Selector selector = Selector.open();
			SelectionKey key = serverSocketChannel.register(selector,   SelectionKey.OP_ACCEPT);
			while(true) {
				int readyChannels = selector.select();
				if(readyChannels == 0) continue;
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				while(keyIterator.hasNext()) {
                     key =keyIterator.next();
                     if(key.isAcceptable()) {//有新的链接
                    	ServerSocketChannel serverChanel = (ServerSocketChannel)key.channel();    
                    	SocketChannel sc = serverChanel.accept(); 
                    	sc.configureBlocking( false );   
                    	sc.register( selector,  SelectionKey.OP_READ | SelectionKey.OP_WRITE); //注册学号成功，并分配学生的权限   
                    	keyIterator.remove(); //新生注册任务完成了，呵呵   
                    	System.out.println( "Got connection from "+sc );   
                     }else if(key.isWritable()){
                    	 SocketChannel sc = (SocketChannel)key.channel(); //通过学号知道是谁问的问题 
                    	 String newData = "New String to write to file..." + System.currentTimeMillis();
                         System.out.print(newData);
                    	 ByteBuffer buf = ByteBuffer.allocate(512);
                    	 buf.clear();
                    	 buf.put(Protocol.encodeMsg(newData));

                    	 buf.flip();

                    	 while(buf.hasRemaining()) {
                    	     sc.write(buf);
                    	 }
                    	 
                    	 
                     }else if(key.isReadable()) {//有数据发来
                    	SocketChannel sc = (SocketChannel)key.channel(); //通过学号知道是谁问的问题   
                    	ByteBuffer echoBuffer = ByteBuffer.allocate(1024);
                    	
                    	while((bytesEchoed = sc.read(echoBuffer))> 0){   
                    		System.out.println("bytesEchoed:"+bytesEchoed);   
                    	}   
                    	echoBuffer.flip();   //ByteBuffer 读写转换
                    	byte [] content = new byte[echoBuffer.limit()];   
                    	echoBuffer.get(content);   
                    	pcl.dealPackage(content);
                    	echoBuffer.clear();   
                    	keyIterator.remove(); //任务完成，记得上面也是一样，要remove掉，否则下一次又来一次任务，就死循环了
                     }else {
                    	 System.out.println("Undefinded Status!");
                     }
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
