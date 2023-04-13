public class RestoreThread extends Thread{
    DrawBoard board;
    String name;
    int time;

    public void run() {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        board.NodeNone(name);
    }

    public RestoreThread(DrawBoard board){
        this.board = board;
    }
}
