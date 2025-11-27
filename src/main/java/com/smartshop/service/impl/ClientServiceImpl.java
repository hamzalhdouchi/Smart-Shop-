package com.smartshop.service.impl;

import com.smartshop.dto.requist.createRequistDto.UserClientRegistrationDTO;
import com.smartshop.dto.requist.updateRequistDto.ClientUpdateDTO;
import com.smartshop.dto.response.client.ClientResponseDTO;
import com.smartshop.dto.response.client.ClientWithUserResponseDTO;
import com.smartshop.entity.Client;
import com.smartshop.entity.User;
import com.smartshop.enums.ClientTier;
import com.smartshop.enums.UserRole;
import com.smartshop.exception.DuplicateResourceException;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.exception.ValidationException;
import com.smartshop.mapper.ClientMapper;
import com.smartshop.repository.ClientRepository;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.ClientService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private  ClientMapper clientMapper;

    @Override
    public ClientWithUserResponseDTO create(UserClientRegistrationDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (clientRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (dto.getRole().equals(UserRole.ADMIN)) {
            throw new ValidationException("Admin role is not allowed");
        }
        String hashPassword =  BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());

        User user = User.builder()
                .username(dto.getUsername())
                .password(hashPassword)
                .role(dto.getRole() != null ? dto.getRole() : UserRole.CLIENT)
                .build();

        Client client = Client.builder()
                .nom(dto.getNom())
                .email(dto.getEmail())
                .user(user)
                .build();

        user.setClient(client);
        User savedUser = userRepository.save(user);

        return clientMapper.toClientWithUserResponse(savedUser.getClient());
    }

    @Override
    public ClientResponseDTO getClientById(String id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return clientMapper.toClientResponse(client);
    }

    @Override
    public ClientWithUserResponseDTO getClientWithUserById(String id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return clientMapper.toClientWithUserResponse(client);
    }

    @Override
    public Page<ClientResponseDTO> getAllClients(Pageable pageable) {
        Page<Client> clientsPage = clientRepository.findAll(pageable);
        return clientsPage.map(clientMapper::toClientResponse);
    }

    @Override
    public Page<ClientWithUserResponseDTO> getAllClientsWithUser(Pageable pageable) {
        Page<Client> clientsPage = clientRepository.findAll(pageable);
        return clientsPage.map(clientMapper::toClientWithUserResponse);
    }

    @Override
    public ClientWithUserResponseDTO update(String id, ClientUpdateDTO dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        User user = client.getUser();

        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
                throw new DuplicateResourceException("Username already exists");
            }
            user.setUsername(dto.getUsername());
        }

        String hashPassword =  BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());


        if (dto.getPassword() != null) {
            user.setPassword(hashPassword);
        }

        if (dto.getNom() != null) {
            client.setNom(dto.getNom());
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(client.getEmail())) {
            if (clientRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new DuplicateResourceException("Email already exists");
            }
            client.setEmail(dto.getEmail());
        }

        Client updatedClient = clientRepository.save(client);
        return clientMapper.toClientWithUserResponse(updatedClient);
    }

    @Override
    public void delete(String id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        clientRepository.delete(client);
    }

    @Override
    public ClientResponseDTO changeTair(String id, String tair) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        ClientTier clientTier = ClientTier.valueOf(tair.toUpperCase());
        client.setTier(clientTier);
        Client updatedClient = clientRepository.save(client);
        return clientMapper.toClientResponse(updatedClient);
    }
}


