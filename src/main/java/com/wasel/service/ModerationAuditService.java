package com.wasel.service;

import com.wasel.entity.ModerationAction;
import com.wasel.entity.Report;
import com.wasel.entity.User;
import com.wasel.model.ModerationActionType;
import com.wasel.repository.ModerationActionRepository;
import org.springframework.stereotype.Service;

@Service
public class ModerationAuditService {

    private final ModerationActionRepository moderationActionRepository;

    public ModerationAuditService(ModerationActionRepository moderationActionRepository) {
        this.moderationActionRepository = moderationActionRepository;
    }

    public void log(Report report, User performedBy, ModerationActionType actionType, String reason) {
        ModerationAction action = new ModerationAction();
        action.setReport(report);
        action.setPerformedBy(performedBy);
        action.setActionType(actionType);
        action.setReason(reason);
        moderationActionRepository.save(action);
    }
}