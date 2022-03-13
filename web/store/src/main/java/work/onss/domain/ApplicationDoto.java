package work.onss.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDoto {

    private Application application;
    private ApplicationCustomer applicationCustomer;
}
