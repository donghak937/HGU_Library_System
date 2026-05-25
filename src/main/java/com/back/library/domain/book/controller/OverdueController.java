package com.back.library.domain.book.controller;

import com.back.library.domain.book.dto.overdue.response.OverdueRecordResponse;
import com.back.library.domain.book.service.OverdueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 연체 관련 API Controller (Iteration 3).
 *
 *  GET  /overdue/OverdueManagementUI  → 연체 관리 UI 페이지
 *  GET  /overdue/list                 → viewOverdueList
 *  GET  /overdue/details/{overdueId}  → viewOverdueDetails
 *  POST /overdue/penalty/apply        → applyLatePenalty (사서가 days 입력)
 */
@Controller
@RequestMapping("/overdue")
@RequiredArgsConstructor
public class OverdueController {

    private final OverdueService overdueService;

    @GetMapping("/OverdueManagementUI")
    public String showOverdueManagementUI() {
        return "loan/OverdueManagementUI";
    }

    // viewOverdueList
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<OverdueRecordResponse>> viewOverdueList(
            @RequestParam(required = false) String userId) {
        return ResponseEntity.ok(overdueService.viewOverdueList(userId));
    }

    // viewOverdueDetails
    @GetMapping("/details/{overdueId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewOverdueDetails(
            @PathVariable String overdueId) {
        try {
            OverdueRecordResponse record = overdueService.viewOverdueDetails(overdueId);
            return ResponseEntity.ok(Map.of("success", true, "record", record));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // applyLatePenalty — 사서가 원하는 일수만큼 정지 기한 연장
    @PostMapping("/penalty/apply")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> applyLatePenalty(
            @RequestParam String overdueId,
            @RequestParam(defaultValue = "1") int days) {
        try {
            OverdueRecordResponse record = overdueService.applyLatePenalty(overdueId, days);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", days + "일 정지 기한이 연장되었습니다.",
                    "record",  record
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    // cancelSuspension — 정지 해지
    @PostMapping("/suspension/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelSuspension(
            @RequestParam String overdueId) {
        try {
            OverdueRecordResponse record = overdueService.cancelSuspension(overdueId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "정지가 해지되었습니다.",
                    "record",  record
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}