package work.onss.vo;

import lombok.Data;

import java.util.Set;

@Data
public class PasswordVo {
    private Set<String> password;
    private String oldPassword;
}
