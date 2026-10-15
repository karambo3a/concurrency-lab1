package org.labs;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            throw new IllegalArgumentException();
        }
        int programmersCount = Integer.parseInt(args[0]);
        int waitersCount = Integer.parseInt(args[1]);
        long foodCount = Long.parseLong(args[2]);

        Application application = new Application(new ApplicationConfig(programmersCount, waitersCount, foodCount));
        application.start();
        application.stop();
    }
}
