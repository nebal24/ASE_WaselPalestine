package com.wasel.repository;

import com.wasel.entity.Report;
import com.wasel.entity.User;
import com.wasel.entity.Vote;
import com.wasel.model.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    //checks if this user already voted on this report before
    Optional<Vote> findByUserAndReport(User user, Report report);
    //counts how many upvotes or downvotes this report has
    long countByReportAndVoteType(Report report, VoteType voteType);

    List<Vote> findByReport(Report report);

    void deleteByUserAndReport(User user, Report report);
}