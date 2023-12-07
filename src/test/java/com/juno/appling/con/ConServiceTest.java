package com.juno.appling.con;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ConServiceTest {

    @Test
    void 동시성_테스트() throws InterruptedException {
        int numberOfThreads = 10;
        int numberOfIncrementsPerThread = 1000;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        ConService conService = new ConService();

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    for (int j = 0; j < numberOfIncrementsPerThread; j++) {
                        conService.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기
        executorService.shutdown();

        // 기대하는 총 증가 수
        int expectedTotalIncrements = numberOfThreads * numberOfIncrementsPerThread + 1;

        // 실제 증가 수
        int actualTotalIncrements = conService.incrementAndGet();

        // 테스트 결과 검증
        assertEquals(expectedTotalIncrements, actualTotalIncrements);
    }
}