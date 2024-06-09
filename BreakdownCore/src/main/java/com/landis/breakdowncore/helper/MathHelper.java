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

    //index0 -> x100value; index1 -> power
    public static int[] doubleToInt(double d){
        int value;
        int power = 0;
        if(Math.abs(d) < 1000){
            value = (int) (d * 100);
        }else if(Math.abs(d) < 10000){
            value = (int) (d * 10) * 10;
        }else{
            String str = String.format("%.7e",d);
            power = Integer.parseInt(str.split("e+")[1]) / 3 * 3;
            value = (int)(d / Math.pow(10,power - 2));
        }
        return new int[]{value,power};
    }
}
