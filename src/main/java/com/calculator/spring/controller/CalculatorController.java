package com.calculator.spring.controller;

import com.calculator.spring.model.CalculateIntRequest;
import com.calculator.spring.model.Request;
import com.calculator.spring.util.Calculator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api")
public class CalculatorController {

    @PostMapping("/calculate")
    public ResponseEntity<Double> calculate(@RequestBody Request request) {
        return ResponseEntity.ok(Calculator.PBT(request));
    }

    @PostMapping("/calculate/int")
    public ResponseEntity<List<Double>> calculate(@RequestBody CalculateIntRequest request) {
        return ResponseEntity.ok(Calculator.Int(request));
    }
}
