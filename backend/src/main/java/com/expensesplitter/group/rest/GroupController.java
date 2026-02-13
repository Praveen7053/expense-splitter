package com.expensesplitter.group.rest;


import com.expensesplitter.group.dto.AddMemberRequest;
import com.expensesplitter.group.dto.CreateGroupRequest;
import com.expensesplitter.group.dto.GroupListResponse;
import com.expensesplitter.group.dto.GroupMemberResponse;
import com.expensesplitter.common.response.ApiResponse;
import com.expensesplitter.group.service.GroupService;
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

    @PostMapping
    public ApiResponse<Void> createGroup( @Valid @RequestBody CreateGroupRequest request, Authentication authentication) {
        groupService.createGroup(request, authentication.getName());
        return new ApiResponse<>(true,"Group created successfully",null);
    }

    @PostMapping("/{groupId}/members")
    public ApiResponse<Void> addMember(@PathVariable Long groupId, @Valid @RequestBody AddMemberRequest request, Authentication authentication) {
        groupService.addMember(groupId, request, authentication.getName());
        return new ApiResponse<>(true,"Member added successfully",null);
    }

    @GetMapping("/getGroupList")
    public ApiResponse<List<GroupListResponse>> myGroups(Authentication authentication) {
        List<GroupListResponse> groups =groupService.myGroups(authentication.getName());
        return new ApiResponse<>(true,"Groups fetched successfully", groups);
    }

    @GetMapping("/{groupId}/members")
    public ApiResponse<List<GroupMemberResponse>> getMembers( @PathVariable Long groupId, Authentication authentication) {
        List<GroupMemberResponse> members = groupService.getMembers(groupId, authentication.getName());
        return new ApiResponse<>(true, "Members fetched successfully", members);
    }
}
