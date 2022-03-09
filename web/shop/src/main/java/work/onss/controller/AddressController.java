package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Address;
import work.onss.domain.AddressRepository;
import work.onss.exception.ServiceException;

import java.util.List;

@Log4j2
@RestController
public class AddressController {

    @Autowired
    protected AddressRepository addressRepository;

    /**
     * @param aid     用户ID
     * @param address 编辑内容
     * @return 最新收货地址内容
     */
    @PostMapping(value = {"addresses"})
    public Address saveOrInsert(@RequestHeader(name = "aid") Long aid, @RequestBody @Validated Address address) {
        address.setAccountId(aid);
        if (address.getId() == null) {
            addressRepository.save(address);
        } else {
            Address oldAddress = addressRepository.findByIdAndAccountId(address.getId(), aid).orElseThrow(() -> new ServiceException("FAIL", "该数据不存在,请联系客服", address));
            address.setInsertTime(oldAddress.getInsertTime());
            addressRepository.save(address);
        }
        return address;
    }

    /**
     * @param aid 用户ID
     * @param id  主键
     */
    @DeleteMapping(value = {"addresses/{id}"})
    public void delete(@RequestHeader(name = "aid") Long aid, @PathVariable Long id) {
        addressRepository.deleteByIdAndAccountId(id, aid);
    }

    /**
     * @param aid 用户ID
     * @param id  主键
     * @return 收货地址
     */
    @GetMapping(value = {"addresses/{id}"})
    public Address findOne(@RequestHeader(name = "aid") Long aid, @PathVariable Long id) {
        return addressRepository.findByIdAndAccountId(id, aid).orElse(null);
    }

    /**
     * @param aid 用户ID
     * @return 所有收货地址
     */
    @GetMapping(value = {"addresses"})
    public List<Address> findAll(@RequestHeader(name = "aid") Long aid) {
        return addressRepository.findByAccountIdOrderByUpdateTime(aid);
    }
}
