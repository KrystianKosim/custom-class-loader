package com.company.service;

import java.math.BigDecimal;

public class Service {

    /**
     * This method calculate a field of triangle based on given params, return field of triangle
     *
     * @param base
     * @param high
     * @return field of triangle
     */
    public static double getFieldOfTriangle(double base, double high) throws IllegalArgumentException {
        if(base <= 0 || high <= 0){
            throw new IllegalArgumentException("Wartosci musza byc wieksze od 0!");
        }
        BigDecimal bigDecimalBase = BigDecimal.valueOf(base);
        BigDecimal bigDecimalHigh = BigDecimal.valueOf(high);
        BigDecimal multiplyBaseAndHigh = bigDecimalHigh.multiply(bigDecimalBase);
        double divider = 2;
        BigDecimal bigDecimalField = multiplyBaseAndHigh.divide(BigDecimal.valueOf(divider));
        double fieldToReturn = bigDecimalField.doubleValue();
        return fieldToReturn;
    }

    /**
     * This method calculate an area of triangle based on given params, return an area of triangle
     *
     * @param base
     * @return area of triangle
     */
    public static double getAreaOfTriangle(double base) {
        if(base <= 0){
            throw new IllegalArgumentException("Wartosci musza byc wieksze od 0!");
        }
        BigDecimal bigDecimalBase = BigDecimal.valueOf(base);
        double numberOfSides = 3;
        bigDecimalBase = bigDecimalBase.multiply(BigDecimal.valueOf(numberOfSides));
        double valueToReturn = bigDecimalBase.doubleValue();
        return valueToReturn;
    }
}