package com.calculator.spring.model;


import lombok.Data;

@Data
public class Request {
    private double v;
    private double d;
    private int period;
    private int month;
    private int year;
    String withdrawType;

}
