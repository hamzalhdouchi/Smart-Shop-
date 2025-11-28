package com.smartshop;

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
import com.smartshop.mapper.ClientMapper;
import com.smartshop.repository.ClientRepository;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private Pageable pageable;

    private User user;
    private Client client;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("1")
                .username("testuser")
                .password("hashed")
                .role(UserRole.CLIENT)
                .build();

        client = Client.builder()
                .id("1")
                .nom("Test Nom")
                .email("test@example.com")
                .user(user)
                .tier(ClientTier.SILVER)
                .build();

        user.setClient(client);
    }

    @Test
    void testCreateClientSuccess() {
        UserClientRegistrationDTO dto = UserClientRegistrationDTO.builder()
                .username("newuser")
                .password("password")
                .role(UserRole.CLIENT)
                .nom("Nom")
                .email("new@example.com")
                .build();

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(clientRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(clientMapper.toClientWithUserResponse(any(Client.class))).thenReturn(new ClientWithUserResponseDTO());

        assertDoesNotThrow(() -> clientService.create(dto));
        verify(userRepository).findByUsername("newuser");
        verify(clientRepository).findByEmail("new@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateClientDuplicateUsername() {
        UserClientRegistrationDTO dto = UserClientRegistrationDTO.builder()
                .username("testuser").password("pass").nom("Nom").email("nom@example.com").build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(DuplicateResourceException.class, () -> clientService.create(dto));
    }

    @Test
    void testGetClientById_Success() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));
        when(clientMapper.toClientResponse(client)).thenReturn(new ClientResponseDTO());

        assertNotNull(clientService.getClientById("1"));
        verify(clientMapper).toClientResponse(client);
    }

    @Test
    void testGetClientById_NotFound() {
        when(clientRepository.findById("99")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> clientService.getClientById("99"));
    }

    @Test
    void testGetAllClients() {
        Page<Client> clientPage = new PageImpl<>(Collections.singletonList(client));
        when(clientRepository.findAll(pageable)).thenReturn(clientPage);
        when(clientMapper.toClientResponse(client)).thenReturn(new ClientResponseDTO());

        Page<ClientResponseDTO> result = clientService.getAllClients(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testUpdateClientSuccess() {
        ClientUpdateDTO dto = new ClientUpdateDTO();
        dto.setUsername("updatedUser");
        dto.setPassword("newPass");
        dto.setNom("New Nom");
        dto.setEmail("update@example.com");

        when(clientRepository.findById("1")).thenReturn(Optional.of(client));
        when(userRepository.findByUsername("updatedUser")).thenReturn(Optional.empty());
        when(clientRepository.findByEmail("update@example.com")).thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toClientWithUserResponse(client)).thenReturn(new ClientWithUserResponseDTO());

        assertNotNull(clientService.update("1", dto));
    }

    @Test
    void testUpdateClientDuplicateUsername() {
        ClientUpdateDTO dto = new ClientUpdateDTO();
        dto.setUsername("otheruser");
        dto.setPassword("p");

        when(clientRepository.findById("1")).thenReturn(Optional.of(client));
        when(userRepository.findByUsername("otheruser")).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateResourceException.class, () -> clientService.update("1", dto));
    }


    @Test
    void testDeleteClientSuccess() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));
        doNothing().when(clientRepository).delete(client);

        assertDoesNotThrow(() -> clientService.delete("1"));
        verify(clientRepository).delete(client);
    }

    @Test
    void testDeleteClientNotFound() {
        when(clientRepository.findById("99")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> clientService.delete("99"));
    }

    @Test
    void testChangeTierSuccess() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toClientResponse(client)).thenReturn(new ClientResponseDTO());

        ClientResponseDTO result = clientService.changeTair("1", "gold");
        assertNotNull(result);
        assertEquals(ClientTier.GOLD, client.getTier());
    }

    @Test
    void testChangeTierNotFound() {
        when(clientRepository.findById("99")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> clientService.changeTair("99", "gold"));
    }
}

