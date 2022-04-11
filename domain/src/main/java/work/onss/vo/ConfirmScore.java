package work.onss.vo;

import lombok.Data;
import work.onss.domain.Address;
import work.onss.domain.Score;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class ConfirmScore implements Serializable {
    @NotBlank(message = "请输入姓名")
    private String username;
    @NotBlank(message = "请输入手机号")
    private String phone;
    @NotBlank(message = "缺少微信服务商APPID")
    private String subAppId;
    @NotEmpty(message = "商品不能为空")
    private List<String> cart;
    @NotNull(message = "请选择配送方式")
    @Enumerated(value = EnumType.STRING)
    private Score.Way way;
    @NotNull(message = "缺少商户ID")
    private Long storeId;
    @Valid
    @NotNull(message = "请选择联系方式")
    private Address address;
}
