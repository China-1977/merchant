package work.onss.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDoto {

    private Resource resource;
    private ResourceCustomer resourceCustomer;
}
