package nttdata.bootcamp.customer_service.Mapper;

import com.bank.customer.model.Customer;
import com.bank.customer.model.CustomerProfile;
import com.bank.customer.model.CustomerRequest;
import nttdata.bootcamp.customer_service.Entity.CustomerDocument;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for {@link CustomerMapper}: profile resolution, DTO mapping, and document creation.
 */
class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapper();

    /**
     * When {@code customerProfile} is set on the document, it must map to the API profile enum.
     */
    @Test
    void toDTO_usesCustomerProfileWhenPresent() {
        Instant created = Instant.parse("2024-01-15T00:00:00Z");
        CustomerDocument doc = CustomerDocument.builder()
                .id("id-1")
                .firstName("A")
                .lastName("B")
                .documentNumber("123")
                .documentType("DNI")
                .customerProfile("VIP")
                .status("ACTIVE")
                .createdAt(created)
                .build();

        Customer c = mapper.toDTO(doc);
        assertEquals(CustomerProfile.VIP, c.getCustomerProfile());
        assertEquals(Customer.DocumentTypeEnum.DNI, c.getDocumentType());
        assertEquals(OffsetDateTime.ofInstant(created, ZoneOffset.UTC), c.getCreatedAt());
    }

    /**
     * {@link CustomerMapper#mapToDocument} should generate id and persist profile string.
     */
    @Test
    void mapToDocument_buildsNew() {
        CustomerRequest req = new CustomerRequest();
        req.setFirstName("f");
        req.setLastName("l");
        req.setDocumentNumber("d");
        req.setDocumentType(CustomerRequest.DocumentTypeEnum.DNI);
        req.setCustomerProfile(CustomerProfile.PERSONAL);
        req.setStatus(CustomerRequest.StatusEnum.ACTIVE);

        CustomerDocument doc = mapper.mapToDocument(req);
        assertNotNull(doc.getId());
        assertEquals("PERSONAL", doc.getCustomerProfile());
    }

}
