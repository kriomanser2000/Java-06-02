import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Threads1
{
    private static final int ARRAY_SIZE = 10;
    private static int[] array = new int[ARRAY_SIZE];
    private static int sum;
    private static double average;
    public static void main(String[] args) throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch(1);
        Thread fillArrayThread = new Thread(() ->
        {
            Random random = new Random();
            for (int i = 0; i < ARRAY_SIZE; i++)
            {
                array[i] = random.nextInt(100);
            }
            System.out.println("Array filled: " + Arrays.toString(array));
            latch.countDown();
        });
        Thread sumThread = new Thread(() ->
        {
            try
            {
                latch.await();
                sum = Arrays.stream(array).sum();
                System.out.println("Sum massive elements: " + sum);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        });
        Thread averageThread = new Thread(() ->
        {
            try
            {
                latch.await();
                average = Arrays.stream(array).average().orElse(0);
                System.out.println("Arithmetic average value: " + average);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        });
        fillArrayThread.start();
        sumThread.start();
        averageThread.start();
        fillArrayThread.join();
        sumThread.join();
        averageThread.join();
        System.out.println("The final array: " + Arrays.toString(array));
        System.out.println("Elements sum: " + sum);
        System.out.println("Arithmetic average: " + average);
    }
}
