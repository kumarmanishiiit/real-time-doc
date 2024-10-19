package org.example;

import java.io.IOException;
import java.nio.file.*;

public class Changes {

    public static void main(String[] args) throws IOException, InterruptedException {
        // Define the path to the directory you want to watch
        Path path = Paths.get("/Users/manish.kumar2/Desktop/IIITH/Distributed System/grpc/grpc-playground/");

        // Create a WatchService
        WatchService watchService = FileSystems.getDefault().newWatchService();

        // Register the directory with the WatchService for specific events
        path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        System.out.println("Watching directory for changes...");

        // Start watching for events
        while (true) {
            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }
                // Get the file that was modified
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path modifiedFile = ev.context();

                System.out.println("File modified: " + modifiedFile);

                // Optionally, read the modified file content or specific changes
                // Example of how you could reload or process the file.
            }

            // Reset the key and exit loop if the directory is no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }
}
