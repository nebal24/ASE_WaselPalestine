package com.wasel.dto;

import java.util.List;

public class PagedReportResponse {

    private List<ReportSummaryDTO> reports;
    private int currentPage;
    private int totalPages;
    private long totalReports;
    private int pageSize;

    public PagedReportResponse(List<ReportSummaryDTO> reports,
                               int currentPage,
                               int totalPages,
                               long totalReports,
                               int pageSize) {
        this.reports = reports;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalReports = totalReports;
        this.pageSize = pageSize;
    }

    public List<ReportSummaryDTO> getReports()  { return reports; }
    public int getCurrentPage()                  { return currentPage; }
    public int getTotalPages()                   { return totalPages; }
    public long getTotalReports()                { return totalReports; }
    public int getPageSize()                     { return pageSize; }
}