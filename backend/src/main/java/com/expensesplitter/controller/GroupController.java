package com.expensesplitter.controller;

import com.expensesplitter.dto.AddMemberRequest;
import com.expensesplitter.dto.CreateGroupRequest;
import com.expensesplitter.dto.GroupListResponse;
import com.expensesplitter.dto.MessageResponse;
import com.expensesplitter.model.GroupMember;
import com.expensesplitter.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/createGroup")
    public MessageResponse createGroup(@Valid @RequestBody CreateGroupRequest request, Authentication authentication) {
        groupService.createGroup(request, authentication.getName());
        return new MessageResponse("Group created successfully");
    }

    @PostMapping("/{groupId}/members")
    public String addMember(@PathVariable Long groupId,
                            @Valid @RequestBody AddMemberRequest request,
                            Authentication authentication) {

        groupService.addMember(groupId, request, authentication.getName());
        return "Member added successfully";
    }

    @GetMapping("/getGroupList")
    public List<GroupListResponse> myGroups(Authentication authentication) {
        return groupService.myGroups(authentication.getName());
    }
}
