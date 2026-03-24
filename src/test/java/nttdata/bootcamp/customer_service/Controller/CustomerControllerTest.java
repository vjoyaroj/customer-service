package nttdata.bootcamp.customer_service.Controller;

import com.bank.customer.model.Customer;
import com.bank.customer.model.CustomerProfile;
import com.bank.customer.model.CustomerRequest;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import nttdata.bootcamp.customer_service.Service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CustomerController}: verifies HTTP status mapping when delegating to {@link CustomerService}.
 */
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController controller;

    /**
     * {@code GET} all customers should return 200 OK with the list from the service.
     */
    @Test
    void findAllCustomers_shouldReturnOk() {
        when(customerService.findAll()).thenReturn(Single.just(List.of(new Customer())));

        ResponseEntity<List<Customer>> result = controller.findAllCustomers().blockingGet();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    /**
     * {@code POST} should return 201 Created with the created resource id.
     */
    @Test
    void createCustomer_shouldReturnCreated() {
        CustomerRequest req = new CustomerRequest("DOC-001",
                CustomerRequest.DocumentTypeEnum.DNI,
                CustomerProfile.PERSONAL);
        Customer customer = new Customer();
        customer.setId("cust-1");

        when(customerService.createCustomer(req)).thenReturn(Single.just(customer));

        ResponseEntity<Customer> result = controller.createCustomer(req).blockingGet();

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("cust-1", result.getBody().getId());
    }

    /**
     * {@code GET} by id should return 200 when the customer exists.
     */
    @Test
    void findCustomerById_shouldReturnOkWhenFound() {
        Customer customer = new Customer();
        customer.setId("cust-1");

        when(customerService.findCustomerById("cust-1")).thenReturn(Maybe.just(customer));

        ResponseEntity<Customer> result = controller.findCustomerById("cust-1").blockingGet();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("cust-1", result.getBody().getId());
    }

    /**
     * {@code GET} by id should return 404 when the service returns empty.
     */
    @Test
    void findCustomerById_shouldReturnNotFoundWhenMissing() {
        when(customerService.findCustomerById("missing")).thenReturn(Maybe.empty());

        ResponseEntity<Customer> result = controller.findCustomerById("missing").blockingGet();

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    /**
     * {@code PUT} should return 200 OK on success.
     */
    @Test
    void updateCustomer_shouldReturnOk() {
        CustomerRequest req = new CustomerRequest("DOC-001",
                CustomerRequest.DocumentTypeEnum.DNI,
                CustomerProfile.PERSONAL);
        Customer customer = new Customer();
        customer.setId("cust-1");

        when(customerService.updateCustomer("cust-1", req)).thenReturn(Single.just(customer));

        ResponseEntity<Customer> result = controller.updateCustomer("cust-1", req).blockingGet();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    /**
     * {@code DELETE} should return 204 No Content.
     */
    @Test
    void deleteCustomer_shouldReturnNoContent() {
        when(customerService.deleteCustomer("cust-1")).thenReturn(Completable.complete());

        ResponseEntity<Void> result = controller.deleteCustomer("cust-1").blockingGet();

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
