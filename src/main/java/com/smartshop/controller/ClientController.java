package com.smartshop.controller;

import com.smartshop.apiResponse.ApiResponseDTO;
import com.smartshop.dto.requist.createRequistDto.UserClientRegistrationDTO;
import com.smartshop.dto.requist.updateRequistDto.ClientUpdateDTO;
import com.smartshop.dto.response.client.ClientResponseDTO;
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
    public ResponseEntity<ApiResponseDTO<ClientWithUserResponseDTO>> createClient(
            @Valid @RequestBody UserClientRegistrationDTO dto) {

        ClientWithUserResponseDTO client = clientService.create(dto);

        ApiResponseDTO<ClientWithUserResponseDTO> response = ApiResponseDTO.success(
                client,
                "Client created successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ClientResponseDTO>> getClientById(
            @PathVariable String id) {

        ClientResponseDTO client = clientService.getClientById(id);

        ApiResponseDTO<ClientResponseDTO> response = ApiResponseDTO.success(
                client,
                "Client retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/with-user")
    public ResponseEntity<ApiResponseDTO<ClientWithUserResponseDTO>> getClientWithUserById(
            @PathVariable String id) {

        ClientWithUserResponseDTO client = clientService.getClientWithUserById(id);

        ApiResponseDTO<ClientWithUserResponseDTO> response = ApiResponseDTO.success(
                client,
                "Client with user details retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<ClientResponseDTO>>> getAllClients(
            @PageableDefault(page = 0, size = 20, sort = "nom") Pageable pageable) {

        Page<ClientResponseDTO> clients = clientService.getAllClients(pageable);

        ApiResponseDTO<Page<ClientResponseDTO>> response = ApiResponseDTO.success(
                clients,
                "Clients retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/with-user")
    public ResponseEntity<ApiResponseDTO<Page<ClientWithUserResponseDTO>>> getAllClientsWithUser(
            @PageableDefault(page = 0, size = 20, sort = "nom") Pageable pageable) {

        Page<ClientWithUserResponseDTO> clients = clientService.getAllClientsWithUser(pageable);

        ApiResponseDTO<Page<ClientWithUserResponseDTO>> response = ApiResponseDTO.success(
                clients,
                "Clients with user details retrieved successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ClientWithUserResponseDTO>> updateClient(
            @PathVariable String id,
            @Valid @RequestBody ClientUpdateDTO dto) {

        ClientWithUserResponseDTO client = clientService.update(id, dto);

        ApiResponseDTO<ClientWithUserResponseDTO> response = ApiResponseDTO.success(
                client,
                "Client updated successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteClient(@PathVariable String id) {

        clientService.delete(id);

        ApiResponseDTO<Void> response = ApiResponseDTO.success("Client deleted successfully");
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/change-Tair/{tair}")
    public ResponseEntity<ApiResponseDTO<ClientResponseDTO>> changeTair(
            @PathVariable String id,
            @PathVariable String tair){

        ClientResponseDTO client =  clientService.changeTair(id, tair);
        ApiResponseDTO<ClientResponseDTO> response = ApiResponseDTO.success(
                client,
                "Client  successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

}


