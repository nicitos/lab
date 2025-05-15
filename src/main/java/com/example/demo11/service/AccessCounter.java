package com.example.demo11.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AccessCounter {
    private final AtomicLong count = new AtomicLong(0);

    public void increment() {
        count.incrementAndGet();
    }

    public long getCount() {
        return count.get();
    }

    public void reset() {
        count.set(0);
    }
}