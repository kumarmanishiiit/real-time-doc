package org.example.filewatcher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileChangeObserver {

    private static String previousContent = "";

    static List<String> currentContent =  new ArrayList<>();

    public static void main(String[] args) {
        try {
            // Path to the directory where the file is located
            Path path = Paths.get("/Users/manish.kumar2/Desktop/IIITH/Distributed System/grpc/grpc-playground/cache");


            Path filePath = Paths.get("/Users/manish.kumar2/Desktop/IIITH/Distributed System/grpc/grpc-playground/cache/test.txt");

            // File to monitor
            String fileName = "test.txt"; // replace with the name of the file you want to monitor

            currentContent = Files.readAllLines(filePath);

            previousContent = String.join("\n", currentContent);

            // Start watching the directory for file changes
            WatchService watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            System.out.println("Monitoring changes to " + fileName + " ...");

            while (true) {
                WatchKey key = watchService.take();  // Block until an event occurs

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    // Get the file name from the event
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path changedFile = ev.context();

                    // Check if the changed file is the one we're monitoring
                    if (changedFile.toString().equals(fileName)) {
                        System.out.println("File " + fileName + " has been modified.");

                        // Get the new content and compare it with the previous content
                        String newContent = getFileContent(filePath);
                        getChanges(previousContent, newContent);

                        // Update the previous content
                        previousContent = newContent;
                    }
                }

                // Reset the key to be ready for the next event
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("cache/new_test.txt"));
                for (String line : currentContent) {
                    bufferedWriter.write(line);
                    bufferedWriter.write("\n");
                }
                bufferedWriter.close();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to read the entire content of the file
    private static String getFileContent(Path filePath) {
        try {
            List<String> lines = Files.readAllLines(filePath);
            return String.join("\n", lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Method to compare and print differences between two versions of content
    private static void getChanges(String oldContent, String newContent) {
        System.out.println("----- Changes Detected -----");
        String[] oldLines = oldContent.split("\n");
        String[] newLines = newContent.split("\n");

        int minLength = Math.min(oldLines.length, newLines.length);

        for (int i = 0; i < minLength; i++) {
            if (!oldLines[i].equals(newLines[i])) {
                System.out.println("Line " + (i + 1) + " changed:");
                System.out.println("Old: " + oldLines[i]);
                System.out.println("New: " + newLines[i]);
                if( currentContent.get(i) == null) {
                    currentContent.add(newLines[i]);
                } else {
                    currentContent.set(i, newLines[i]);
                }
            }
        }

        // If new content has extra lines
        if (newLines.length > oldLines.length) {
            System.out.println("New lines added:");
            for (int i = oldLines.length; i < newLines.length; i++) {
                System.out.println("Line " + (i + 1) + ": " + newLines[i]);
                currentContent.add(newLines[i]);
            }
        } else if (oldLines.length > newLines.length) {
            System.out.println("Lines removed:");
            for (int i = newLines.length; i < oldLines.length; i++) {
                System.out.println("Line " + (i + 1) + ": " + oldLines[i]);
                currentContent.set(i, "");
            }
        }
    }
}

