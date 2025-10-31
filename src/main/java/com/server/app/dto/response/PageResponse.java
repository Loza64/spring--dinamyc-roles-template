package com.server.app.dto.response;

import java.util.List;

public record PageResponse<T>(
                List<T> content,
                int pageNumber,
                int pageSize,
                int totalPages,
                long total) {
}