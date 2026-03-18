package com.wasel.dto;

import java.util.List;

public class ReportResponseDTO {
    private List<String> messages;

    public ReportResponseDTO(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() { return messages; }
    public void setMessages(List<String> messages) { this.messages = messages; }
}