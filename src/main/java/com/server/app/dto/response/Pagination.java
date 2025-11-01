package com.server.app.dto.response;

import java.util.List;

public record Pagination<T>(
                List<T> data,
                int page,
                int size,
                int pages,
                long total) {
}