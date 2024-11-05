package ir.bigz.kafka.dto;


//public record Customer (int id, String name, String email) {
//}

import lombok.Data;

@Data
public class Customer {
    int id; String name; String email;

    public Customer(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Customer() {
    }
}
