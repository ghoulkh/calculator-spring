package com.calculator.spring.util;

import com.calculator.spring.model.CalculateIntRequest;
import com.calculator.spring.model.Request;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class Calculator {

    private static final int VNDAF_i = 70;
    private static final int VNDBF_i = 30;
    private static final double R_annual_i = 10.8;
    private static final int m_i = 12;
    private static final double tax = 0.1;
    private static final double o = 2000.0; // số tiền rút định kỳ
    private static final double R_ref = 6.75/100.0;


    public static double PBT(Request request) {
        double previousPBT = 0;
        for (int i = 1; i <= request.getPeriod(); i ++) {
            previousPBT = PBT_i(request.getV(), request.getD(), i, previousPBT, request.getMonth(), request.getYear(), request.getWithdrawType());
        }
        return previousPBT;
    }
    private static double PBT_i(double v, double d, int period, double previousPBT_i, int month, int year, String withdrawType) {
        return PB0_i(period, previousPBT_i, v) + DB_i(d, month, year) + IntB_i(period, withdrawType, month, year, previousPBT_i, v, d) -
                OB_i(withdrawType, month, year, period, v, previousPBT_i);
    }


    private static double R_i() {
        double result = (Math.pow(1.0 + R_annual_i/100.0, 1.0/m_i) - 1.0) * m_i;
        return result;
    }
    private static double OB_i(String withdrawType, int month, int year, int period, double v, double previousPBT_) {
        if (!positiveKHCL(month, year) && "All".equals(withdrawType)) {
            return PB0_i(period, previousPBT_, v);
        } else if (!positiveKHCL(month, year) && "Monthly".equals(withdrawType)) {
            return Math.min(o, PB0_i(period, previousPBT_, v));
        } else {
            return 0;
        }
    }

    private static double DB_i(double d, int month, int year) {
        return positiveKHCL(month, year) ? d : 0;
    }

    private static double PB0_i(int period, double previousPBT_, double v) {
        return period == 1 ? v : previousPBT_;
    }

    private static double IntB_i(int period, String withdrawType, int month, int year, double previousPBT_, double v, double d) {
        double result = (PB0_i(period, previousPBT_, v) + DB_i(d, month, year) - OB_i(withdrawType, month, year, period, v, previousPBT_))
                * (RB_i(period)/m_i);
        return result;
    }

    private static double O_i(int period, String withdrawType, int month, int year, double previousPT_, double v) {
        if (!positiveKHCL(month, year) && "All".equals(withdrawType)) {
            return P0_i(period, previousPT_, v);
        } else if (!positiveKHCL(month, year) && "Monthly".equals(withdrawType)) {
            return Math.min(o/(1.0 - tax), P0_i(period, previousPT_, v));
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
//        System.out.println(PT_i(1, 0, 100, 5, 10, 2024, "All"));

        CalculateIntRequest calculateIntRequest = new CalculateIntRequest();
        calculateIntRequest.setV(1000);
        calculateIntRequest.setMonth(8);
        calculateIntRequest.setYear(2033);
        double result = 0.001;
        double expect = 2000000;
        for (int i = 1; true ; i ++) {
            result = result + 0.001;
            calculateIntRequest.setD(result);
            double toEqual = lastInt(calculateIntRequest);
            if (Math.abs(toEqual - expect) < 0.1) {
                System.out.println(result);
                break;
            }
        }

//        System.out.println(KHCL(3, 2024));
    }

    public static List<Double> Int(CalculateIntRequest request) {
        List<Double> result = new LinkedList<>();
        int numberOfMonth = KHCL(request.getMonth(), request.getYear());
        double previousPT_ = 0.0;
        for (int i = 1; i <= numberOfMonth; i ++) {
            previousPT_ = PT_i(i, previousPT_, request.getV(), request.getD(), request.getMonth(), request.getYear(), "All");
            result.add(previousPT_);
        }
        return result;
    }

    public static Double lastInt(CalculateIntRequest request) {
        int numberOfMonth = KHCL(request.getMonth(), request.getYear());
        double previousPT_ = 0.0;
        for (int i = 1; i <= numberOfMonth; i ++) {
            previousPT_ = PT_i(i, previousPT_, request.getV(), request.getD(), request.getMonth(), request.getYear(), "All");
        }
        return previousPT_;
    }
    private static double Int_i(int period, double v, double d, String withdrawType, double previousPT_, int month, int year) {
        double result =  (P0_i(period, previousPT_, v) - O_i(period, withdrawType, month, year, previousPT_, v)) *
                (R_i()/m_i) +
                D_i(d, month, year) * Math.pow((1.0 + R_i()/m_i), 1.0 - (0.5 * m_i)/12.0) - D_i(d, month, year);
        return result;
    }

    private static double P0_i(int period, double previousPT_, double v) {
        return period == 1 ? v : previousPT_;
    }

    private static double PT_i(int period, double previousPT_, double v, double d, int month, int year, String withdrawType) {

        return P0_i(period, previousPT_, v) + D_i(d, month, year) + Int_i(period, v, d, withdrawType, previousPT_, month, year) +
                O_i(period, withdrawType,month, year, previousPT_, v);
    }




    private static double RB_i(int period) {
        double result = m_i * (Math.pow(1.0 + R_ref, 1.0/m_i) - 1.0);
        return result;
    }

    private static double D_i(double d, int month, int year) {
        return positiveKHCL(month, year) ? d : 0;
    }

    private static int KHCL(int month, int year) {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonth().getValue();
        int currentYear = currentDate.getYear();
        int result = (year - currentYear) * 12 + (month - currentMonth);
        return result > 0 ? result : 0;
    }


    private static boolean positiveKHCL(int month, int year) {
        return KHCL(month, year) > 0;
    }
}
