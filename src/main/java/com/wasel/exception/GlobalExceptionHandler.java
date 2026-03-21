package com.wasel.exception;
import com.wasel.dto.ReportResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

//راقب كل الـ Controllers وامسك الأخطاء
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ReportResponseDTO> handleValidationException(ValidationException ex)
    {
        // Return the errors list inside ReportResponseDTO
        return new ResponseEntity<>(new ReportResponseDTO(ex.getErrors()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ReportResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex)
    {
        return new ResponseEntity<>(
                new ReportResponseDTO(List.of(ex.getMessage())),
                HttpStatus.NOT_FOUND  // 404
        );
    }
}