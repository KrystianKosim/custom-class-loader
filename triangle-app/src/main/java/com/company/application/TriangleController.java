package com.company.application;


import com.company.application.runner.Controller;
import com.company.service.Service;
import com.company.service.Terminal;

@Controller
public class TriangleController {

    /**
     * A method that manages the call of appropriate methods and showing the result
     */
    public void run() {
        double high = Terminal.getHigh();
        double base = Terminal.getBase();
        double field = Service.getFieldOfTriangle(base, high);
        double area = Service.getAreaOfTriangle(base);
        Terminal.printResult(field, area);
    }
}
