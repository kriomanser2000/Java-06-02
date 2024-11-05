import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Threads2
{
    private static final int NUMBERS_COUNT = 100;
    private static List<Integer> numbers = new ArrayList<>();
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter path to file: ");
        String filePath = scanner.nextLine();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(1);
        executorService.submit(() ->
        {
            generateRandomNumbers(filePath);
            latch.countDown();
        });
        executorService.submit(() ->
        {
            try
            {
                latch.await();
                List<Integer> primes = findPrimes(filePath);
                writeToFile("primes.txt", primes);
                System.out.println("Prime numbers are written in primes.txt");
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        });
        executorService.submit(() ->
        {
            try
            {
                latch.await();
                List<Long> factorials = calculateFactorials(filePath);
                writeToFile("factorials.txt", factorials);
                System.out.println("Factors are written in factorials.txt");
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        });
        executorService.shutdown();
    }
    private static void generateRandomNumbers(String filePath)
    {
        Random random = new Random();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath)))
        {
            for (int i = 0; i < NUMBERS_COUNT; i++)
            {
                int number = random.nextInt(100);
                numbers.add(number);
                writer.write(String.valueOf(number));
                writer.newLine();
            }
            System.out.println("File filled with random nums.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private static List<Integer> findPrimes(String filePath)
    {
        List<Integer> primes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                int number = Integer.parseInt(line);
                if (isPrime(number))
                {
                    primes.add(number);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return primes;
    }
    private static List<Long> calculateFactorials(String filePath)
    {
        List<Long> factorials = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                int number = Integer.parseInt(line);
                factorials.add(factorial(number));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return factorials;
    }
    private static void writeToFile(String fileName, List<?> results)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)))
        {
            for (Object result : results)
            {
                writer.write(String.valueOf(result));
                writer.newLine();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private static boolean isPrime(int number)
    {
        if (number < 2) return false;
        for (int i = 2; i <= Math.sqrt(number); i++)
        {
            if (number % i == 0) return false;
        }
        return true;
    }
    private static long factorial(int number)
    {
        long result = 1;
        for (int i = 2; i <= number; i++)
        {
            result *= i;
        }
        return result;
    }
}
