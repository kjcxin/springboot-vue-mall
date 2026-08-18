package com.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mall.common.BusinessException;
import com.mall.dto.AddressRequest;
import com.mall.entity.Address;
import com.mall.mapper.AddressMapper;
import com.mall.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    public List<Address> list() {
        Long userId = UserContext.getUserId();
        return addressMapper.selectList(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getUpdateTime));
    }

    @Transactional(rollbackFor = Exception.class)
    public Address create(AddressRequest req) {
        Long userId = UserContext.getUserId();
        Address addr = new Address();
        addr.setUserId(userId);
        addr.setReceiverName(req.getReceiverName());
        addr.setReceiverPhone(req.getReceiverPhone());
        addr.setReceiverAddress(req.getReceiverAddress());
        addr.setIsDefault(0);
        addressMapper.insert(addr);
        if (Integer.valueOf(1).equals(req.getIsDefault())) {
            setDefault(addr.getId());
        }
        return addr;
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, AddressRequest req) {
        Address addr = getOwnAddress(id);
        addr.setReceiverName(req.getReceiverName());
        addr.setReceiverPhone(req.getReceiverPhone());
        addr.setReceiverAddress(req.getReceiverAddress());
        addressMapper.updateById(addr);
        if (Integer.valueOf(1).equals(req.getIsDefault())) {
            setDefault(id);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long id) {
        Long userId = UserContext.getUserId();
        Address addr = getOwnAddress(id);
        addressMapper.update(null, new LambdaUpdateWrapper<Address>()
                .eq(Address::getUserId, userId).set(Address::getIsDefault, 0));
        addr.setIsDefault(1);
        addressMapper.updateById(addr);
    }

    public void delete(Long id) {
        Address addr = getOwnAddress(id);
        addressMapper.deleteById(addr.getId());
    }

    private Address getOwnAddress(Long id) {
        Long userId = UserContext.getUserId();
        Address addr = addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, id).eq(Address::getUserId, userId));
        if (addr == null) {
            throw new BusinessException("地址不存在");
        }
        return addr;
    }
}
