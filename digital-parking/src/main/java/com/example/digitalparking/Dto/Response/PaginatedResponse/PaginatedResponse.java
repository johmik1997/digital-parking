package com.example.digitalparking.Dto.Response.PaginatedResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;  // currentPage is redundant since pageNumber serves the same purpose
    private int pageSize;

    // Primary constructor using Page
    public PaginatedResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.pageNumber = page.getNumber();
    }

    public PaginatedResponse() {
    }

//    // Alternative constructor for manual creation
//    public PaginatedResponse(List<T> content, long totalElements, int totalPages, int pageSize, int pageNumber) {
//        this.content = content;
//        this.totalElements = totalElements;
//        this.totalPages = totalPages;
//        this.pageSize = pageSize;
//        this.pageNumber = pageNumber;
//    }

    // Static factory method
    public static <T> PaginatedResponse<T> fromPage(Page<T> page) {
        return new PaginatedResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber()
        );
    }
}

//@Getter
//@Setter
//@AllArgsConstructor
//public class PaginatedResponse<T> {
//    private List<T> content;
//    private long totalElements;
//    private int totalPages;
//    private int currentPage;
//    private int pageSize;
//    private int pageNumber;
//
//    public PaginatedResponse(Page<T> page) {
//        this.content = page.getContent();
////        this.pageNumber = page.getNumber();
//        this.pageSize = page.getSize();
//        this.totalElements = page.getTotalElements();
//        this.totalPages = page.getTotalPages();
//        this.pageNumber=page.getNumber();
////        this.numberOfElements = page.getNumberOfElements();
//    }
//
//    public PaginatedResponse() {
//
//    }
//
//    public PaginatedResponse(List<RegularBatchResponse> paginatedResponses, long totalElements, int totalPages, int size, int number) {
//    }
//
//    public static <T> PaginatedResponse<T> fromPage(Page<T> page) {
//        PaginatedResponse<T> response = new PaginatedResponse<>();
//        response.setContent(page.getContent());
//        response.setTotalElements(page.getTotalElements());
//        response.setTotalPages(page.getTotalPages());
//        response.setPageNumber(page.getNumber());
//        response.setPageSize(page.getSize());
//        return response;
//    }
//}
