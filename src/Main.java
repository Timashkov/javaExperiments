import java.util.concurrent.atomic.AtomicBoolean;

class RecursionExample {

    int a = 0;

    synchronized public void inc() {
        if (a < 10) {
            System.out.println(a);
            a++;
            inc();
        }
    }

}

class Foot implements Runnable {

    String side;
    AtomicBoolean currentSide;

    public Foot(String side, AtomicBoolean currentSide) {
        this.side = side;
        this.currentSide = currentSide;
    }

    private void step() {
        for (int i = 0; i < 100; i++) {
            synchronized (currentSide) {
                while (currentSide.get() == (side.equals("Left"))) {
                    try {
                        currentSide.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Step " + side);
                currentSide.compareAndSet(!side.equals("Left"), side.equals("Left"));
                currentSide.notifyAll();
            }
        }
    }

    @Override
    public void run() {
        step();
    }
}


public class Main {
    public static void main(String... args) {
        System.out.println("Started");

        RecursionExample ex = new RecursionExample();
        ex.inc();

        AtomicBoolean currentSide = new AtomicBoolean(false);
        new Thread(new Foot("Left", currentSide)).start();
        new Thread(new Foot("Right", currentSide)).start();
    }
}
