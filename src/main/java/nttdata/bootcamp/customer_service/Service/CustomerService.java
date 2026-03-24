package nttdata.bootcamp.customer_service.Service;

import com.bank.customer.model.Customer;
import com.bank.customer.model.CustomerRequest;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * Reactive contract for customer CRUD and cache-backed reads.
 */
public interface CustomerService {

    /**
     * Lists customers according to repository rules (e.g. by status).
     *
     * @return all matching customers as API models
     */
    Single<List<Customer>> findAll();

    /**
     * Persists a new customer.
     *
     * @param request creation payload
     * @return created customer
     */
    Single<Customer> createCustomer(CustomerRequest request);

    /**
     * Loads a customer by id (may use Redis cache).
     *
     * @param id customer identifier
     * @return customer if present
     */
    Maybe<Customer> findCustomerById(String id);

    /**
     * Updates fields from the request onto the stored customer.
     *
     * @param id customer identifier
     * @param request update payload
     * @return updated customer
     */
    Single<Customer> updateCustomer(String id, CustomerRequest request);

    /**
     * Performs a logical delete for the customer.
     *
     * @param id customer identifier
     * @return completion signal
     */
    Completable deleteCustomer(String id);

}
