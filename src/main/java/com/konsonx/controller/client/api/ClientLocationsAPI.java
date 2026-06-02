package com.konsonx.controller.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.konsonx.po.Location;
import com.konsonx.service.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequestMapping(value = "/api/client")
@Controller
public class ClientLocationsAPI {
    @Resource(name = "LocationService")
    private LocationService locationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/locations")
    @ResponseBody
    public ObjectNode getLocations(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize) {

        ObjectNode resultJsonObject = objectMapper.createObjectNode();

        List<Location> locations = locationService.selectByPage(pageNum, pageSize);
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

        resultJsonObject.put("code", 1);
        resultJsonObject.put("msg", "获取成功");
        resultJsonObject.set("data", data);
        return resultJsonObject;
    }

    @GetMapping("/locations/generateMock")
    @ResponseBody
    public ObjectNode generateMockLocations(
            @RequestParam(value = "longitude", required = false) String longitude,
            @RequestParam(value = "latitude", required = false) String latitude) {

        ObjectNode resultJsonObject = objectMapper.createObjectNode();

        double userLng = 116.397428;
        double userLat = 39.90923;

        if (longitude != null && !"".equals(longitude)) {
            userLng = Double.parseDouble(longitude);
        }
        if (latitude != null && !"".equals(latitude)) {
            userLat = Double.parseDouble(latitude);
        }

        String[] stationNames = {"便民服务站", "购物中心", "写字楼大厅", "地铁站出口", "公园入口", "医院门口", "学校教学楼", "社区服务中心", "酒店大堂", "体育馆"};
        String[] addresses = {"东门", "西门", "南门", "北门", "正门", "侧门", "一层大厅", "二层休息区", "地下一层", "停车场"};
        String[] cities = {"北京", "上海", "广州", "深圳"};
        String[] districts = {"朝阳区", "海淀区", "东城区", "西城区", "丰台区", "石景山区"};

        Random random = new Random();
        List<Location> savedLocations = new ArrayList<>();
        int numStations = 8 + random.nextInt(5);

        for (int i = 0; i < numStations; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = 0.005 + random.nextDouble() * 0.02;

            double lng = userLng + Math.cos(angle) * distance;
            double lat = userLat + Math.sin(angle) * distance;

            int total = 6 + random.nextInt(15);
            int available = random.nextInt(total + 1);

            Location location = new Location();
            location.setLocation_city(cities[random.nextInt(cities.length)]);
            location.setLocation_district(districts[random.nextInt(districts.length)]);
            location.setLocation_address(addresses[random.nextInt(addresses.length)]);
            location.setLocation_alias(stationNames[random.nextInt(stationNames.length)]);
            location.setLocation_amount(total);
            location.setLocation_available(available);
            location.setLocation_longitude(String.format("%.6f", lng));
            location.setLocation_latitude(String.format("%.6f", lat));
            location.setLocation_yun_id(0);

            try {
                locationService.insertByAddress(location);
                savedLocations.add(location);
            } catch (Exception e) {
                System.err.println("保存模拟充电桩失败: " + e.getMessage());
            }
        }

        ObjectNode data = objectMapper.createObjectNode();
        try {
            data.putPOJO("list", savedLocations);
        } catch (Exception e) {
            try {
                data.put("list", objectMapper.writeValueAsString(savedLocations));
            } catch (Exception ex) {
                data.put("list", "[]");
            }
        }
        data.put("total", savedLocations.size());
        data.put("generated", true);

        resultJsonObject.put("code", 1);
        resultJsonObject.put("msg", "成功生成并保存 " + savedLocations.size() + " 个模拟充电桩");
        resultJsonObject.set("data", data);
        return resultJsonObject;
    }
}