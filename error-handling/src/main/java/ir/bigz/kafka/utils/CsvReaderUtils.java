package ir.bigz.kafka.utils;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import ir.bigz.kafka.dto.User;
import ir.bigz.kafka.exception.PublisherException;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CsvReaderUtils {

    public static List<User> readDataFromCsv() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader
                (new ClassPathResource("users.csv").getInputStream()))) {
            CsvToBean<User> csvToBean = new CsvToBeanBuilder<User>(reader)
                    .withType(User.class)
                    .build();

            return csvToBean.parse();
        } catch (IOException e) {
            throw new PublisherException(String.format("Raise an Error during Parse the CSV file: [%s]", e.getMessage()));
        }
    }
}