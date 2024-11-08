package com.dayone.web;

import com.dayone.model.Company;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.service.CompanyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
@Slf4j
public class CompanyController {

    private CompanyService companyService;
    private CacheManager redisCacheManager;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        var result = this.companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(result);
    }
    /*
    * 전체 회사 조회
    */

    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(Pageable pageable) {
        log.info("CompanyController: Search Company started");
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        log.info("CompanyController: Search Company Complete");
        return ResponseEntity.ok(companies);
    }

    /*
     * 회사 및 배당금 정보 추가
     * @Param request
     * @return
     */

    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        log.info("CompanyController: Add Company started with -> {}", request.getTicker().trim());
        String ticker = request.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker)) {
            log.error("CompanyController: Add Company failed. -> Ticker is empty");
            throw new RuntimeException("Ticker is empty");
        }

        Company company = this.companyService.save(ticker);
        this.companyService.addAutoCompleteKeyWord(company.getName());
        log.info("CompanyController: Add Company completed");
        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        log.info("CompanyController: Delete Company started with -> {}", ticker);
        String companyName = this.companyService.deleteCompany(ticker);
        // 캐시에서도 삭제
        this.clearFinanceCache(companyName);
        log.info("CompanyController: Delete Company completed");
        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName) {
        this.redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
    }
}
