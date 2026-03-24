package nttdata.bootcamp.customer_service.Mapper;

import com.bank.customer.model.Customer;
import com.bank.customer.model.CustomerProfile;
import com.bank.customer.model.CustomerRequest;
import nttdata.bootcamp.customer_service.Entity.CustomerDocument;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Maps between API models and {@link CustomerDocument} persistence entities.
 */
@Component
public class CustomerMapper {

    /**
     * Converts a stored document to the public API {@link Customer} model.
     *
     * @param doc persisted customer
     * @return API customer
     */
    public Customer toDTO(CustomerDocument doc) {

        Customer c = new Customer();

        c.setId(doc.getId());
        c.setFirstName(doc.getFirstName());
        c.setLastName(doc.getLastName());
        c.setCompanyName(doc.getCompanyName());
        c.setDocumentNumber(doc.getDocumentNumber());
        c.setDocumentType(Customer.DocumentTypeEnum.valueOf(doc.getDocumentType()));
        c.setCustomerProfile(resolveProfile(doc));
        c.setStatus(Customer.StatusEnum.valueOf(doc.getStatus()));
        c.setCreatedAt(OffsetDateTime.ofInstant(doc.getCreatedAt(), ZoneOffset.UTC));

        return c;
    }

    /**
     * Migration helper: legacy documents with only {@code customerType} map to a base profile.
     *
     * @param doc persisted customer
     * @return resolved {@link CustomerProfile}
     */
    private CustomerProfile resolveProfile(CustomerDocument doc) {
        if (doc.getCustomerProfile() != null && !doc.getCustomerProfile().isBlank()) {
            return CustomerProfile.fromValue(doc.getCustomerProfile());
        }
        if ("ENTERPRISE".equals(doc.getCustomerType())) {
            return CustomerProfile.ENTERPRISE;
        }
        return CustomerProfile.PERSONAL;
    }

    /**
     * Creates a new document from a create request with generated id and timestamps.
     *
     * @param request API create request
     * @return document ready to persist
     */
    public CustomerDocument mapToDocument(CustomerRequest request) {

        CustomerDocument doc = new CustomerDocument();

        doc.setId(UUID.randomUUID().toString());
        doc.setFirstName(request.getFirstName());
        doc.setLastName(request.getLastName());
        doc.setCompanyName(request.getCompanyName());
        doc.setDocumentNumber(request.getDocumentNumber());
        doc.setDocumentType(request.getDocumentType().getValue());
        doc.setCustomerProfile(request.getCustomerProfile().getValue());
        doc.setCustomerType(null);
        doc.setStatus(request.getStatus() != null ? request.getStatus().getValue() : CustomerRequest.StatusEnum.ACTIVE.getValue());
        doc.setCreatedAt(Instant.now());
        return doc;
    }

    /**
     * Applies update request fields onto an existing document.
     *
     * @param doc document to mutate
     * @param request update payload
     */
    public void updateDocument(CustomerDocument doc, CustomerRequest request) {

        doc.setFirstName(request.getFirstName());
        doc.setLastName(request.getLastName());
        doc.setCompanyName(request.getCompanyName());
        doc.setDocumentNumber(request.getDocumentNumber());
        doc.setDocumentType(request.getDocumentType().getValue());
        doc.setCustomerProfile(request.getCustomerProfile().getValue());
        doc.setCustomerType(null);
        if (request.getStatus() != null) {
            doc.setStatus(request.getStatus().getValue());
        }
    }

}
