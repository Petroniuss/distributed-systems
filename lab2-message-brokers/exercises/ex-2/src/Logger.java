public class Logger {

    public static void logProducer(String message) {
        log("Producer", message);
    }

    public static void logConsumer(String message) {
        log("Consumer", message);
    }

    private static void log(String who, String message) {
        System.out.printf("%s>%s\n", who, message);
    }
    
}
