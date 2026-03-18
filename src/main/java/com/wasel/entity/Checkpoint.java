package com.wasel.entity;

import com.wasel.model.CheckpointStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "checkpoints")
@Data
public class Checkpoint  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private CheckpointStatus currentStatus;

    private String description;

    @OneToMany(mappedBy = "checkpoint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CheckpointStatusHistory> statusHistory = new ArrayList<>();

    // علقنا Incident عشان ما يسبب مشاكل الآن
    // @OneToMany(mappedBy = "checkpoint")
    // private List<Incident> incidents = new ArrayList<>();
}