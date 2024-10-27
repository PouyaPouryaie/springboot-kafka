package ir.bigz.kafka.dto;

import lombok.Data;

//@Data
//public class Customer {
//    int id;
//    String name;
//    String email;
//}

public record Customer (int id, String name, String email) {
}
