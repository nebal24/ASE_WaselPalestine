package com.wasel.service;

import com.wasel.dto.UserStatsByRoleDTO;
import com.wasel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    /**
     * Query 3 — User Statistics by Role.
     *
     * Executes the native GROUP BY query and maps each Object[] row to a typed
     * DTO.  Column order matches the SELECT list in UserRepository exactly:
     * [0] role, [1] user_count, [2] total_submissions, [3] total_reports,
     * [4] total_moderations.
     */
    @Transactional(readOnly = true)
    public List<UserStatsByRoleDTO> getUserStatsByRole() {
        return userRepository.findUserStatsByRole()
                .stream()
                .map(row -> UserStatsByRoleDTO.builder()
                        .role((String) row[0])
                        .userCount(((Number) row[1]).longValue())
                        .totalSubmissions(((Number) row[2]).longValue())
                        .totalReports(((Number) row[3]).longValue())
                        .totalModerations(((Number) row[4]).longValue())
                        .build())
                .toList();
    }
}
