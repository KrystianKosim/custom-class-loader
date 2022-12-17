package com.company.service;

import java.util.Scanner;

public class Terminal {
    private static Scanner scanner = new Scanner(System.in);

    /**
     * This method print value of field and area given as parameters
     *
     * @param field
     * @param area
     */
    public static void printResult(double field, double area) {
        System.out.println("Pole wynosi: " + field);
        System.out.printf("Obwod wynosi: " + area);
    }

    /**
     * This method takes a value of base from user, and checks if it is correct value
     *
     * @return
     */
    public static double getBase() {
        System.out.println("Podaj podstawe: ");
        double base = getValueFromConsole();
        if(base <= 0){
            System.out.print("Wartosc musi byc wieksze od 0, podaj poprawna wartosc! ");
            return getBase();
        }
        return base;
    }

    /**
     * This method takes a value of high from user, and checks if it is correct value
     *
     * @return
     */
    public static double getHigh() {
        System.out.println("Podaj wysokosc: ");
        double high = getValueFromConsole();
        if (high <= 0) {
            System.out.print("Wartosc musi byc byc wieksze od 0, podaj poprawna wartosc! ");
            return getHigh();
        }
        return high;
    }

    private static double getValueFromConsole() {
        String scanValue = scanner.nextLine();
        try {
            double base = Double.valueOf(scanValue);
            return base;
        } catch (NumberFormatException e) {
            System.err.print("Bledna wartosc!!! Poprawna to: ");
            return getValueFromConsole();
        }
    }
}