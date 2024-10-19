package org.example.sec06;

import com.iiith.assignment.model.sec06.ChatMessage;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimpleTextEditorWithFileWatcher extends JFrame implements ActionListener {

    // Text component
    JTextArea textArea;

    // Frame
    JFrame frame;

    // Current file being edited
    File currentFile;

    // WatchService for file changes
    WatchService watchService;
    ExecutorService executorService = Executors.newFixedThreadPool(5);

    // Constructor
    public SimpleTextEditorWithFileWatcher() {
        // Create a frame
        frame = new JFrame("Simple Text Editor with File Watcher");

        // Create a text area
        textArea = new JTextArea();

        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create file menu
        JMenu fileMenu = new JMenu("File");

        // Create menu items
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");

        // Add action listeners
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(this);

        // Set keyboard shortcuts (accelerators)
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        // Add menu items to file menu
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        // Add file menu to menu bar
        menuBar.add(fileMenu);

        // Add menu bar to frame
        frame.setJMenuBar(menuBar);

        // Add text area to frame
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Set the frame size and make it visible
        frame.setSize(500, 500);
        frame.setVisible(true);

        // Close the frame when the user closes it
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Handle menu actions
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        // Open file action
        if (command.equals("Open")) {
            JFileChooser fileChooser = new JFileChooser("cache/");
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
                loadFile(currentFile);

                // Start watching for file changes
                startFileWatcher(currentFile.toPath());
                try {
                    establishServerConnection(currentFile.toPath());
                } catch (InterruptedException ex) {
//                    throw new RuntimeException(ex);
                }
            }
        }
        // Save file action
        else if (command.equals("Save")) {
            if (currentFile != null) {
                saveFile(currentFile);
            } else {
                JFileChooser fileChooser = new JFileChooser();
                int option = fileChooser.showSaveDialog(this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    currentFile = fileChooser.getSelectedFile();
                    saveFile(currentFile);
                }
            }
        }
        // Exit action
        else if (command.equals("Exit")) {
            stopFileWatcher();  // Stop the file watcher before exiting
            System.exit(0);
        }
    }

    private void establishServerConnection(Path path) throws InterruptedException {
            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                    .usePlaintext()
                    .build();

            com.iiith.assignment.model.sec06.BidirectionalServiceGrpc.BidirectionalServiceStub asyncStub = com.iiith.assignment.model.sec06.BidirectionalServiceGrpc.newStub(channel);

            CountDownLatch latch = new CountDownLatch(1);

            // StreamObserver for receiving responses from the server
            StreamObserver<ChatMessage> responseObserver = new StreamObserver<ChatMessage>() {

                @Override
                public void onNext(ChatMessage value) {
                    System.out.println("Received from server: " + value.getUser() + ": " + value.getMessage());
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                    latch.countDown();
                }

                @Override
                public void onCompleted() {
                    System.out.println("Server has completed sending messages.");
                    latch.countDown();
                }
            };

        // Run the watcher in a separate thread
        executorService.submit(() -> {
            try {
                // StreamObserver for sending messages to the server
                StreamObserver<ChatMessage> requestObserver = asyncStub.chat(responseObserver);

                // Send multiple messages
//            for (int i = 0; i < 5; i++) {
                ChatMessage message = ChatMessage.newBuilder()
                        .setUser("Client 1")
                        .setMessage("Started working on"+ path.getFileName())
                        .build();

                requestObserver.onNext(message);

                // Simulate a delay
                Thread.sleep(1000);
//            }

                // Mark the end of requests
                requestObserver.onCompleted();

                // Wait for the server to finish
                latch.await(3, TimeUnit.SECONDS);

                channel.shutdown();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });

    }

    // Load the file into the text area
    private void loadFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            textArea.read(br, null);
            frame.setTitle("Simple Text Editor - " + file.getName());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // Save the file from the text area
    private void saveFile(File file) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            textArea.write(bw);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // Start the file watcher to monitor file changes
    private void startFileWatcher(Path filePath) {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            filePath.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            // Run the watcher in a separate thread
            executorService.submit(() -> {
                try {
                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();

                            // Check if the modified file is the one we're editing
                            if (kind == StandardWatchEventKinds.ENTRY_MODIFY && event.context().toString().equals(filePath.getFileName().toString())) {
                                // Reload the file in the text area
                                SwingUtilities.invokeLater(() -> loadFile(currentFile));
                            }
                        }
                        key.reset();
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Stop the file watcher
    private void stopFileWatcher() {
        try {
            if (watchService != null) {
                watchService.close();
            }
            executorService.shutdownNow();  // Stop the watcher thread
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Main method
    public static void main(String[] args) {
        new SimpleTextEditorWithFileWatcher();
    }
}

