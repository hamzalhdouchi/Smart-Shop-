package com.smartshop.controller;

import com.smartshop.apiResponse.ApiResponse;
import com.smartshop.dto.requist.createRequistDto.UserClientRegistrationDTO;
import com.smartshop.dto.requist.updateRequistDto.ClientUpdateDTO;
import com.smartshop.dto.response.client.ClientResponseDTO;
import com.smartshop.dto.response.client.ClientStatisticsDTO;
import com.smartshop.dto.response.client.ClientWithUserResponseDTO;
import com.smartshop.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final HttpServletRequest request;

    @PostMapping
    public ResponseEntity<ApiResponse<ClientWithUserResponseDTO>> createClient(
            @Valid @RequestBody UserClientRegistrationDTO dto) {

        ClientWithUserResponseDTO client = clientService.create(dto);

        ApiResponse<ClientWithUserResponseDTO> response = ApiResponse.success(
                client,
                "Client created successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponseDTO>> getClientById(
            @PathVariable String id) {

        ClientResponseDTO client = clientService.getClientById(id);

        ApiResponse<ClientResponseDTO> response = ApiResponse.success(
                client,
                "Client retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/with-user")
    public ResponseEntity<ApiResponse<ClientWithUserResponseDTO>> getClientWithUserById(
            @PathVariable String id) {

        ClientWithUserResponseDTO client = clientService.getClientWithUserById(id);

        ApiResponse<ClientWithUserResponseDTO> response = ApiResponse.success(
                client,
                "Client with user details retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ClientResponseDTO>>> getAllClients(
            @PageableDefault(page = 0, size = 20, sort = "nom") Pageable pageable) {

        Page<ClientResponseDTO> clients = clientService.getAllClients(pageable);

        ApiResponse<Page<ClientResponseDTO>> response = ApiResponse.success(
                clients,
                "Clients retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/with-user")
    public ResponseEntity<ApiResponse<Page<ClientWithUserResponseDTO>>> getAllClientsWithUser(
            @PageableDefault(page = 0, size = 20, sort = "nom") Pageable pageable) {

        Page<ClientWithUserResponseDTO> clients = clientService.getAllClientsWithUser(pageable);

        ApiResponse<Page<ClientWithUserResponseDTO>> response = ApiResponse.success(
                clients,
                "Clients with user details retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientWithUserResponseDTO>> updateClient(
            @PathVariable String id,
            @Valid @RequestBody ClientUpdateDTO dto) {

        ClientWithUserResponseDTO client = clientService.update(id, dto);

        ApiResponse<ClientWithUserResponseDTO> response = ApiResponse.success(
                client,
                "Client updated successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable String id) {

        clientService.delete(id);

        ApiResponse<Void> response = ApiResponse.success("Client deleted successfully");
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/change-Tair/{tair}")
    public ResponseEntity<ApiResponse<ClientResponseDTO>> changeTair(
            @PathVariable String id,
            @PathVariable String tair){

        ClientResponseDTO client =  clientService.changeTair(id, tair);
        ApiResponse<ClientResponseDTO> response = ApiResponse.success(
                client,
                "Client  successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clientId}/statistics")
    public ResponseEntity<ApiResponse<ClientStatisticsDTO>> getClientStatistics(
            @PathVariable String clientId
    ) {
        ClientStatisticsDTO stats = clientService.getClientStatistics(clientId);
        return ResponseEntity.ok(ApiResponse.success(stats, "Client statistics retrieved successfully"));
    }

}


