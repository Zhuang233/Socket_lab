import java.time.LocalTime;

public class SendStatus {
    public LocalTime moment;
    public boolean cts;
    public byte src;
    public byte dest;

    public SendStatus(LocalTime moment, boolean cts, byte src, byte dest) {
        this.moment = moment;
        this.cts = cts;
        this.src = src;
        this.dest = dest;
    }

    public SendStatus() {
        this.moment = null;
        this.cts = false;
        this.src = 127;
        this.dest = 127;
    }

}
