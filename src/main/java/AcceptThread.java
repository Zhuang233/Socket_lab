import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AcceptThread extends Thread{
    public Node p;
    private ServerSocket serverSocket;

    public void run(){
//        System.out.println(p.info.getName()+"开启监听");
        p.w.print("开始监听");
        while (true) {
            try {
                Socket s = serverSocket.accept();
                new RecvThread(p,s).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public AcceptThread(String name, Node p) throws IOException {
        super(name);
        this.p = p;
        this.serverSocket = new ServerSocket(p.info.port);
    }

}