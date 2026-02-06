package com.intelligent.customer.api;

import com.intelligent.customer.api.dto.FaqImportRequest;
import com.intelligent.customer.faq.FaqIngestService;
import com.intelligent.customer.faq.FaqSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * FAQ 管理接口，用于导入与检索。
 */
@RestController
@RequestMapping("/api/faq")
public class FaqController {
    private final FaqIngestService ingestService;
    private final FaqSearchService searchService;

    public FaqController(FaqIngestService ingestService, FaqSearchService searchService) {
        this.ingestService = ingestService;
        this.searchService = searchService;
    }

    /**
     * 导入 FAQ 内容，返回写入条数。
     */
    @PostMapping("/import")
    public Map<String, Object> importFaq(@RequestBody FaqImportRequest request) {
        int count = ingestService.ingest(request.sourceType(), request.content());
        return Map.of("count", count);
    }

    /**
     * 按问题检索 FAQ 片段。
     */
    @GetMapping("/search")
    public List<String> search(@RequestParam("query") String query,
                               @RequestParam(value = "topK", defaultValue = "3") int topK) {
        return searchService.search(query, topK);
    }
}
