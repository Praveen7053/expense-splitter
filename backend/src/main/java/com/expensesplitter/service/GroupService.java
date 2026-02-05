package com.expensesplitter.service;

import com.expensesplitter.dto.AddMemberRequest;
import com.expensesplitter.dto.CreateGroupRequest;
import com.expensesplitter.model.GroupEntity;
import com.expensesplitter.model.GroupMember;
import com.expensesplitter.model.UserExpensesSplitter;
import com.expensesplitter.repository.GroupMemberRepository;
import com.expensesplitter.repository.GroupRepository;
import com.expensesplitter.repository.UserExpensesSplitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserExpensesSplitterRepository userRepository;

    public GroupEntity createGroup(CreateGroupRequest request, String ownerEmail) {

        UserExpensesSplitter owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupEntity group = GroupEntity.builder()
                .name(request.getName())
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .build();

        groupRepository.save(group);

        // add owner as member
        GroupMember ownerMember = GroupMember.builder()
                .group(group)
                .user(owner)
                .role("OWNER")
                .joinedAt(LocalDateTime.now())
                .build();

        groupMemberRepository.save(ownerMember);

        return group;
    }

    public void addMember(Long groupId, AddMemberRequest request, String ownerEmail) {

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Only owner can add members");
        }

        UserExpensesSplitter user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (groupMemberRepository.findByGroupIdAndUserId(groupId, user.getId()).isPresent()) {
            throw new RuntimeException("User already in group");
        }

        GroupMember member = GroupMember.builder()
                .group(group)
                .user(user)
                .role("MEMBER")
                .joinedAt(LocalDateTime.now())
                .build();

        groupMemberRepository.save(member);
    }

    public List<GroupMember> myGroups(String email) {
        UserExpensesSplitter user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return groupMemberRepository.findByUserId(user.getId());
    }
}
