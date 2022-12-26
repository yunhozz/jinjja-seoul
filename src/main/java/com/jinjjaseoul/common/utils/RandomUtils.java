package com.jinjjaseoul.common.utils;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomUtils {

    private static final int counterMax = 256 * 256;
    private static final AtomicInteger intVal = new AtomicInteger(0);
    private static final Random random = new Random(System.currentTimeMillis());

    public String generatePk() {
        final long id = Instant.now().toEpochMilli() * counterMax + intVal.accumulateAndGet(1, (index, inc) -> (index + inc) % counterMax);
        return Long.toHexString(id);
    }

    public Long generateId(int bound) {
        return (long) random.nextInt(bound) + 1;
    }

    public String generateStr(int length) {
        char[] chars = new char[length];

        for (int i = 0; i < chars.length; i++) {
            int next = random.nextInt(3);
            switch (next) {
                case 0:
                    int number = random.nextInt(10) + 48; // 48 ~ 57
                    chars[i] = (char) number;
                case 1:
                    int upper = random.nextInt(26) + 65; // 65 ~ 90
                    chars[i] = (char) upper;
                case 2:
                    int lower = random.nextInt(26) + 97; // 97 ~ 122
                    chars[i] = (char) lower;
            }
        }

        return new String(chars);
    }
}