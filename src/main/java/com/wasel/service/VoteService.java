package com.wasel.service;

import com.wasel.dto.VoteResponse;
import com.wasel.entity.ModerationAction;
import com.wasel.entity.Report;
import com.wasel.entity.User;
import com.wasel.entity.Vote;
import com.wasel.exception.ResourceNotFoundException;
import com.wasel.model.ModerationActionType;
import com.wasel.model.ReportStatus;
import com.wasel.model.VoteType;
import com.wasel.repository.ModerationActionRepository;
import com.wasel.repository.ReportRepository;
import com.wasel.repository.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final ReportRepository reportRepository;
    private final ModerationActionRepository moderationActionRepository;
    private final ModerationAuditService moderationAuditService;

    private static final long VERIFY_THRESHOLD = 5;
    private static final long REJECT_THRESHOLD = -3;

    public VoteService(VoteRepository voteRepository,
                       ReportRepository reportRepository,
                       ModerationActionRepository moderationActionRepository,
                       ModerationAuditService moderationAuditService) {
        this.voteRepository = voteRepository;
        this.reportRepository = reportRepository;
        this.moderationActionRepository = moderationActionRepository;
        this.moderationAuditService = moderationAuditService;
    }

    @Transactional
    public VoteResponse castOrUpdateVote(Long reportId, User user, VoteType voteType) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));

        Optional<Vote> existingVoteOpt = voteRepository.findByUserAndReport(user, report);

        String message;

        if (existingVoteOpt.isPresent()) {
            Vote existingVote = existingVoteOpt.get();

            if (existingVote.getVoteType() == voteType) {
                message = "User already cast the same vote.";
            } else {
                existingVote.setVoteType(voteType);
                voteRepository.save(existingVote);

                moderationAuditService.log(
                        report,
                        user,
                        ModerationActionType.VOTE_CHANGED,
                        "User changed vote to " + voteType
                );

                message = "Vote updated successfully.";
            }
        } else {
            Vote vote = new Vote();
            vote.setUser(user);
            vote.setReport(report);
            vote.setVoteType(voteType);
            voteRepository.save(vote);

            moderationAuditService.log(
                    report,
                    user,
                    ModerationActionType.VOTE_CAST,
                    "User cast " + voteType
            );

            message = "Vote added successfully.";
        }

        autoModerateReport(report);

        return buildVoteResponse(report, user, voteType, message);
    }

    @Transactional
    public VoteResponse removeVote(Long reportId, User user) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));

        Vote vote = voteRepository.findByUserAndReport(user, report)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found for this user and report"));

        VoteType previousType = vote.getVoteType();
        voteRepository.delete(vote);

        moderationAuditService.log(
                report,
                user,
                ModerationActionType.VOTE_REMOVED,
                "User removed vote " + previousType
        );

        autoModerateReport(report);

        return buildVoteResponse(report, user, null, "Vote removed successfully.");
    }

    public long getUpvotes(Report report) {
        return voteRepository.countByReportAndVoteType(report, VoteType.UPVOTE);
    }

    public long getDownvotes(Report report) {
        return voteRepository.countByReportAndVoteType(report, VoteType.DOWNVOTE);
    }

    public long getScore(Report report) {
        return getUpvotes(report) - getDownvotes(report);
    }

    @Transactional
    public void autoModerateReport(Report report) {
        long score = getScore(report);
        ReportStatus oldStatus = report.getStatus();

        if (oldStatus == ReportStatus.DUPLICATE) {
            return;
        }

        if (isManuallyModerated(report)) {
            return;
        }

        if (score >= VERIFY_THRESHOLD && oldStatus != ReportStatus.VERIFIED) {
            report.setStatus(ReportStatus.VERIFIED);
            reportRepository.save(report);

            moderationAuditService.log(
                    report,
                    null,
                    ModerationActionType.AUTO_VERIFY,
                    "System auto-verified report because score reached " + score
            );

        } else if (score <= REJECT_THRESHOLD && oldStatus != ReportStatus.REJECTED) {
            report.setStatus(ReportStatus.REJECTED);
            reportRepository.save(report);

            moderationAuditService.log(
                    report,
                    null,
                    ModerationActionType.AUTO_REJECT,
                    "System auto-rejected report because score reached " + score
            );

        } else if (score > REJECT_THRESHOLD && score < VERIFY_THRESHOLD && oldStatus != ReportStatus.PENDING) {
            report.setStatus(ReportStatus.PENDING);
            reportRepository.save(report);

            moderationAuditService.log(
                    report,
                    null,
                    ModerationActionType.RESET_TO_PENDING,
                    "System reset report to PENDING because score is now " + score
            );
        }
    }

    private boolean isManuallyModerated(Report report) {
        List<ModerationAction> actions = moderationActionRepository.findByReportOrderByCreatedAtDesc(report);

        for (ModerationAction action : actions) {
            ModerationActionType type = action.getActionType();

            if (type == ModerationActionType.VERIFY ||
                    type == ModerationActionType.REJECT ||
                    type == ModerationActionType.MARK_DUPLICATE) {
                return true;
            }

            if (type == ModerationActionType.AUTO_VERIFY ||
                    type == ModerationActionType.AUTO_REJECT ||
                    type == ModerationActionType.RESET_TO_PENDING ||
                    type == ModerationActionType.AUTO_MARK_DUPLICATE) {
                return false;
            }
        }

        return false;
    }

    private VoteResponse buildVoteResponse(Report report, User user, VoteType currentVoteType, String message) {
        long upvotes = getUpvotes(report);
        long downvotes = getDownvotes(report);
        long score = upvotes - downvotes;

        return new VoteResponse(
                report.getReportId(),
                user.getId(),
                currentVoteType,
                upvotes,
                downvotes,
                score,
                report.getStatus(),
                message
        );
    }
}