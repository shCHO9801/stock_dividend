package com.dayone.web;

import com.dayone.model.Company;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private CompanyService companyService;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        return null;
    }
    /*
    * 전체 회사 조회
    * */
    @GetMapping
    public ResponseEntity<?> searchCompany(Pageable pageable) {
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }

    /*
     * 회사 및 배당금 정보 추가
     * @Param request
     * @return
     * */
    @PostMapping
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("Ticker is empty");
        }

        Company company = this.companyService.save(ticker);

        return ResponseEntity.ok(company);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
