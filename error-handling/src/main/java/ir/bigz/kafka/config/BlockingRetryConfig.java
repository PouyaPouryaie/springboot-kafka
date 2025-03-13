package ir.bigz.kafka.config;

import ir.bigz.kafka.exception.ConsumerException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.backoff.FixedBackOff;

import java.io.IOException;

@Configuration
public class BlockingRetryConfig extends RetryTopicConfigurationSupport {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10); // Adjust pool size as needed
        scheduler.setThreadNamePrefix("retry-scheduler-");
        return scheduler;
    }

    @Override
    protected void configureBlockingRetries(BlockingRetriesConfigurer blockingRetries) {
        blockingRetries
                .retryOn(ConsumerException.class, IOException.class)
                .backOff(new FixedBackOff(1_000, 3));
    }
}
