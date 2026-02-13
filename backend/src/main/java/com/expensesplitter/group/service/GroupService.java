package com.expensesplitter.group.service;

import com.expensesplitter.group.dto.AddMemberRequest;
import com.expensesplitter.group.dto.CreateGroupRequest;
import com.expensesplitter.group.dto.GroupListResponse;
import com.expensesplitter.group.dto.GroupMemberResponse;
import com.expensesplitter.group.entity.GroupEntity;
import com.expensesplitter.group.entity.GroupMember;
import com.expensesplitter.user.entity.UserExpensesSplitter;
import com.expensesplitter.group.repository.GroupMemberRepository;
import com.expensesplitter.group.repository.GroupRepository;
import com.expensesplitter.user.repository.UserExpensesSplitterRepository;
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

    public List<GroupListResponse> myGroups(String email) {
        UserExpensesSplitter user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<GroupMember> memberships = groupMemberRepository.findByUserId(user.getId());

        return memberships.stream()
                .map(m -> new GroupListResponse(
                        m.getGroup().getId(),
                        m.getGroup().getName()
                ))
                .toList();
    }

    public List<GroupMemberResponse> getMembers(Long groupId, String email) {
        List<GroupMember> members = groupMemberRepository.findByGroup_Id(groupId);
        return members.stream()
                .map(m -> new GroupMemberResponse(
                        m.getUser().getId(),
                        m.getUser().getName(),
                        m.getUser().getEmail(),
                        m.getRole()
                ))
                .toList();
    }


}
