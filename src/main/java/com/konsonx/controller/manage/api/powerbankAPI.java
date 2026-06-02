package com.konsonx.controller.manage.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.konsonx.po.Pobk;
import com.konsonx.service.PobkService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping(value = "/api/manage")
@Controller
public class powerbankAPI {
    @Resource(name = "PobkService")
    private PobkService pobkService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Integer pageSize = 14;

    @GetMapping("/powerbanks")
    @ResponseBody
    public ObjectNode getPowerBanks(@RequestParam("pageNum") Integer pageNum,
            @RequestParam(value = "pobk_location_id", required = false) Integer locationId) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        int code = 0;
        Object data = null;
        String msg = null;
        if (pageNum != null && pageNum > 0) {
            List<Pobk> list = null;
            if (locationId != null && locationId > 0) {
                list = pobkService.selectByLocation(locationId, pageNum, pageSize);
            } else {
                list = pobkService.selectByPage(pageNum, pageSize);
            }

            if (list != null) {
                code = 1;
                msg = "获取成功。";
                data = objectMapper.valueToTree(list);
            } else {
                code = 0;
                msg = "获取失败。";
                data = null;
            }
        } else {
            code = -1;
            msg = "参数有误。";
            data = null;
        }
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if (data != null) {
            jsonObject.set("data", objectMapper.valueToTree(data));
        } else {
            jsonObject.putNull("data");
        }
        return jsonObject;
    }

    @PutMapping("/powerbanks")
    @ResponseBody
    public ObjectNode putPowerBanks(@RequestParam(value = "pobk_location_id") Integer locationId,
            @RequestParam("pobk_amount") Integer addAmount) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        int code = 0;
        Object data = null;
        String msg = null;
        Pobk pobk = new Pobk();
        pobk.setPobk_status("available");
        pobk.setPobk_location_id(locationId);
        try {
            if (pobkService.insert(pobk, addAmount)) {
                code = 1;
                msg = "投放成功。";
                data = null;
            } else {
                code = 0;
                msg = "投放失败。";
                data = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            code = -1;
            msg = e.getMessage();
            data = null;
        }
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if (data != null) {
            jsonObject.put("data", data.toString());
        } else {
            jsonObject.put("data", "null");
        }
        return jsonObject;
    }

    @GetMapping("/powerbanks/{pobk_id}")
    @ResponseBody
    public ObjectNode getPowerBank(@PathVariable("pobk_id") Integer pobkId) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        int code = 0;
        Object data = null;
        String msg = null;
        if (pobkId != null && pobkId > 0) {
            Pobk pobk = pobkService.selectById(pobkId);
            if (pobk != null) {
                code = 1;
                msg = "获取成功。";
                ArrayNode jsonArray = objectMapper.createArrayNode();
                try {
                    jsonArray.add(objectMapper.valueToTree(pobk));
                } catch (Exception e) {
                    jsonArray.add(pobk.toString());
                }
                data = jsonArray;
            } else {
                code = 0;
                msg = "充电宝不存在。";
                data = null;
            }
        } else {
            code = -1;
            msg = "参数有误。";
            data = null;
        }

        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if (data != null) {
            jsonObject.set("data", objectMapper.valueToTree(data));
        } else {
            jsonObject.putNull("data");
        }
        return jsonObject;
    }

    @PostMapping("/powerbanks/{pobk_id}")
    @ResponseBody
    public ObjectNode updatePowerBank(@PathVariable("pobk_id") Integer pobkId,
            @RequestParam("pobk_location_id") Integer newLocationId) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        int code = 0;
        Object data = null;
        String msg = null;
        if (pobkId != null && pobkId > 0 && newLocationId != null && newLocationId > 0) {
            Pobk pobk = pobkService.selectById(pobkId);
            if (pobk != null) {
                pobk.setPobk_location_id(newLocationId);
                try {
                    if (pobkService.updatewithLocation(pobk)) {
                        code = 1;
                        msg = "转移成功。";
                        data = null;
                    } else {
                        code = 0;
                        msg = "转移失败。";
                        data = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    code = 0;
                    msg = "转移失败。 " + e.getMessage();
                    data = null;
                }

            } else {
                code = 0;
                msg = "充电宝不存在。";
                data = null;
            }
        } else {
            code = -1;
            msg = "参数有误。";
            data = null;
        }

        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if (data != null) {
            jsonObject.set("data", objectMapper.valueToTree(data));
        } else {
            jsonObject.putNull("data");
        }
        return jsonObject;
    }

    @DeleteMapping("/powerbanks/{pobk_id}")
    @ResponseBody
    public ObjectNode deletPowerBank(@PathVariable("pobk_id") Integer pobkId) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        int code = 0;
        Object data = null;
        String msg = null;
        if (pobkId != null && pobkId > 0) {
            Pobk pobk = pobkService.selectById(pobkId);
            if (pobk != null) {
                try {
                    if (pobkService.delete(pobkId)) {
                        code = 1;
                        msg = "删除成功。";
                        data = null;
                    } else {
                        code = 0;
                        msg = "删除失败。";
                        data = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    code = 0;
                    msg = "删除失败。 " + e.getMessage();
                    data = null;
                }
            } else {
                code = 0;
                msg = "充电宝不存在。";
                data = null;
            }
        } else {
            code = -1;
            msg = "参数有误。";
            data = null;
        }

        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if (data != null) {
            jsonObject.put("data", data.toString());
        } else {
            jsonObject.put("data", "null");
        }
        return jsonObject;
    }
}