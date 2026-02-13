package com.nequi.purchaseTickets.controller;

import com.nequi.purchaseTickets.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/finalize")
    public Mono<ResponseEntity<Map<String, String>>> finalizePurchase(@RequestBody FinalizePurchaseRequest request) {
        return purchaseService.initiatePurchaseFinalization(request.orderId(), request.status())
                .thenReturn(ResponseEntity.accepted().body(Map.of("message", "Purchase finalization in process", "orderId", request.orderId())));
    }

    public record FinalizePurchaseRequest(String orderId, String status) {}
}