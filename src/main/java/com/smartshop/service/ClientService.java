package com.smartshop.service;

import com.smartshop.dto.requist.createRequistDto.UserClientRegistrationDTO;
import com.smartshop.dto.requist.updateRequistDto.ClientUpdateDTO;
import com.smartshop.dto.response.client.ClientResponseDTO;
import com.smartshop.dto.response.client.ClientWithUserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ClientService {

    ClientWithUserResponseDTO create(UserClientRegistrationDTO dto);
    ClientResponseDTO getClientById(String id);
    ClientWithUserResponseDTO getClientWithUserById(String id);
    Page<ClientResponseDTO> getAllClients(Pageable pageable);
    Page<ClientWithUserResponseDTO> getAllClientsWithUser(Pageable pageable);
    ClientWithUserResponseDTO update(String id, ClientUpdateDTO dto);
    void delete(String id);
    ClientResponseDTO changeTair(String id, String tair);
}


