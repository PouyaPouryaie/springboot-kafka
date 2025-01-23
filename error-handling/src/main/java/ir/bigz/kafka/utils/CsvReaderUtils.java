package ir.bigz.kafka.utils;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import ir.bigz.kafka.dto.User;
import ir.bigz.kafka.exception.PublisherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for reading data from CSV files.
 */
public class CsvReaderUtils {

    private static final Logger log = LoggerFactory.getLogger(CsvReaderUtils.class);

    // Private constructor to prevent instantiation
    private CsvReaderUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Reads data from a CSV file and parses it into a list of User objects.
     *
     * @return a list of User objects parsed from the CSV
     */
    public static List<User> readDataFromCsv() {

        String csvFilePath = "users.csv";
        log.info("Attempting to read data from CSV file: {}", csvFilePath);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader
                (new ClassPathResource(csvFilePath).getInputStream()))) {

            CsvToBean<User> csvToBean = new CsvToBeanBuilder<User>(reader)
                    .withType(User.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<User> users = csvToBean.parse();
            if (Objects.requireNonNull(users).isEmpty()) {
                log.warn("The CSV file [{}] was read successfully but contains no data", csvFilePath);
                throw new PublisherException("The CSV file is empty");
            }

            log.info("Successfully read and parsed [{}] records from the CSV file", users.size());
            return users;

        } catch (IOException e) {
            String errorMessage = String.format("Error occurred while reading the CSV file [%s]: %s", csvFilePath, e.getMessage());
            log.error(errorMessage, e);
            throw new PublisherException(errorMessage, e);
        }
    }
}