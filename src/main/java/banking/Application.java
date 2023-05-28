package banking;

import banking.entity.AppImpl;

public class Application {
    public static void main(String[] args) throws Exception {
        new AppImpl(args).run();
    }
}
