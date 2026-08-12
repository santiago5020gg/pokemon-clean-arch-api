package com.pokedex.core.dto;

import java.util.List;

/**
 * Framework-neutral pagination envelope so the core never leaks Spring Data's {@code Page}.
 */
public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public <R> PageResult<R> map(java.util.function.Function<? super T, ? extends R> mapper) {
        List<R> mapped = content.stream().<R>map(mapper).toList();
        return new PageResult<>(mapped, page, size, totalElements, totalPages);
    }
}
