package org.example;

import com.iiith.assignment.model.PersonOuterClass;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("Hello world!");

        // This BufferedWriter can write to many different things.
        // In this example I am using BufferedWriter to write to file.
//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("output.txt"));
//        bufferedWriter.write("Writing to file");
//        bufferedWriter.close();

        // it has 8192 bytes of buffer
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("output.txt"));

//        String line;
//        bufferedInputStream.readAllBytes()
//        while()
    }
}