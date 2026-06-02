package com.konsonx.service.imp;

import com.github.pagehelper.PageHelper;
import com.konsonx.dao.LocationMapper;
import com.konsonx.dao.PobkMapper;
import com.konsonx.po.Location;
import com.konsonx.po.NavLocation;
import com.konsonx.po.Pobk;
import com.konsonx.service.LocationService;
// 注释掉 YunTu 导入
// import com.konsonx.utils.YunTu;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service(value = "LocationService")
public class LocationServiceImpl implements LocationService {

    @Resource(name = "LocationMapper")
    private LocationMapper locationMapper;

    @Resource(name = "PobkMapper")
    private PobkMapper pobkMapper;

    // 注释掉 YunTu 注入
    // @Resource(name = "YunTu")
    // private YunTu yunTu;

    @Override
    @Transactional
    public boolean insertByLocation(Location location, NavLocation navLocation) throws Exception {
        try {
            if (location == null)
                return false;
            // 绕过云图，直接插入本地数据库
            location.setLocation_yun_id(-1);
            if (navLocation != null) {
                location.setLocation_longitude(navLocation.getLongitude());
                location.setLocation_latitude(navLocation.getLatitude());
            }
            boolean result = locationMapper.insertSelective(location) > 0;
            System.out.println("数据库插入结果: " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean insertByAddress(Location location) throws Exception {
        try {
            if (location == null)
                throw new Exception("location对象为null");
            // 绕过云图，直接插入本地数据库
            location.setLocation_yun_id(-1);
            boolean result = locationMapper.insertSelective(location) > 0;
            return result;
        } catch (Exception e) {
            throw new Exception("insertByAddress异常: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean delete(Integer locationId) throws Exception {
        Location location = locationMapper.selectByPrimaryKey(locationId);
        if (location == null) {
            throw new Exception("投放点不存在");
        }

        // 检查该投放点下是否有充电宝
        List<Pobk> pobkList = pobkMapper.findbyPobk_location_id(locationId);
        if (pobkList != null && !pobkList.isEmpty()) {
            throw new Exception("该投放点还有 " + pobkList.size() + " 个充电宝，请先转移或移除所有充电宝后再删除");
        }

        return locationMapper.deleteByPrimaryKey(locationId) > 0;
    }

    @Override
    @Transactional
    public boolean update(Location location, NavLocation navLocation) throws Exception {
        if (location == null)
            return false;
        // 只更新本地数据库
        return locationMapper.updateByPrimaryKeySelective(location) > 0;
    }

    @Override
    @Transactional
    public boolean addPowerBank(Integer locationId) throws Exception {
        Location location = selectById(locationId);
        if (location == null) {
            throw new Exception("投放点不存在");
        }
        if (location.getLocation_available() + 1 <= location.getLocation_amount()) {
            location.setLocation_available(location.getLocation_available() + 1);
            return update(location, null);
        } else {
            throw new Exception("投放点容量已满");
        }
    }

    @Override
    @Transactional
    public boolean deductPowerbank(Integer locationId) throws Exception {
        Location location = selectById(locationId);
        if (location == null) {
            throw new Exception("投放点不存在");
        }
        if (location.getLocation_available() - 1 >= 0) {
            location.setLocation_available(location.getLocation_available() - 1);
            return update(location, null);
        } else {
            throw new Exception("投放点可用充电宝不足");
        }
    }

    @Override
    public Location selectById(Integer locationId) {
        return locationMapper.selectByPrimaryKey(locationId);
    }

    @Override
    public Location selectByYunId(Integer yunId) {
        return locationMapper.findOnebyLocation_yun_id(yunId);
    }

    @Override
    public Location selectByIdForUpdate(Integer locationId) {
        return locationMapper.findOneByLocation_idForUpdate(locationId);
    }

    @Override
    public List<Location> selectByCityAndDistrictAndAddress(String city, String district, String address,
                                                            Integer pageNum, Integer pageSize) {
        if (pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return locationMapper.findbyLocation_cityandlocation_districtandlocation_addresscontaining(city, district, address);
    }

    @Override
    public List<Location> selectByAlias(String alias, Integer pageNum, Integer pageSize) {
        if (pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return locationMapper.findbylocation_aliascontaining(alias);
    }

    @Override
    public List<Location> selectByPage(Integer pageNum, Integer pageSize) {
        if (pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return locationMapper.findorderBylocation_iddesc();
    }
}