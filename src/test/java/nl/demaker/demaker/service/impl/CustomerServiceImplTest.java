package nl.demaker.demaker.service.impl;

import nl.demaker.demaker.dto.request.CustomerRequest;
import nl.demaker.demaker.dto.response.CustomerResponse;
import nl.demaker.demaker.exception.ResourceNotFoundException;
import nl.demaker.demaker.model.Customer;
import nl.demaker.demaker.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;
    private CustomerRequest testCustomerRequest;

    @BeforeEach
    void setUp() {
        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john@test.com");
        testCustomer.setPhoneNumber("0612345678");

        // Setup test request
        testCustomerRequest = new CustomerRequest();
        testCustomerRequest.setFirstName("John");
        testCustomerRequest.setLastName("Doe");
        testCustomerRequest.setEmail("john@test.com");
        testCustomerRequest.setPhoneNumber("0612345678");
    }

    @Test
    void createCustomer_WithValidRequest_ReturnsSavedCustomer() {
        // Arrange
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        CustomerResponse response = customerService.createCustomer(testCustomerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testCustomer.getId(), response.getId());
        assertEquals(testCustomer.getFirstName(), response.getFirstName());
        assertEquals(testCustomer.getLastName(), response.getLastName());
        assertEquals(testCustomer.getEmail(), response.getEmail());
        assertEquals(testCustomer.getPhoneNumber(), response.getPhoneNumber());

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void createCustomer_WithValidData_SavesCustomerCorrectly() {
        // Arrange
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        CustomerResponse response = customerService.createCustomer(testCustomerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john@test.com", response.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void getCustomerById_WithValidId_ReturnsCustomer() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // Act
        CustomerResponse response = customerService.getCustomerById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(testCustomer.getId(), response.getId());
        assertEquals(testCustomer.getFirstName(), response.getFirstName());
        assertEquals(testCustomer.getLastName(), response.getLastName());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerById(999L)
        );

        assertTrue(exception.getMessage().contains("Customer not found"));
        verify(customerRepository, times(1)).findById(999L);
    }

    @Test
    void getAllCustomers_ReturnsListOfCustomers() {
        // Arrange
        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setEmail("jane@test.com");
        customer2.setPhoneNumber("0687654321");

        when(customerRepository.findAll()).thenReturn(Arrays.asList(testCustomer, customer2));

        // Act
        List<CustomerResponse> responses = customerService.getAllCustomers();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("John", responses.get(0).getFirstName());
        assertEquals("Jane", responses.get(1).getFirstName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void getAllCustomers_WithEmptyDatabase_ReturnsEmptyList() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<CustomerResponse> responses = customerService.getAllCustomers();

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void updateCustomer_WithValidData_ReturnsUpdatedCustomer() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        CustomerRequest updateRequest = new CustomerRequest();
        updateRequest.setFirstName("Jane");
        updateRequest.setLastName("Smith");
        updateRequest.setEmail("jane@test.com");
        updateRequest.setPhoneNumber("0687654321");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1L);
        updatedCustomer.setFirstName("Jane");
        updatedCustomer.setLastName("Smith");
        updatedCustomer.setEmail("jane@test.com");
        updatedCustomer.setPhoneNumber("0687654321");

        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // Act
        CustomerResponse response = customerService.updateCustomer(1L, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("jane@test.com", response.getEmail());
        assertEquals("0687654321", response.getPhoneNumber());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomer_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.updateCustomer(999L, testCustomerRequest)
        );

        assertTrue(exception.getMessage().contains("Customer not found"));
        verify(customerRepository, times(1)).findById(999L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_WithValidId_DeletesCustomer() {
        // Arrange
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        // Act
        customerService.deleteCustomer(1L);

        // Assert
        verify(customerRepository, times(1)).existsById(1L);
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCustomer_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        when(customerRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.deleteCustomer(999L)
        );

        assertTrue(exception.getMessage().contains("Customer not found"));
        verify(customerRepository, times(1)).existsById(999L);
        verify(customerRepository, never()).deleteById(999L);
    }
}
