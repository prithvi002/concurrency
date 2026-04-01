public class ExceptionsDemo {

    public static void main(String[] args) {

        System.out.println("===== 1. Basic Exception (No try-catch) =====");
        basicCrashExample();

        System.out.println("\n===== 2. Basic try-catch =====");
        basicTryCatch();

        System.out.println("\n===== 3. Multiple catch blocks =====");
        multipleCatchExample();

        System.out.println("\n===== 4. try-catch-finally =====");
        tryCatchFinallyExample();

        System.out.println("\n===== 5. throw + throws (Custom Exception) =====");
        customExceptionExample();

        System.out.println("\n===== 6. throw + throws (Built-in Exception) =====");
        builtinThrowsExample();

        System.out.println("\n===== END OF DEMO =====");
    }


    // -----------------------------------------------------------
    // 1. No Try-Catch → Crash Example
    // -----------------------------------------------------------
    static void basicCrashExample() {
        try {
            System.out.println("Before risky division...");
            int result = 10 / 0;   // This throws ArithmeticException
            System.out.println("This will not print: " + result);
        } catch (Exception e) {
            System.out.println("Caught crash: " + e);
        }
    }


    // -----------------------------------------------------------
    // 2. Basic try-catch
    // -----------------------------------------------------------
    static void basicTryCatch() {
        try {
            int a = 10;
            int b = 0;
            int c = a / b;  // risky
            System.out.println("Result = " + c);
        } catch (ArithmeticException e) {
            System.out.println("Caught ArithmeticException: " + e.getMessage());
        }

        System.out.println("Program continues after try-catch");
    }


    // -----------------------------------------------------------
    // 3. Multiple catch blocks
    // -----------------------------------------------------------
    static void multipleCatchExample() {
        String text = null;

        try {
            // Uncomment to test:
            // int x = 10 / 0;     // ArithmeticException
            System.out.println(text.length()); // NullPointerException
        }
        catch (ArithmeticException e) {
            System.out.println("Math issue: " + e.getMessage());
        }
        catch (NullPointerException e) {
            System.out.println("Null pointer issue: " + e.getMessage());
        }
        catch (Exception e) {
            System.out.println("Other exception: " + e.getMessage());
        }
    }


    // -----------------------------------------------------------
    // 4. try-catch-finally
    // -----------------------------------------------------------
    static void tryCatchFinallyExample() {
        try {
            int x = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("Handled: " + e.getMessage());
        } finally {
            System.out.println("Finally block ALWAYS runs (cleanup code).");
        }
    }


    // -----------------------------------------------------------
    // 5. Custom Exception: throw + throws
    // -----------------------------------------------------------
    static void customExceptionExample() {
        try {
            registerUser(15);  // age < 18 → throws
        } catch (InvalidAgeException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    static void registerUser(int age) throws InvalidAgeException {
        if (age < 18) {
            throw new InvalidAgeException("Age must be >= 18. Provided: " + age);
        }
        System.out.println("User registered successfully!");
    }


    // -----------------------------------------------------------
    // Custom Exception Class
    // -----------------------------------------------------------
    static class InvalidAgeException extends Exception {
        public InvalidAgeException(String message) {
            super(message);
        }
    }


    // -----------------------------------------------------------
    // 6. throw + throws with built-in exception
    // -----------------------------------------------------------
    static void builtinThrowsExample() {
        try {
            int result = safeDivide(10, 0);
            System.out.println("Result = " + result);
        } catch (ArithmeticException e) {
            System.out.println("Error while dividing: " + e.getMessage());
        }
    }

    static int safeDivide(int a, int b) throws ArithmeticException {
        if (b == 0) {
            throw new ArithmeticException("Denominator cannot be zero");
        }
        return a / b;
    }
}
