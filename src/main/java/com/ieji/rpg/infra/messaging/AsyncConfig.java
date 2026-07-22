package com.ieji.rpg.infra.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
/// classe de configurassao dos serviços assíncronos.
/// Define o número mínimo de theads (2)
/// O máximo para demandas simultaneas (5)
/// A capacidade da fila (50)
/// inicializa o serviço
/// É um bean então será reaproveitado para tudo o quee stiver anotado vom @Async
@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.initialize();
        return executor;
    }
}