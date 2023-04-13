import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;

public class SendStatusManager extends Thread{
    public Node p;
    public List<SendStatus> sendStatusList;// 发送状态列表

    public void run(){
        while(true){
            Iterator<SendStatus> it = sendStatusList.listIterator();
            while (it.hasNext()){
                SendStatus sendStatus = it.next();
                if (sendStatus.moment.plusSeconds(6).compareTo(LocalTime.now()) < 0){
                    it.remove();
                    p.w.print(p.info.getName()+"删除"+p.getName(sendStatus.src)+"状态");
                }

            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //删除
    public void Delete(byte mac){
        Iterator<SendStatus> it = sendStatusList.listIterator();
        while (it.hasNext()){
            SendStatus sendStatus = it.next();
            if (sendStatus.src == mac){
                it.remove();
                p.w.print(p.info.getName()+"删除"+p.getName(sendStatus.src)+"状态");
            }

        }
    }

    //检查指定节点是否存在发送列表
    public boolean IsExist(byte mac){
        for(SendStatus s:sendStatusList){
            if(s.src == mac){
                return true;
            }
        }
        return false;
    }


    public SendStatusManager(String name, Node p, List<SendStatus> sendStatusList) {
        super(name);
        this.p = p;
        this.sendStatusList = sendStatusList;
    }
}
