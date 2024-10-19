package org.example.sec01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleProtoDemo {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleProtoDemo.class);

    public static void main(String[] args) {
        var person = com.iiith.assignment.model.PersonOuterClass.Person.newBuilder().setName("Manish").setAge(34).build();

        System.out.println(person);
    }

}
