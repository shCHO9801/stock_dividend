package com.dayone.web;

import com.dayone.service.FinanceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")
@AllArgsConstructor
@Slf4j
public class FinanceController {

    private FinanceService financeService;

    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
        log.info("FinanceController : Search Finance started for company {}", companyName);
        var result = this.financeService.getDividendByCompanyName(companyName);
        log.info("FinanceController : Search Finance finished for company {}", companyName);
        return ResponseEntity.ok(result);
    }
}
