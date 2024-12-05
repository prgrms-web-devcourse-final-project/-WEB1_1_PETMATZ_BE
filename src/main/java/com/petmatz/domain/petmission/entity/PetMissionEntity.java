package com.petmatz.domain.petmission.entity;

import com.petmatz.domain.pet.Pet;
import com.petmatz.domain.petmission.dto.PetMissionInfo;
import com.petmatz.common.constants.PetMissionStatusZip;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetMissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime petMissionStarted;

    private LocalDateTime petMissionEnd;

    @Enumerated(EnumType.STRING)
    private PetMissionStatusZip status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;


    @OneToMany(mappedBy = "petMission", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PetMissionAskEntity> petMissionAsks = new ArrayList<>();

    @Builder
    public PetMissionEntity(LocalDateTime petMissionStarted, LocalDateTime petMissionEnd, PetMissionStatusZip status, Pet pet, List<UserToPetMissionEntity> userPetMissions, List<PetMissionAskEntity> petMissionAsks) {
        this.petMissionStarted = petMissionStarted;
        this.petMissionEnd = petMissionEnd;
        this.status = status;
        this.pet = pet;
    }


    public static PetMissionEntity of(PetMissionInfo petMissionInfo, Pet pet) {
        return PetMissionEntity.builder()
                .petMissionStarted(petMissionInfo.missionStarted())
                .petMissionEnd(petMissionInfo.missionEnd())
                .status(PetMissionStatusZip.fromDescription("시작"))
                .pet(pet)
                .build();
    }

    public void addPetMissionAsk(List<PetMissionAskEntity> ask) {
        petMissionAsks.addAll(ask);
        for (PetMissionAskEntity petMissionAsk : petMissionAsks) {
            System.out.println("petMissionAsk.getComment() :: " + petMissionAsk.getComment());
        }
        ask.forEach(petMissionAskEntity -> petMissionAskEntity.addPetMission(this));
    }


    public void updatePetMissionStatusZip(PetMissionStatusZip updateStatus) {
        status = updateStatus;
    }



}
