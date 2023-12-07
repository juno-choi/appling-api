package com.juno.appling.con;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class ConService {
    private final AtomicInteger counter = new AtomicInteger(0);
    private int su = 0;

    Map<Integer, Integer> map = new HashMap<>();

    Lock lock = new ReentrantLock();

    ConcurrentHashMap<Integer, Integer> conMap = new ConcurrentHashMap<>();

    public int incrementAndGet() {
        //AtomicInteger 를 사용하여 원자성 보장
//        return counter.incrementAndGet();

        //lock을 사용하여 원자성 보장
        try{
            lock.lock();
            return ++su;
        }finally {
            lock.unlock();
        }

        //ConcurrentHashMap은 여러 쓰레드에서 put, get, remove시에 원자성을 보장해주지만 연산에 대해서는 보장하지 않는다.
    }
}
