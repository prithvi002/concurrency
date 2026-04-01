public class ThrowExample {

    // This method explicitly throws a checked exception
    public static void readFile(String path) throws Exception {
        if (path == null || path.isEmpty()) {
            throw new Exception("Invalid file path!");
        }

        System.out.println("Pretend we're reading file: " + path);
    }

    public static void main(String[] args) {

        try {
            readFile("");  // invalid path → will throw
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
        }

        System.out.println("Program continues...");
    }
}
