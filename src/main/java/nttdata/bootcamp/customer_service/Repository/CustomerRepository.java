package nttdata.bootcamp.customer_service.Repository;

import nttdata.bootcamp.customer_service.Entity.CustomerDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * Reactive MongoDB repository for {@link CustomerDocument}.
 */
public interface CustomerRepository extends ReactiveMongoRepository<CustomerDocument, String> {

    /**
     * Finds customers by status (e.g. ACTIVE).
     *
     * @param status status filter
     * @return matching customers
     */
    Flux<CustomerDocument> findByStatus(String status);
}
