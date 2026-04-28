package sme.tech.innovators.sme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${spring.async.executor.core-pool-size:2}")
    private int corePoolSize;

    @Value("${spring.async.executor.max-pool-size:5}")
    private int maxPoolSize;

    @Value("${spring.async.executor.queue-capacity:100}")
    private int queueCapacity;

    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("email-async-");
        executor.initialize();
        return executor;
    }
}
