import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class DirectoryCopy
{
    private static int totalFilesCopied = 0;
    private static int totalDirectoriesCopied = 0;
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to an existing directory: ");
        String sourceDir = scanner.nextLine();
        System.out.print("Enter the path to the new directory: ");
        String targetDir = scanner.nextLine();
        File source = new File(sourceDir);
        File target = new File(targetDir);
        if (!source.exists() || !source.isDirectory())
        {
            System.out.println("The source directory does not exist or is not a directory.");
            return;
        }
        if (!target.exists())
        {
            target.mkdirs();
        }
        Thread copyThread = new Thread(() ->
        {
            try
            {
                copyDirectory(source.toPath(), target.toPath());
                System.out.println("Copying finished.");
            }
            catch (IOException e)
            {
                System.err.println("Error copying: " + e.getMessage());
            }
        });
        copyThread.start();
        try
        {
            copyThread.join();
        }
        catch (InterruptedException e)
        {
            System.err.println("Error: " + e.getMessage());
        }
        System.out.println("Copying stats: ");
        System.out.println("Files copied: " + totalFilesCopied);
        System.out.println("Copied directories: " + totalDirectoriesCopied);
    }
    private static void copyDirectory(Path source, Path target) throws IOException
    {
        Files.walk(source).forEach(path ->
        {
            try
            {
                Path targetPath = target.resolve(source.relativize(path));
                if (Files.isDirectory(path))
                {
                    Files.createDirectories(targetPath);
                    totalDirectoriesCopied++;
                }
                else
                {
                    Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    totalFilesCopied++;
                }
            }
            catch (IOException e)
            {
                System.err.println("Error copying: " + e.getMessage());
            }
        });
    }
}
