package com.smartshop.service.impl;

import com.smartshop.dto.requist.createRequistDto.UserClientRegistrationDTO;
import com.smartshop.dto.requist.updateRequistDto.ClientUpdateDTO;
import com.smartshop.dto.response.client.ClientResponseDTO;
import com.smartshop.dto.response.client.ClientStatisticsDTO;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Service
    @Transactional
public class ClientServiceImpl implements ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private  ClientMapper clientMapper;

    @Override
    public ClientWithUserResponseDTO create(UserClientRegistrationDTO dto) {
        log.info("Starting ClientService.create with username={}", dto.getUsername());

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            log.warn("Username already exists: {}", dto.getUsername());
            throw new DuplicateResourceException("Username already exists");
        }
        if (clientRepository.findByEmail(dto.getEmail()).isPresent()) {
            log.warn("Email already exists: {}", dto.getEmail());
            throw new DuplicateResourceException("Email already exists");
        }

        if (dto.getRole().equals(UserRole.ADMIN)) {
            log.warn("Attempt to create client with ADMIN role for username={}", dto.getUsername());
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

        log.info("Client created successfully with id={} for username={}", savedUser.getClient().getId(), dto.getUsername());
        log.info("Finished ClientService.create with username={}", dto.getUsername());

        return clientMapper.toClientWithUserResponse(savedUser.getClient());
    }

    @Override
    public ClientResponseDTO getClientById(String id) {
        log.info("Starting ClientService.getClientById with id={}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        log.info("Finished ClientService.getClientById with id={}", id);
        return clientMapper.toClientResponse(client);
    }

    @Override
    public ClientWithUserResponseDTO getClientWithUserById(String id) {
        log.info("Starting ClientService.getClientWithUserById with id={}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        log.info("Finished ClientService.getClientWithUserById with id={}", id);
        return clientMapper.toClientWithUserResponse(client);
    }

    @Override
    public Page<ClientResponseDTO> getAllClients(Pageable pageable) {
        log.info("Starting ClientService.getAllClients with page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Client> clientsPage = clientRepository.findAll(pageable);

        log.info("Retrieved {} clients from database", clientsPage.getTotalElements());
        log.info("Finished ClientService.getAllClients");

        return clientsPage.map(clientMapper::toClientResponse);
    }

    @Override
    public Page<ClientWithUserResponseDTO> getAllClientsWithUser(Pageable pageable) {
        log.info("Starting ClientService.getAllClientsWithUser with page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Client> clientsPage = clientRepository.findAll(pageable);

        log.info("Retrieved {} clients with users from database", clientsPage.getTotalElements());
        log.info("Finished ClientService.getAllClientsWithUser");

        return clientsPage.map(clientMapper::toClientWithUserResponse);
    }

    @Override
    public ClientWithUserResponseDTO update(String id, ClientUpdateDTO dto) {
        log.info("Starting ClientService.update with id={}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        User user = client.getUser();

        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
                log.warn("Username already exists during update: {}", dto.getUsername());
                throw new DuplicateResourceException("Username already exists");
            }
            user.setUsername(dto.getUsername());
            log.info("Username updated for client id={}", id);
        }

        String hashPassword =  BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());


        if (dto.getPassword() != null) {
            user.setPassword(hashPassword);
            log.info("Password updated for client id={}", id);
        }

        if (dto.getNom() != null) {
            client.setNom(dto.getNom());
            log.info("Name updated for client id={}", id);
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(client.getEmail())) {
            if (clientRepository.findByEmail(dto.getEmail()).isPresent()) {
                log.warn("Email already exists during update: {}", dto.getEmail());
                throw new DuplicateResourceException("Email already exists");
            }
            client.setEmail(dto.getEmail());
            log.info("Email updated for client id={}", id);
        }

        Client updatedClient = clientRepository.save(client);

        log.info("Client updated successfully with id={}", id);
        log.info("Finished ClientService.update with id={}", id);

        return clientMapper.toClientWithUserResponse(updatedClient);
    }

    @Override
    public void delete(String id) {
        log.info("Starting ClientService.delete with id={}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        clientRepository.delete(client);

        log.info("Client deleted successfully with id={}", id);
        log.info("Finished ClientService.delete with id={}", id);
    }

    @Override
    public ClientResponseDTO changeTair(String id, String tair) {
        log.info("Starting ClientService.changeTair with id={} and tier={}", id, tair);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        ClientTier clientTier = ClientTier.valueOf(tair.toUpperCase());
        client.setTier(clientTier);
        Client updatedClient = clientRepository.save(client);

        log.info("Client tier changed successfully to {} for id={}", clientTier, id);
        log.info("Finished ClientService.changeTair with id={}", id);

        return clientMapper.toClientResponse(updatedClient);
    }

    @Override
    public ClientStatisticsDTO getClientStatistics(String clientId) {
        log.info("Starting ClientService.getClientStatistics with clientId={}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (client.getTotalOrders() > 0) {
            averageOrderValue = client.getTotalSpent()
                    .divide(new BigDecimal(client.getTotalOrders()), 2, RoundingMode.HALF_UP);
        }

        log.info("Statistics calculated for client id={}: totalOrders={}, totalSpent={}",
                clientId, client.getTotalOrders(), client.getTotalSpent());
        log.info("Finished ClientService.getClientStatistics with clientId={}", clientId);

        return ClientStatisticsDTO.builder()
                .clientId(client.getId())
                .nom(client.getNom())
                .email(client.getEmail())
                .tier(client.getTier())
                .totalOrders(client.getTotalOrders())
                .totalSpent(client.getTotalSpent())
                .build();
    }
}
