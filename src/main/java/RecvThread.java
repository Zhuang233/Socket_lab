import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;

public class RecvThread extends Thread{
    public Node p;
    public Socket s;
    public InputStream in;
    public OutputStream out;
    public byte macFrom;
    public byte macTo;

    public void run(){
        byte[]buf = new byte[1024];
        try {
            this.in = this.s.getInputStream();
            this.out = this.s.getOutputStream();

            // 读取帧
            this.in.read(buf);
            // 解析帧
            Package pkg = new Package().UnPack(buf);
            macFrom = pkg.srcMac;
            macTo = pkg.destMac;


            // 分类处理

            // 位置帧
            if(Objects.equals(pkg.type, "EXCHANGE")){
                if(p.accessNode.IsAcc(pkg.x, pkg.y)){
                    NodeInfo infoFromTable = p.comTable.checkByMac(pkg.srcMac);
                    NodeInfo infoAdd = new NodeInfo(pkg.srcMac, pkg.x, pkg.y, infoFromTable.ip,infoFromTable.port);
                    p.w.print(p.getName(pkg.srcMac)+"位置：("+pkg.x+","+pkg.y+"), 添加邻点");
                    p.accessNode.infos.add(infoAdd);
                }
                this.in.close();
                s.close();
            }
            // 数据帧(一般不存在)
            else if(Objects.equals(pkg.type, "DATA")){
                //------------------------------------------------------------------
                p.w.print(p.info.getName()+"收到数据： "+pkg.data);
                p.w.print("添加发送状态");
                p.sendStatusList.add(new SendStatus(LocalTime.now(),false,pkg.destMac, pkg.destMac));

                //------------------------------------------------------------------

            }
            // RTS帧
            else if(Objects.equals(pkg.type, "RTS")) {
                p.w.print(p.info.getName() + "收到RTS");
                int ret = DealWithRTS();
                if (ret == 1) {
                    //对方预约成功，准备接收数据
                    Arrays.fill(buf, (byte) 0);
                    this.in.read(buf);

                    // 解析数据
                    Package DataPkg = new Package().UnPack(buf);


                    // 返回数据应答帧(5秒内没有其他帧到来)
                    Thread.sleep(5000);

                    Package ansPkg = new Package();
                    ansPkg.type = "ANSWER";
                    ansPkg.destMac = macFrom;
                    ansPkg.srcMac = p.info.mac;

                    if(p.status == "CONFLICT"){
                        // 返回NAK
                        p.w.print(p.info.getName() + "接收数据冲突");
                        ansPkg.anserType = "NAK";
                        p.w.print(p.info.getName() + "返回NAK");
                        this.out.write(ansPkg.Pack());
                        // 广播NAK
                        for (NodeInfo i:p.accessNode.infos){
                            if(i.mac != macFrom){
                                Socket s = new Socket(i.ip,i.port);
                                OutputStream o = s.getOutputStream();
                                p.w.print(p.info.getName()+"广播NAK帧到"+i.getName());
                                o.write(ansPkg.Pack());
                            }
                        }
                    }
                    else {
                        // 返回ACK
                        p.w.print(p.info.getName() + "收到数据： " + DataPkg.data);
                        ansPkg.anserType = "ACK";
                        p.w.print(p.info.getName() + "返回ACK");
                        this.out.write(ansPkg.Pack());
                        // 广播ACK
                        for (NodeInfo i:p.accessNode.infos){
                            if(i.mac != macFrom){
                                Socket s = new Socket(i.ip,i.port);
                                OutputStream o = s.getOutputStream();
                                p.w.print(p.info.getName()+"广播ACK帧到"+i.getName());
                                o.write(ansPkg.Pack());
                            }
                        }
                        p.board.NodeSendACK(p.info.getName());
                    }



                }
            }
            // CTS帧(肯定不是给本节点的)
            else if(Objects.equals(pkg.type, "CTS")){
                // 没在发送状态列表则添加
                if(!p.sendStatusManager.IsExist(macFrom)){
                    p.w.print(p.info.getName()+"收到CTS  添加状态："+p.getName(macFrom)+"在接收数据");
                    p.sendStatusList.add(new SendStatus(LocalTime.now().plusSeconds(1),true,macTo,macFrom));
                }
                else {
                    p.w.print(p.info.getName()+"收到CTS  "+p.getName(macFrom)+"在发送数据，已存在");
                }

            }
            // 预约失败帧(肯定不是给本节点的)
            else if(Objects.equals(pkg.type, "FAIL")){
                p.w.print(p.info.getName()+"尝试删除"+p.getName(macTo));
                p.sendStatusManager.Delete(macTo);
            }
            // 数据应答帧
            else if(Objects.equals(pkg.type, "ANSWER")){
                p.w.print(p.info.getName()+"收到"+p.getName(macTo)+"的ANSWER， 不处理");
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    // 处理RTS帧
    public int DealWithRTS() throws IOException, InterruptedException {
        // 不是发给本节点的
        if(macTo != p.info.mac){

            // 本节点在接收RTS帧
            if(Objects.equals(p.status, "RECV_RTS")){
                p.status = "HIND";
                return 0;
            }
            // 本节点在发送数据帧
            else if(Objects.equals(p.status, "SEND_DATA")){
                p.sendStatusList.add(new SendStatus(LocalTime.now(),false, macFrom, macTo));
                return 0;
            }
            // 本节点空闲
            else {
                p.w.print(p.info.getName()+"添加状态: "+p.getName(macFrom)+"在申请");
                p.sendStatusList.add(new SendStatus(LocalTime.now(),false, macFrom, macTo));
                return 0;
            }

//            p.w.print(p.info.getName()+"丢弃RTS");
        }
        // 是发给本节点的
        else {
            // 创建预约结果回复帧
            Package resPkg = new Package();
            resPkg.srcMac = p.info.mac;
            resPkg.destMac = macFrom;

            // 已经在接收其他节点的RTS
            if(Objects.equals(p.status, "RECV_RTS")){
                p.status = "HIND";
                p.w.print(p.info.getName()+"收到"+p.getName(this.macFrom)+"的RTS: 隐蔽站问题");
                // 回复预约失败帧
                resPkg.type = "FAIL";
                p.w.print(p.info.getName()+"回复预约失败帧到"+p.getName(this.macFrom));
                this.out.write(resPkg.Pack());
                // 广播预约失败帧
                for (NodeInfo i:p.accessNode.infos){
                    if(i.mac != macFrom){
                        Socket s = new Socket(i.ip,i.port);
                        OutputStream o = s.getOutputStream();
                        p.w.print(p.info.getName()+"广播预约失败帧到"+i.getName());
                        o.write(resPkg.Pack());
                    }
                }
                p.board.NodeSendFail(p.info.getName());
                return 0;
            }


            // 等待1s
            p.status = "RECV_RTS";
            Thread.sleep(1000);
            // 检查节点状态
            // 1s内其他节点又来了个预约，隐蔽站问题
            if(Objects.equals(p.status, "HIND")){
                p.w.print(p.info.getName()+"收到"+p.getName(this.macFrom)+"的RTS: 隐蔽站问题");
                // 回复预约失败帧
                resPkg.type = "FAIL";
                p.w.print(p.info.getName()+"回复预约失败帧到"+p.getName(this.macFrom));
                this.out.write(resPkg.Pack());
                // 广播预约失败帧
                for (NodeInfo i:p.accessNode.infos){
                    if(i.mac != macFrom){
                        Socket s = new Socket(i.ip,i.port);
                        OutputStream o = s.getOutputStream();
                        p.w.print(p.info.getName()+"广播预约失败帧到"+i.getName());
                        o.write(resPkg.Pack());
                    }
                }
                p.board.NodeSendFail(p.info.getName());

                return 0;
            }
            else {
                p.status = "RECV_DATA";
                // 回复CTS帧
                resPkg.type = "CTS";
                this.out.write(resPkg.Pack());
                p.w.print(p.info.getName()+"回复CTS帧");
                // 广播CTS帧（给其他邻节点）
                for (NodeInfo i:p.accessNode.infos){
                    if(i.mac != macFrom){
                        Socket s = new Socket(i.ip,i.port);
                        OutputStream o = s.getOutputStream();
                        p.w.print(p.info.getName()+"广播CTS帧到"+i.getName());
                        o.write(resPkg.Pack());
                    }
                }
                p.board.NodeSendCTS(p.info.getName());
                return 1;
            }

        }


    }


    public RecvThread(Node p, Socket s) {
        this.p = p;
        this.s = s;
    }
}
