package com.ebanking.service.impl;

import com.ebanking.dto.request.BankAgentCreateRequest;
import com.ebanking.dto.response.BankAgentDTO;
import com.ebanking.entity.BankAgent;
import com.ebanking.entity.UserStatus;
import com.ebanking.repository.BankAgentRepository;
import com.ebanking.repository.UserRepository;
import com.ebanking.service.AdminService;
import com.ebanking.service.EmailService;
import com.ebanking.util.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AdminService for admin operations
 */
@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private BankAgentRepository bankAgentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private EmailService emailService;

    @Override
    public BankAgentDTO createBankAgent(BankAgentCreateRequest request) {
        logger.debug("Creating bank agent with username: {}", request.getUsername());

        // Validate if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        // Validate if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Validate if employee ID already exists
        if (bankAgentRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists: " + request.getEmployeeId());
        }

        // Generate password automatically if not provided
        String generatedPassword = request.getPassword();
        if (generatedPassword == null || generatedPassword.trim().isEmpty()) {
            generatedPassword = passwordGenerator.generatePassword();
            logger.debug("Generated password for bank agent: {}", request.getUsername());
        }

        // Create new bank agent
        BankAgent bankAgent = new BankAgent(
            request.getUsername(),
            passwordEncoder.encode(generatedPassword),
            request.getEmail(),
            request.getPhoneNumber(),
            request.getEmployeeId(),
            request.getBranch(),
            request.getPosition() != null ? request.getPosition() : "Agent"
        );

        // Save the bank agent
        BankAgent savedAgent = bankAgentRepository.save(bankAgent);

        logger.info("Bank agent created successfully with ID: {} and username: {}",
                   savedAgent.getId(), savedAgent.getUsername());

        // Send credentials via email
        try {
            emailService.sendBankAgentCredentials(
                savedAgent.getEmail(),
                savedAgent.getUsername(),
                savedAgent.getUsername(),
                generatedPassword,
                savedAgent.getEmployeeId(),
                savedAgent.getBranch()
            );
            logger.info("Credentials email sent successfully to bank agent: {}", savedAgent.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send credentials email to bank agent: {}", savedAgent.getEmail(), e);
            // Note: We don't throw exception here to avoid rolling back the agent creation
            // The agent is created successfully, but email sending failed
        }

        return convertToDTO(savedAgent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAgentDTO> getAllBankAgents() {
        logger.debug("Fetching all bank agents");
        List<BankAgent> agents = bankAgentRepository.findAll();
        return agents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BankAgentDTO getBankAgentById(Long id) {
        logger.debug("Fetching bank agent by ID: {}", id);
        BankAgent agent = bankAgentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank agent not found with ID: " + id));
        return convertToDTO(agent);
    }

    @Override
    @Transactional(readOnly = true)
    public BankAgentDTO getBankAgentByEmployeeId(String employeeId) {
        logger.debug("Fetching bank agent by employee ID: {}", employeeId);
        BankAgent agent = bankAgentRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Bank agent not found with employee ID: " + employeeId));
        return convertToDTO(agent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAgentDTO> getBankAgentsByBranch(String branch) {
        logger.debug("Fetching bank agents by branch: {}", branch);
        List<BankAgent> agents = bankAgentRepository.findByBranch(branch);
        return agents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BankAgentDTO updateBankAgentStatus(Long id, UserStatus status) {
        logger.debug("Updating bank agent status for ID: {} to {}", id, status);

        BankAgent agent = bankAgentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank agent not found with ID: " + id));

        agent.setStatus(status);
        BankAgent updatedAgent = bankAgentRepository.save(agent);

        logger.info("Bank agent status updated successfully for ID: {}", id);
        return convertToDTO(updatedAgent);
    }

    @Override
    public void deleteBankAgent(Long id) {
        logger.debug("Deleting bank agent with ID: {}", id);

        BankAgent agent = bankAgentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank agent not found with ID: " + id));

        // Check if agent has managed clients
        if (!agent.getManagedClients().isEmpty()) {
            throw new RuntimeException("Cannot delete bank agent with managed clients. " +
                    "Please reassign clients first.");
        }

        bankAgentRepository.delete(agent);
        logger.info("Bank agent deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public BankAgentStatistics getBankAgentStatistics() {
        logger.debug("Fetching bank agent statistics");

        // Get all bank agents
        List<BankAgent> allAgents = bankAgentRepository.findAll();
        long totalAgents = allAgents.size();

        // Count active and inactive agents based on their status
        long activeAgents = allAgents.stream()
                .filter(agent -> agent.getStatus() == UserStatus.ACTIVE)
                .count();

        long inactiveAgents = allAgents.stream()
                .filter(agent -> agent.getStatus() != UserStatus.ACTIVE)
                .count();

        // Count total managed clients across all agents
        long totalManagedClients = allAgents.stream()
                .mapToLong(agent -> agent.getManagedClients().size())
                .sum();

        logger.debug("Statistics: Total={}, Active={}, Inactive={}, ManagedClients={}",
                    totalAgents, activeAgents, inactiveAgents, totalManagedClients);

        return new BankAgentStatistics(totalAgents, activeAgents, inactiveAgents, totalManagedClients);
    }

    /**
     * Convert BankAgent entity to BankAgentDTO
     */
    private BankAgentDTO convertToDTO(BankAgent agent) {
        BankAgentDTO dto = new BankAgentDTO();
        dto.setId(agent.getId());
        dto.setUsername(agent.getUsername());
        dto.setEmail(agent.getEmail());
        dto.setPhoneNumber(agent.getPhoneNumber());
        dto.setEmployeeId(agent.getEmployeeId());
        dto.setBranch(agent.getBranch());
        dto.setPosition(agent.getPosition());
        dto.setStatus(agent.getStatus());
        dto.setRole(agent.getRole());
        dto.setCreatedAt(agent.getCreatedAt());
        dto.setUpdatedAt(agent.getUpdatedAt());
        dto.setManagedClientsCount(agent.getManagedClients().size());
        return dto;
    }
}
