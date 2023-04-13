import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class Package {
    public String data;
    public String type;
    public byte srcMac;
    public byte destMac;
    public byte x;
    public byte y;
    public String anserType;
    public byte sender1;
    public byte sender2;

    // 报文类型：数据帧，数据应答帧，交换地理位置帧，RTS帧，CTS帧
    public enum TYPE{DATA, ANSWER, EXCHANGE, RTS, CTS, FAIL}


    public byte[] Pack(){
        //地理位置帧
        if(Objects.equals(type, "EXCHANGE")){
            byte[] Package = new byte[4];

            // 填充数据帧
            Package[0] = (byte) TYPE.EXCHANGE.ordinal();
            Package[1] = this.srcMac;
            Package[2] = this.x;
            Package[3] = this.y;

            return Package;
        }
        // 数据帧
        if(Objects.equals(type, "DATA")){
            // 字节转换 计算帧长
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            short dataLen = (short) (dataBytes.length);
            byte[] Package = new byte[dataLen + 5];

            ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.putShort(dataLen);
            byte packType = (byte) TYPE.DATA.ordinal();

            // 填充数据帧
            Package[0] = packType;
            Package[1] = this.destMac;
            Package[2] = this.srcMac;
            Package[3] = buffer.get(0);
            Package[4] = buffer.get(1);
            int dataOffest = 5;
            for(byte b:dataBytes){
                Package[dataOffest] = b;
                dataOffest ++;
            }
            return Package;
        }
        //CTS帧
        else if(Objects.equals(type, "CTS")){
            byte[] Package = new byte[3];
            byte packType = (byte) TYPE.CTS.ordinal();

            // 填充数据帧
            Package[0] = packType;
            Package[1] = this.destMac;
            Package[2] = this.srcMac;
            return Package;
        }
        //FAIL帧
        else if(Objects.equals(type, "FAIL")){
            byte[] Package = new byte[3];
            byte packType = (byte) TYPE.FAIL.ordinal();

            // 填充数据帧
            Package[0] = packType;
            Package[1] = this.destMac;
            Package[2] = this.srcMac;
            return Package;
        }
        //RTS帧
        else if(Objects.equals(type, "RTS")){
            byte[] Package = new byte[3];
            byte packType = (byte) TYPE.RTS.ordinal();

            // 填充数据帧
            Package[0] = packType;
            Package[1] = this.destMac;
            Package[2] = this.srcMac;
            return Package;
        }
        // 数据应答帧
        else if(Objects.equals(type, "ANSWER")){
            byte[] Package = new byte[6];
            byte packType = (byte) TYPE.ANSWER.ordinal();

            // 填充数据帧
            Package[0] = packType;
            Package[1] = this.destMac;
            Package[2] = this.srcMac;
            if(Objects.equals(anserType, "ACK")){
                Package[3] = 1;
                Package[4] = 0;
                Package[5] = 0;
            }
            else if(Objects.equals(anserType, "NAK")){
                Package[3] = 0;
                Package[4] = this.sender1;
                Package[5] = this.sender2;
            }
            return Package;
        }





        return new byte[0];
    }

    public Package UnPack(byte[] buf){
        // 位置帧
        if(buf[0] == (byte) TYPE.EXCHANGE.ordinal()){
            this.type = "EXCHANGE";
            this.srcMac = buf[1];
            this.x = buf[2];
            this.y = buf[3];
            return this;
        }
        // 数据帧
        if(buf[0] == (byte) TYPE.DATA.ordinal()){
            type = "DATA";
            this.destMac = buf[1];
            this.srcMac = buf[2];
            byte[] dataLenBytes = {buf[3],buf[4]};
            short dataLen = ByteBuffer.wrap(dataLenBytes).getShort();
            byte[] dataBytes = Arrays.copyOfRange(buf, 5, 5+dataLen);
            data = new String(dataBytes);
            return this;
        }
        // CTS帧
        if(buf[0] == (byte) TYPE.CTS.ordinal()){
            this.type = "CTS";
            this.destMac = buf[1];
            this.srcMac = buf[2];
            return this;
        }
        // FAIL帧
        if(buf[0] == (byte) TYPE.FAIL.ordinal()){
            this.type = "FAIL";
            this.destMac = buf[1];
            this.srcMac = buf[2];
            return this;
        }
        // RTS帧
        if(buf[0] == (byte) TYPE.RTS.ordinal()){
            this.type = "RTS";
            this.destMac = buf[1];
            this.srcMac = buf[2];
            return this;
        }
        // 数据应答帧
        if(buf[0] == (byte) TYPE.ANSWER.ordinal()){
            this.type = "ANSWER";
            this.destMac = buf[1];
            this.srcMac = buf[2];
            if(buf[3] == 1)
                this.anserType = "ACK";
            else this.anserType = "NAK";

            this.sender1 = buf[4];
            this.sender2 = buf[5];
            return this;
        }




        return null;
    }






}
