package com.petmatz.domain.chatting.entity;

import com.petmatz.domain.global.BaseEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class ChatRoomEntity extends BaseEntity {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserToChatRoomEntity> participants = new ArrayList<>();


    public void addParticipant(UserToChatRoomEntity participant) {
        this.participants.add(participant);
        participant.addChatRoom(this); // 연관관계 편의 메서드
    }

}
