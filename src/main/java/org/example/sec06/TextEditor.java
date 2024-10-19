package org.example.sec06;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;

public class TextEditor extends JFrame implements ActionListener {

    // Text component
    JTextArea textArea;

    // Frame
    JFrame frame;

    // Constructor
    public TextEditor() {
        // Create a frame
        frame = new JFrame("Simple Text Editor");

        // Create a text area
        textArea = new JTextArea();

        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create file menu
        JMenu fileMenu = new JMenu("File");

        // Create menu items
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As");
        JMenuItem exitItem = new JMenuItem("Exit");

        // Add action listeners
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        saveAsItem.addActionListener(this);
        exitItem.addActionListener(this);

        // Set keyboard shortcuts (accelerators)
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));


        // Add menu items to file menu
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
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

    private File currentSelectedFile = null;

    // Handle menu actions
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        // Open file action
        if (command.equals("Open")) {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                currentSelectedFile = file;
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    textArea.read(br, null);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        // Save file action
        else if (command.equals("Save")) {
            File file = currentSelectedFile;
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    textArea.write(bw);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
        } else if (command.equals("Save As")) {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    textArea.write(bw);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        // Exit action
        else if (command.equals("Exit")) {
            System.exit(0);
        }
    }

    // Main method
    public static void main(String[] args) {
        new TextEditor();
    }
}