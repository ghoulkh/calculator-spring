package com.calculator.spring.model;

import lombok.Data;

@Data
public class CalculateIntRequest {
    private double v;
    private double d;
    private int month;
    private int year;
}
