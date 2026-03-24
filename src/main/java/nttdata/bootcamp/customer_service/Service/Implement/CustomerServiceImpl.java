package nttdata.bootcamp.customer_service.Service.Implement;

import com.bank.customer.model.Customer;
import com.bank.customer.model.CustomerRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;

import nttdata.bootcamp.customer_service.Entity.CustomerDocument;
import nttdata.bootcamp.customer_service.Mapper.CustomerMapper;
import nttdata.bootcamp.customer_service.Repository.CustomerRepository;
import nttdata.bootcamp.customer_service.Service.CustomerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.adapter.rxjava.RxJava3Adapter;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Default {@link CustomerService} with MongoDB persistence and Redis read-through cache.
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${redis.cache.ttl-seconds:900}")
    private long cacheTtlSeconds;

    /**
     * Removes one customer entry from the Redis cache.
     *
     * @param customerId customer identifier
     * @return empty completion when delete finishes
     */
    private Mono<Void> evictCustomerCache(String customerId) {
        String key = "customer:" + customerId;
        return redisTemplate.delete(key).then();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Single<List<Customer>> findAll() {
        return RxJava3Adapter.fluxToFlowable(repository.findByStatus("ACTIVE"))
                .map(mapper::toDTO)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Single<Customer> createCustomer(CustomerRequest request) {
        CustomerDocument doc = mapper.mapToDocument(request);
        return RxJava3Adapter.monoToSingle(repository.save(doc))
                .map(mapper::toDTO)
                .flatMap(customer -> RxJava3Adapter.monoToSingle(evictCustomerCache(customer.getId()).thenReturn(customer)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Maybe<Customer> findCustomerById(String id) {
        String cacheKey = "customer:" + id;
        return RxJava3Adapter.monoToMaybe(
                redisTemplate.opsForValue().get(cacheKey)
                        .flatMap(json -> {
                            try {
                                return Mono.just(objectMapper.readValue(json, Customer.class));
                            } catch (JsonProcessingException e) {
                                return Mono.error(new RuntimeException("Failed to deserialize customer from cache", e));
                            }
                        })
                        .switchIfEmpty(repository.findById(id)
                                .map(mapper::toDTO)
                                .flatMap(dto -> {
                                    try {
                                        String json = objectMapper.writeValueAsString(dto);
                                        return redisTemplate.opsForValue()
                                                .set(cacheKey, json, Duration.ofSeconds(cacheTtlSeconds))
                                                .thenReturn(dto);
                                    } catch (JsonProcessingException e) {
                                        return Mono.error(new RuntimeException("Failed to serialize customer for cache", e));
                                    }
                                }))
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Single<Customer> updateCustomer(String id, CustomerRequest request) {
        return RxJava3Adapter.monoToMaybe(repository.findById(id))
                .switchIfEmpty(Single.error(new RuntimeException("Customer not found")))
                .flatMap(doc -> {
                    mapper.updateDocument(doc, request);
                    return RxJava3Adapter.monoToSingle(repository.save(doc));
                })
                .map(mapper::toDTO)
                .flatMap(customer -> RxJava3Adapter.monoToSingle(evictCustomerCache(customer.getId()).thenReturn(customer)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Completable deleteCustomer(String id) {
        return RxJava3Adapter.monoToMaybe(repository.findById(id))
                .switchIfEmpty(Single.error(new RuntimeException("Customer not found")))
                .flatMapCompletable(doc -> {
                    doc.setStatus("INACTIVE");
                    return RxJava3Adapter.monoToCompletable(
                            repository.save(doc).then(evictCustomerCache(id))
                    );
                });
    }
}
