package work.onss.vo;

import lombok.Data;
import work.onss.domain.Address;
import work.onss.domain.Site;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class ConfirmScore implements Serializable {
    @NotBlank(message = "缺少微信服务商APPID")
    private String subAppId;
    @NotEmpty(message = "商品不能为空")
    private List<String> cart;
    @NotNull(message = "请选择配送方式")
    private Boolean delivery;
    @NotNull(message = "缺少商户ID")
    private Long storeId;
    @Valid
    @NotNull(message = "请选择联系方式")
    private Address address;
    @Valid
    @NotNull(message = "请选附近驿站")
    private Site site;
}
