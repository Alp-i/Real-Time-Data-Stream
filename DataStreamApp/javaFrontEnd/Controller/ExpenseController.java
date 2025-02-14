package com.project.homework2.Controller;

import com.project.homework2.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @GetMapping("/expense/{userId}")
    public String expense(@PathVariable Integer userId, Model model) {
        Dataset<Row> expenseReport = expenseService.getExpenseReportByUserId(userId);

        model.addAttribute("expenseReport", expenseReport.collectAsList());

        return "expenseReport";
    }
}
