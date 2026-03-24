package nttdata.bootcamp.customer_service.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB persistence model for bank customers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class CustomerDocument {
    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String companyName;

    private String documentNumber;
    private String documentType;

    /** Business profile: PERSONAL, ENTERPRISE, VIP, PYME, etc. */
    private String customerProfile;

    /** Legacy field; prefer {@link #customerProfile} for new data. */
    private String customerType;

    private String status;

    private Instant createdAt;
}
