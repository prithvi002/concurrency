public class Main {

    public static void main(String[] args) {

        BlockingStack<Integer> stack = new BlockingStack<>(3);

        // Producer
        new Thread(() -> {
            int i = 1;
            try {
                while (true) {
                    stack.push(i);
                    System.out.println("Pushed " + i);
                    i++;
                    Thread.sleep(200);
                }
            } catch (Exception e) {}
        }).start();

        // Consumer
        new Thread(() -> {
            try {
                while (true) {
                    int val = stack.pop();
                    System.out.println("Popped " + val);
                    Thread.sleep(500);
                }
            } catch (Exception e) {}
        }).start();
    }
}
