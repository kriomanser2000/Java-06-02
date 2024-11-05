import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class FileSearchAndFilter
{
    private static final List<String> forbiddenWords = new ArrayList<>();
    private static int filesFound = 0;
    private static int linesMerged = 0;
    private static int wordsRemoved = 0;
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter path to directory: ");
        String directoryPath = scanner.nextLine();
        System.out.print("Enter word for search: ");
        String searchWord = scanner.nextLine();
        loadForbiddenWords("forbidden_words.txt");
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() ->
        {
            try
            {
                mergeFilesContainingWord(directoryPath, searchWord);
            }
            finally
            {
                latch.countDown();
            }
        }).start();
        new Thread(() ->
        {
            try
            {
                latch.await();
                filterForbiddenWords("merged.txt");
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    private static void loadForbiddenWords(String fileName)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                forbiddenWords.add(line.trim());
            }
        }
        catch (IOException e)
        {
            System.err.println("Cant read file: " + e.getMessage());
        }
    }
    private static void mergeFilesContainingWord(String directoryPath, String searchWord)
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter("merged.txt")))
        {
            Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .forEach(path ->
                    {
                        try
                        {
                            if (Files.readString(path).contains(searchWord))
                            {
                                filesFound++;
                                List<String> lines = Files.readAllLines(path);
                                for (String line : lines)
                                {
                                    writer.println(line);
                                    linesMerged++;
                                }
                            }
                        }
                        catch (IOException e)
                        {
                            System.err.println("Error: " + path + " - " + e.getMessage());
                        }
                    });
        }
        catch (IOException e)
        {
            System.err.println("Error: " + e.getMessage());
        }
    }
    private static void filterForbiddenWords(String inputFile)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter("filtered.txt")))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                for (String forbiddenWord : forbiddenWords)
                {
                    if (line.contains(forbiddenWord))
                    {
                        wordsRemoved++;
                        line = line.replace(forbiddenWord, "");
                    }
                }
                writer.println(line);
            }
        }
        catch (IOException e)
        {
            System.err.println("File could not processed to remove banned words: " + e.getMessage());
        }
        printStatistics();
    }
    private static void printStatistics()
    {
        System.out.println("Statistics of performed operations: ");
        System.out.println("No files found: " + filesFound);
        System.out.println("Merged lines: " + linesMerged);
        System.out.println("Deleted words: " + wordsRemoved);
    }
}
