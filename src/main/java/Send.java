import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class Send extends Thread{
    public Node p;
    public String data;
    public byte dest;
    public byte[] buf;
    public Socket socket;
    public OutputStream out;
    public InputStream in;

    public void run(){
        try {
            this.send();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void send() throws IOException, InterruptedException {
        this.dest = p.w.dest.getText().getBytes(StandardCharsets.UTF_8)[0];
        this.data = p.w.msg.getText();

        p.status = "INIT";
        p.w.print(p.info.getName()+"开始发送");

        // 检查信道情况
        if(!p.sendStatusList.isEmpty()){
            // 有人发送，判断是不是发送到本节点的邻点
            for(SendStatus s:p.sendStatusList){
                if(p.accessNode.IsAcc(s.dest)){
                    p.status = "FORBID";
                    p.w.print(p.info.getName()+"邻点正在接收其他数据，发送失败");
                    return;
                }
            }

            // 全为暴露站
            if(Objects.equals(p.status, "INIT")){
                p.w.print(p.info.getName()+"暴露站问题");
            }
        }
        // 信道空闲

        // 广播RTS帧
        Package rtsPkg = new Package();
        rtsPkg.type = "RTS";
        rtsPkg.srcMac = p.info.mac;
        rtsPkg.destMac = this.dest;

        this.socket = null;
        for (NodeInfo i:p.accessNode.infos){
            Socket s = new Socket(i.ip,i.port);
            OutputStream o = s.getOutputStream();
            InputStream in = s.getInputStream();
            if(i.mac == this.dest) { // 目标节点的socket，后续仍需使用
                this.socket = s;
                this.out = o;
                this.in = in;
            }
            p.w.print(p.info.getName()+"发送RTS给"+i.getName());
            o.write(rtsPkg.Pack());
        }
        p.board.NodeSendRTS(p.info.getName());

        // 等待CTS帧或失败帧
        assert this.socket != null;
        this.buf = new byte[1024];
        this.in.read(buf);

        Package ctsPkg = new Package().UnPack(buf);
        if(Objects.equals(ctsPkg.type, "CTS")){
            p.board.NodeNone(p.info.getName());
            p.w.print(p.info.getName()+"收到CTS");
        }
        else if(Objects.equals(ctsPkg.type, "FAIL")){
            p.w.print(p.info.getName()+"收到FAIL, 发送错误");
            p.board.NodeNone(p.info.getName());
            return;
        }else {
            p.w.print(p.info.getName()+"收预约回复帧出错");
            return;
        }

        // 发送数据帧
        Package pkg = new Package();
        pkg.type = "DATA";
        pkg.data = this.data;
        pkg.destMac = this.dest;
        pkg.srcMac = p.info.mac;
        p.w.print(p.info.getName()+"发送数据");
        this.out.write(pkg.Pack());
        p.board.NodeSendData(p.info.getName());


        // 等待回复帧
        Arrays.fill(buf, (byte) 0);
        this.in.read(buf);
        Package ansPkg = new Package().UnPack(buf);
        if (Objects.equals(ansPkg.anserType, "ACK")){
            p.w.print(p.info.getName()+"发送成功");
        }
        else if(Objects.equals(ansPkg.anserType, "NAK")){
            p.w.print(p.info.getName()+"发送失败  "+"冲突： "+pkg.sender1+"和"+pkg.sender2);
        }

    }


    public Send(Node p) {
        this.p = p;
    }
}
