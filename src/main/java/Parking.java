import java.util.concurrent.ThreadLocalRandom;

class Parking {
    static final int park_size = 5;
    static int max_places = 0;
    static final boolean[] park_places = new boolean[park_size];

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new Thread(new Car(i)).start();
            Thread.sleep(400);
        }
    }
    static class Car implements Runnable {
        private int carNumber;
        Car(int carNumber) {
            this.carNumber = carNumber;
        }
        @Override
        public void run() {
            System.out.printf("Машина №%d подъехала к парковке.\n", carNumber);
            try {
                int parkingNumber = -1;
                synchronized (park_places) {
                    if(max_places == park_size) {
                        park_places.wait();
                    }
                    for (int i = 0; i < park_size; i++) {
                        if (!park_places[i]) {
                            park_places[i] = true;
                            parkingNumber = i;
                            max_places = max_places + 1;
                            System.out.printf("Занято %d парковочных мест\n", max_places);
                            System.out.printf("Машина №%d заняла место %d.\n", carNumber, parkingNumber + 1);
                            break;
                        }
                    }
                }

                int randomNum = ThreadLocalRandom.current().nextInt(3000, 6000 + 1);
                Thread.sleep(randomNum);

                synchronized (park_places) {
                    max_places--;
                    park_places[parkingNumber] = false;
                    park_places.notify();
                }
                System.out.printf("Машина №%d покинула парковочное место %d.\n", carNumber, parkingNumber + 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}