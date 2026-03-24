package nttdata.bootcamp.customer_service.Controller;

import com.bank.customer.api.CustomersApi;
import com.bank.customer.model.Customer;
import com.bank.customer.model.CustomerRequest;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;

import nttdata.bootcamp.customer_service.Service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Reactive REST controller implementing the OpenAPI-generated customers API.
 */
@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

    private final CustomerService customerService;

    /**
     * Returns all customers (typically filtered by the service layer, e.g. ACTIVE).
     *
     * @return HTTP 200 with list body
     */
    @Override
    public Single<ResponseEntity<List<Customer>>> findAllCustomers() {
        return customerService.findAll()
                .map(ResponseEntity::ok);
    }

    /**
     * Creates a new customer.
     *
     * @param customerRequest creation payload
     * @return HTTP 201 with created customer
     */
    @Override
    public Single<ResponseEntity<Customer>> createCustomer(CustomerRequest customerRequest) {
        return customerService.createCustomer(customerRequest)
                .map(customer -> ResponseEntity.status(HttpStatus.CREATED).body(customer));
    }

    /**
     * Retrieves a customer by id.
     *
     * @param id customer identifier
     * @return HTTP 200 if found, 404 otherwise
     */
    @Override
    public Single<ResponseEntity<Customer>> findCustomerById(String id) {
        return customerService.findCustomerById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing customer.
     *
     * @param id customer identifier
     * @param request update payload
     * @return HTTP 200 with updated customer
     */
    @Override
    public Single<ResponseEntity<Customer>> updateCustomer(String id, CustomerRequest request) {
        return customerService.updateCustomer(id, request)
                .map(ResponseEntity::ok);
    }

    /**
     * Logically deletes a customer (e.g. marks INACTIVE).
     *
     * @param id customer identifier
     * @return HTTP 204 on success
     */
    @Override
    public Single<ResponseEntity<Void>> deleteCustomer(String id) {
        return customerService.deleteCustomer(id)
                .toSingleDefault(ResponseEntity.noContent().<Void>build());
    }
}
