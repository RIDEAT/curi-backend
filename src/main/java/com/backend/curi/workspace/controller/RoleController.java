package com.backend.curi.workspace.controller;

import com.backend.curi.workspace.controller.dto.RoleRequest;
import com.backend.curi.workspace.controller.dto.RoleResponse;
import com.backend.curi.workspace.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}/roles")
public class RoleController {

    private final RoleService roleService;
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getRoles(@PathVariable Long workspaceId){
        var response = roleService.getRoles(workspaceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable Long roleId){
        var response = roleService.getRole(roleId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RoleResponse> createRole (@PathVariable Long workspaceId, @RequestBody @Valid RoleRequest roleRequest){
        var response = roleService.createRole(workspaceId, roleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<RoleResponse> updateRole (@PathVariable Long roleId, @RequestBody @Valid RoleRequest roleRequest){
        var response = roleService.updateRole(roleId, roleRequest);
        return ResponseEntity.ok(response);
    }
}
