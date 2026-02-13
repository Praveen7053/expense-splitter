package com.expensesplitter.group.entity;

import com.expensesplitter.common.baseEntity.BaseEntity;
import com.expensesplitter.user.entity.UserExpensesSplitter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "group_members",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"group_id", "user_id"})
        })
public class GroupMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserExpensesSplitter user;

    @Column(nullable = false)
    private String role; // OWNER, MEMBER

    @Column(nullable = false)
    private Boolean isActive = true;

    private LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
}
