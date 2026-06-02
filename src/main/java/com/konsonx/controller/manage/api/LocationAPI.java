package com.konsonx.controller.manage.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.konsonx.po.Location;
import com.konsonx.po.NavLocation;
import com.konsonx.service.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping(value = "/api/manage")
@Controller
public class LocationAPI {
    @Resource(name = "LocationService")
    private LocationService locationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/locations")
    @ResponseBody
    public ObjectNode getLocations(
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "14") Integer pageSize,
            @RequestParam(name = "locationCity", required = false) String city,
            @RequestParam(name = "locationDistrict", required = false) String district,
            @RequestParam(name = "locationAddress", required = false) String address) {

        ObjectNode resultJsonObject = objectMapper.createObjectNode();

        if (city == null || "".equals(city)) city = null;
        if (district == null || "".equals(district)) district = null;
        if (address == null || "".equals(address)) address = null;

        PageHelper.startPage(pageNum, pageSize);
        List<Location> locations = locationService.selectByCityAndDistrictAndAddress(city, district, address, pageNum, pageSize);

        PageInfo<Location> pageInfo = new PageInfo<>(locations);

        ObjectNode data = objectMapper.createObjectNode();
        try {
            data.putPOJO("list", locations);
        } catch (Exception e) {
            try {
                data.put("list", objectMapper.writeValueAsString(locations));
            } catch (Exception ex) {
                data.put("list", "[]");
            }
        }
        data.put("total", pageInfo.getTotal());
        data.put("pageNum", pageInfo.getPageNum());
        data.put("pageSize", pageInfo.getPageSize());

        resultJsonObject.put("code", 1);
        resultJsonObject.put("msg", "获取成功");
        resultJsonObject.set("data", data);
        return resultJsonObject;
    }

    @GetMapping("/locations/alias/{locationAlias}")
    @ResponseBody
    public ObjectNode getLocationsByAlias(@RequestParam("pageNum") Integer pageNum,
                                          @PathVariable("locationAlias") String alias) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        if (pageNum != null && alias != null && !"".equals(alias)) {
            List<Location> locations = locationService.selectByAlias(alias, pageNum, 14);
            if (locations != null) {
                resultJsonObject.put("code", 1);
                resultJsonObject.put("msg", "获取成功。");
                try {
                    resultJsonObject.put("data", objectMapper.writeValueAsString(locations));
                } catch (Exception e) {
                    resultJsonObject.put("data", locations.toString());
                }
            } else {
                resultJsonObject.put("code", 0);
                resultJsonObject.put("msg", "获取失败。");
                resultJsonObject.put("data", "null");
            }
        } else {
            resultJsonObject.put("code", -1);
            resultJsonObject.put("msg", "参数有误。");
            resultJsonObject.put("data", "null");
        }
        return resultJsonObject;
    }

    @GetMapping("/locations/{locationId}")
    @ResponseBody
    public ObjectNode getLocation(@PathVariable("locationId") Integer locationId) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        if (locationId != null && locationId > 0) {
            Location location = locationService.selectById(locationId);
            if (location != null) {
                ArrayNode jsonArray = objectMapper.createArrayNode();
                try {
                    jsonArray.add(objectMapper.valueToTree(location));
                } catch (Exception e) {
                    jsonArray.add(location.toString());
                }
                resultJsonObject.put("code", 1);
                resultJsonObject.put("msg", "获取成功。");
                resultJsonObject.set("data", jsonArray);
            } else {
                resultJsonObject.put("code", 0);
                resultJsonObject.put("msg", "获取失败。");
                resultJsonObject.put("data", "null");
            }
        } else {
            resultJsonObject.put("code", -1);
            resultJsonObject.put("msg", "参数有误。");
            resultJsonObject.put("data", "null");
        }
        return resultJsonObject;
    }

    @PostMapping("/locations/{locationId}")
    @ResponseBody
    public ObjectNode updateLocation(@PathVariable("locationId") Integer locationId,
                                     @RequestParam(name = "address", required = false) String address,
                                     @RequestParam(value = "alias", required = false) String alias,
                                     @RequestParam(value = "amount", required = false) Integer amount,
                                     @RequestParam(value = "available", required = false) Integer available,
                                     @RequestParam(value = "district", required = false) String district,
                                     @RequestParam(value = "longitude", required = false) Double longitude,
                                     @RequestParam(value = "latitude", required = false) Double latitude,
                                     @RequestParam(value = "_action", required = false) String action) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();

        if ("delete".equalsIgnoreCase(action)) {
            if (locationId != null && locationId > 0) {
                try {
                    boolean result = locationService.delete(locationId);
                    if (result) {
                        resultJsonObject.put("code", 1);
                        resultJsonObject.put("msg", "删除成功。");
                        resultJsonObject.put("data", "null");
                    } else {
                        resultJsonObject.put("code", 0);
                        resultJsonObject.put("msg", "删除失败。");
                        resultJsonObject.put("data", "null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    resultJsonObject.put("code", 0);
                    resultJsonObject.put("msg", "删除失败：" + e.getMessage());
                    resultJsonObject.put("data", "null");
                }
            } else {
                resultJsonObject.put("code", -1);
                resultJsonObject.put("msg", "参数有误。");
                resultJsonObject.put("data", "null");
            }
            return resultJsonObject;
        }

        if (locationId != null && locationId > 0) {
            Location location = locationService.selectById(locationId);
            NavLocation navLocation = new NavLocation();
            navLocation.setId(location.getLocation_yun_id());
            if (address != null && !"".equals(address)) {
                location.setLocation_address(address);
            }
            if (district != null && !"".equals(district)) {
                location.setLocation_district(district);
            }
            navLocation.setAddress(
                    location.getLocation_city() + location.getLocation_district() + location.getLocation_address());
            if (alias != null && !"".equals(alias)) {
                location.setLocation_alias(alias);
                navLocation.setName(alias);
            }
            if (amount != null && amount > 0) {
                location.setLocation_amount(amount);
            }
            if (available != null && available >= 0) {
                location.setLocation_available(available);
            }
            if (longitude != null) {
                location.setLocation_longitude(String.format("%.6f", longitude));
            }
            if (latitude != null) {
                location.setLocation_latitude(String.format("%.6f", latitude));
            }

            try {
                if (locationService.update(location, navLocation)) {
                    ArrayNode jsonArray = objectMapper.createArrayNode();
                    jsonArray.add(objectMapper.valueToTree(location));
                    resultJsonObject.put("code", 1);
                    resultJsonObject.put("msg", "更新成功。");
                    resultJsonObject.set("data", jsonArray);
                } else {
                    resultJsonObject.put("code", 0);
                    resultJsonObject.put("msg", "更新失败。");
                    resultJsonObject.put("data", "null");
                }
            } catch (Exception e) {
                e.printStackTrace();
                resultJsonObject.put("code", 0);
                resultJsonObject.put("msg", e.getMessage());
                resultJsonObject.put("data", "null");
            }
        } else {
            resultJsonObject.put("code", -1);
            resultJsonObject.put("msg", "参数有误。");
            resultJsonObject.put("data", "null");
        }
        return resultJsonObject;
    }

    @GetMapping("/locations/test")
    @ResponseBody
    public ObjectNode testEndpoint() {
        System.out.println("========== TEST ENDPOINT REACHED ==========");
        ObjectNode result = objectMapper.createObjectNode();
        result.put("code", 1);
        result.put("msg", "Test successful");
        result.put("data", "null");
        return result;
    }

    @GetMapping("/locations/testjson")
    @ResponseBody
    public String testJsonEndpoint() {
        System.out.println("========== TEST JSON ENDPOINT ==========");
        ObjectNode obj = objectMapper.createObjectNode();
        obj.put("code", 1);
        obj.put("msg", "Test message with some Chinese 中文");
        obj.put("data", "some data");
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    @GetMapping("/locations/delete/{locationId}")
    @ResponseBody
    public ObjectNode deleteLocationByGet(@PathVariable("locationId") Integer locationId) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        if (locationId != null && locationId > 0) {
            try {
                boolean result = locationService.delete(locationId);
                if (result) {
                    resultJsonObject.put("code", 1);
                    resultJsonObject.put("msg", "删除成功。");
                    resultJsonObject.put("data", "null");
                } else {
                    resultJsonObject.put("code", 0);
                    resultJsonObject.put("msg", "删除失败。");
                    resultJsonObject.put("data", "null");
                }
            } catch (Exception e) {
                e.printStackTrace();
                resultJsonObject.put("code", 0);
                resultJsonObject.put("msg", "删除失败：" + e.getMessage());
                resultJsonObject.put("data", "null");
            }
        } else {
            resultJsonObject.put("code", -1);
            resultJsonObject.put("msg", "参数有误。");
            resultJsonObject.put("data", "null");
        }
        return resultJsonObject;
    }

    @PutMapping("/locations")
    @ResponseBody
    public ObjectNode addLocation(@RequestParam(name = "city") String city,
                                  @RequestParam(name = "district") String district,
                                  @RequestParam(name = "address", required = false) String address,
                                  @RequestParam("alias") String alias,
                                  @RequestParam("amount") Integer amount,
                                  @RequestParam("bylocation") boolean bylocation,
                                  @RequestParam(value = "longitude", required = false) String longitude,
                                  @RequestParam(value = "latitude", required = false) String latitude) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();

        try {
            if (city == null || "".equals(city) || district == null || "".equals(district) || address == null
                    || "".equals(address) || alias == null || "".equals(alias) || amount == null || amount == 0) {
                resultJsonObject.put("code", -1);
                resultJsonObject.put("msg", "参数校验失败");
                resultJsonObject.put("data", "null");
            } else {
                Location location = new Location();
                location.setLocation_city(city);
                location.setLocation_district(district);
                location.setLocation_address(address);
                location.setLocation_amount(amount);
                location.setLocation_alias(alias);
                location.setLocation_available(0);

                if (!bylocation) {
                    boolean result = locationService.insertByAddress(location);
                    resultJsonObject.put("code", result ? 1 : 0);
                    resultJsonObject.put("msg", result ? "增加成功。" : "增加失败。");
                    resultJsonObject.put("data", "null");
                } else if (longitude != null && !"".equals(longitude) && latitude != null && !"".equals(latitude)) {
                    NavLocation navLocation = new NavLocation();
                    navLocation.setAvailable(location.getLocation_available());
                    navLocation.setAddress(location.getLocation_city() + location.getLocation_district() + location.getLocation_address());
                    navLocation.setLongitude(longitude);
                    navLocation.setLatitude(latitude);
                    navLocation.setName(location.getLocation_alias());
                    boolean result = locationService.insertByLocation(location, navLocation);
                    resultJsonObject.put("code", result ? 1 : 0);
                    resultJsonObject.put("msg", result ? "增加成功。" : "增加失败。");
                    resultJsonObject.put("data", "null");
                } else {
                    resultJsonObject.put("code", -1);
                    resultJsonObject.put("msg", "参数校验失败：缺少经纬度信息");
                    resultJsonObject.put("data", "null");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultJsonObject.put("code", 0);
            resultJsonObject.put("msg", "增加失败：" + e.getMessage());
            resultJsonObject.put("data", "null");
        }
        return resultJsonObject;
    }
}