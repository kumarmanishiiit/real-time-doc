package org.example.filewatcher;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

import static java.nio.file.Path.*;

public class FileWatcher {

     private static final String directoryPath = "/Users/manish.kumar2/Desktop/IIITH/Distributed System/grpc/grpc-java/cache";
    public static void main(String[] args) {
        System.out.println("Watch Service Started!!!");
        // 1. Directory - for fiels
        // 2. File watcher service
        // 3. Events
        // 4. Watch Key
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

//            Path path = Paths.get(Objects.requireNonNull(FileWatcher.class.getClassLoader().getResource(directoryPath)).getPath());
            Path path = Paths.get(directoryPath);

            WatchKey watchKey = path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);

            while (true) {
                for(WatchEvent<?> event: watchKey.pollEvents()) {
                    System.out.println("Event Type: "+event.kind());
                    System.out.println("Event Context: "+event.context());
//                    Path file = Path.resolve(String.valueOf((Path)event.context()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
