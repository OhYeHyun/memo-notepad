package com.notepad.memo.controller;

import com.notepad.dto.response.memo.MemoClientResponse;
import com.notepad.dto.request.memo.MemoSearchRequest;
import com.notepad.dto.request.memo.MemoCreateRequest;
import com.notepad.auth.dto.JwtPrincipal;
import com.notepad.memo.service.MemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @GetMapping
    public ResponseEntity<List<MemoClientResponse>> listMemos(
            @AuthenticationPrincipal JwtPrincipal user,
            @ModelAttribute MemoSearchRequest searchRequest
    ) {
        return ResponseEntity.ok(memoService.getMemoList(user.getId(), searchRequest));
    }

    @PostMapping
    public ResponseEntity<MemoClientResponse> createMemo(
            @AuthenticationPrincipal JwtPrincipal user,
            @Valid @RequestBody MemoCreateRequest createRequest
    ) {
        return ResponseEntity.ok(memoService.saveMemo(user.getId(), createRequest));
    }

    @PatchMapping("/{memoId}")
    public ResponseEntity<MemoClientResponse> updateMemo(
            @AuthenticationPrincipal JwtPrincipal user,
            @PathVariable Long memoId,
            @Valid @RequestBody MemoCreateRequest createRequest
    ) {
        return ResponseEntity.ok(memoService.updateMemo(user.getId(), memoId, createRequest));
    }

    @DeleteMapping("/{memoId}")
    public ResponseEntity<Void> deleteMemo(
            @PathVariable("memoId") Long memoId
    ) {
        memoService.deleteMemo(memoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{memoId}/check")
    public ResponseEntity<MemoClientResponse> toggleCheck(
            @AuthenticationPrincipal JwtPrincipal user,
            @PathVariable("memoId") Long memoId
    ) {
        return ResponseEntity.ok(memoService.toggleMemo(user.getId(), memoId));
    }
}
