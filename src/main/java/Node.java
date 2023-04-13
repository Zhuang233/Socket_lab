import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Node {
    public NodeInfo info; // 本节点信息
    public AcceptThread acceptThread;// 接收线程
    public Send send;//发送类
    public AccessNode accessNode;//邻点
    public ComTable comTable;//映射表（可通过mac查ip,port）
    public SendStatusManager sendStatusManager;// 发送状态类
    public List<SendStatus> sendStatusList;// 发送状态列表
    public String status;// 本节点状态
    public Window w;
    public DrawBoard board;

    public void Start() throws IOException {
        this.acceptThread = new AcceptThread(this.info.getName()+"  accept",this);
        acceptThread.start();
        sendStatusManager.start();
    }

    public void SetInfo() throws IOException {
        info.port = Integer.parseInt(w.port.getText());
        info.x = (byte)Integer.parseInt(w.X.getText());
        info.y = (byte)Integer.parseInt(w.Y.getText());
        info.mac = w.mac.getText().getBytes(StandardCharsets.UTF_8)[0];
        comTable.Add(this.info);

        board.AddNode(info.getName(),info.x,info.y);
        Start();
    }

    public void Send(){
        send = new Send(this);
        send.start();
    }

    public void BroadcastXY() throws IOException {
        // 生成位置帧
        Package p = new Package();
        p.type = "EXCHANGE";
        p.srcMac = this.info.mac;
        p.x = this.info.x;
        p.y = this.info.y;

        w.print(info.getName()+"广播位置帧");
        // 向comTable里的所有非己节点发送位置帧
        for(NodeInfo info:comTable.comItems){
            if(info.mac != this.info.mac){
                Socket s = new Socket(info.ip, info.port);
                OutputStream o = s.getOutputStream();
                o.write(p.Pack());
                o.flush();
                o.close();
                s.close();
            }
        }
        board.NodeSendExchange(info.getName());

    }

    // 工具方法
    public String getName(byte mac){
        return new String(new byte[]{mac});
    }


    public Node(DrawBoard board,ComTable comTable, String ip,String port,String mac,String x,String y) throws IOException {
        this.info = new NodeInfo();
//        this.info.mac = mac;
        this.info.ip = ip;
//        this.info.port = port;
        this.comTable = comTable;

        this.accessNode = new AccessNode(this);
        this.sendStatusList = new ArrayList<>();
        this.sendStatusManager = new SendStatusManager(this.info.getName()+"  scaner",this, sendStatusList);
        this.w = new Window(this);
        w.port.setText(port);
        w.mac.setText(mac);
        w.X.setText(x);
        w.Y.setText(y);
        this.board = board;
    }
}
