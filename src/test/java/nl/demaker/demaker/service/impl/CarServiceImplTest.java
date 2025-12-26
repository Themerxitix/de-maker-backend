package nl.demaker.demaker.service.impl;

import nl.demaker.demaker.dto.request.CarRequest;
import nl.demaker.demaker.dto.response.CarResponse;
import nl.demaker.demaker.exception.ResourceNotFoundException;
import nl.demaker.demaker.model.Car;
import nl.demaker.demaker.model.Customer;
import nl.demaker.demaker.repository.CarRepository;
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
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CarServiceImpl carService;

    private Car testCar;
    private Customer testCustomer;
    private CarRequest testCarRequest;

    @BeforeEach
    void setUp() {
        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john@test.com");
        testCustomer.setPhoneNumber("0612345678");

        // Setup test car
        testCar = new Car();
        testCar.setId(1L);
        testCar.setLicensePlate("AB-123-CD");
        testCar.setBrand("Toyota");
        testCar.setModel("Corolla");
        testCar.setYear(2020);
        testCar.setCustomer(testCustomer);

        // Setup test request
        testCarRequest = new CarRequest();
        testCarRequest.setLicensePlate("AB-123-CD");
        testCarRequest.setBrand("Toyota");
        testCarRequest.setModel("Corolla");
        testCarRequest.setYear(2020);
        testCarRequest.setCustomerId(1L);
    }

    @Test
    void createCar_WithValidRequest_ReturnsSavedCar() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // Act
        CarResponse response = carService.createCar(testCarRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testCar.getId(), response.getId());
        assertEquals(testCar.getLicensePlate(), response.getLicensePlate());
        assertEquals(testCar.getBrand(), response.getBrand());
        assertEquals(testCar.getModel(), response.getModel());
        assertEquals(testCar.getYear(), response.getYear());
        assertEquals(testCustomer.getId(), response.getCustomerId());
        assertEquals("John Doe", response.getCustomerName());

        verify(customerRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void createCar_WithInvalidCustomerId_ThrowsResourceNotFoundException() {
        // Arrange
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());
        testCarRequest.setCustomerId(999L);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> carService.createCar(testCarRequest)
        );

        assertTrue(exception.getMessage().contains("Customer not found"));
        verify(customerRepository, times(1)).findById(999L);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void getCarById_WithValidId_ReturnsCar() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        // Act
        CarResponse response = carService.getCarById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(testCar.getId(), response.getId());
        assertEquals(testCar.getLicensePlate(), response.getLicensePlate());
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void getCarById_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> carService.getCarById(999L)
        );

        assertTrue(exception.getMessage().contains("Car not found"));
        verify(carRepository, times(1)).findById(999L);
    }

    @Test
    void getAllCars_ReturnsListOfCars() {
        // Arrange
        Car car2 = new Car();
        car2.setId(2L);
        car2.setLicensePlate("EF-456-GH");
        car2.setBrand("Honda");
        car2.setModel("Civic");
        car2.setYear(2021);
        car2.setCustomer(testCustomer);

        when(carRepository.findAll()).thenReturn(Arrays.asList(testCar, car2));

        // Act
        List<CarResponse> responses = carService.getAllCars();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("AB-123-CD", responses.get(0).getLicensePlate());
        assertEquals("EF-456-GH", responses.get(1).getLicensePlate());
        verify(carRepository, times(1)).findAll();
    }

    @Test
    void getAllCars_WithEmptyDatabase_ReturnsEmptyList() {
        // Arrange
        when(carRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<CarResponse> responses = carService.getAllCars();

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(carRepository, times(1)).findAll();
    }

    @Test
    void updateCar_WithValidData_ReturnsUpdatedCar() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        CarRequest updateRequest = new CarRequest();
        updateRequest.setLicensePlate("XY-999-ZZ");
        updateRequest.setBrand("Ford");
        updateRequest.setModel("Focus");
        updateRequest.setYear(2022);
        updateRequest.setCustomerId(1L);

        Car updatedCar = new Car();
        updatedCar.setId(1L);
        updatedCar.setLicensePlate("XY-999-ZZ");
        updatedCar.setBrand("Ford");
        updatedCar.setModel("Focus");
        updatedCar.setYear(2022);
        updatedCar.setCustomer(testCustomer);

        when(carRepository.save(any(Car.class))).thenReturn(updatedCar);

        // Act
        CarResponse response = carService.updateCar(1L, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("XY-999-ZZ", response.getLicensePlate());
        assertEquals("Ford", response.getBrand());
        assertEquals("Focus", response.getModel());
        assertEquals(2022, response.getYear());
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void updateCar_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> carService.updateCar(999L, testCarRequest)
        );

        assertTrue(exception.getMessage().contains("Car not found"));
        verify(carRepository, times(1)).findById(999L);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void deleteCar_WithValidId_DeletesCar() {
        // Arrange
        when(carRepository.existsById(1L)).thenReturn(true);
        doNothing().when(carRepository).deleteById(1L);

        // Act
        carService.deleteCar(1L);

        // Assert
        verify(carRepository, times(1)).existsById(1L);
        verify(carRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCar_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        when(carRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> carService.deleteCar(999L)
        );

        assertTrue(exception.getMessage().contains("Car not found"));
        verify(carRepository, times(1)).existsById(999L);
        verify(carRepository, never()).deleteById(999L);
    }
}
