package com.landis.breakdowncore.helper;

import java.util.List;

public class MathHelper {

    public static int lcm(List<Integer> numbers) {
        if (numbers.isEmpty()) {
            throw new IllegalArgumentException("At least one number is required.");
        }
        int lcm = Math.abs(numbers.get(0));
        for (int i = 1; i < numbers.size(); i++) {
            int currentNumber = Math.abs(numbers.get(i));
            lcm = lcm(lcm, currentNumber);
        }
        return lcm;
    }


    public static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    public static int lcm(int a, int b) {
        return (a / gcd(a, b)) * b;
    }
}
