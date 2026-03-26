package com.medis.hospital_system.adapters.in.web.dto.response;

public record PageResponse<T>(
        java.util.List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
