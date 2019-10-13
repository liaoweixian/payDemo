package com.demo.pay.service;

import com.demo.pay.dto.CommonRes;
import com.demo.pay.utils.HttpRequest;
import com.demo.pay.utils.ResUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class PayDemoService {

    @Value("${payck.url}")
    private String url;

    @Value("${payck.key}")
    private String key;

    @Value("${payck.return.url}")
    private String returnUrl;

    public CommonRes createOrder(String param, Integer type, String price) {
        String payId = "PayCK" + new Date().getTime();
        String sign = HttpRequest.md5(payId + param + type + price + key);
        String params = "payId=" + payId + "&" + "param=" + param + "&" + "type=" + type + "&" + "price=" + price + "&" + "returnUrl=" + returnUrl + "&" + "sign=" + sign;
        String result = HttpRequest.sendPost(url + "createOrder", params);

        if (StringUtils.isEmpty(result)) {
            return ResUtil.error("服务器无响应！");
        }
        return new Gson().fromJson(result, CommonRes.class);
    }

    public CommonRes closeOrder(String orderId) {
        String sign = HttpRequest.md5(orderId + key);
        String params = "orderId=" + orderId + "&" + "sign=" + sign;
        String result = HttpRequest.sendPost(url + "closeOrder", params);

        if (StringUtils.isEmpty(result)) {
            return ResUtil.error("服务器无响应！");
        }
        return new Gson().fromJson(result, CommonRes.class);
    }

    public CommonRes getOrder(String orderId) {
        String params = "orderId=" + orderId;
        String result = HttpRequest.sendPost(url + "getOrder", params);

        if (StringUtils.isEmpty(result)) {
            return ResUtil.error("服务器无响应！");
        }
        return new Gson().fromJson(result, CommonRes.class);
    }

    public CommonRes checkOrder(String orderId) {
        String params = "orderId=" + orderId;
        String result = HttpRequest.sendPost(url + "checkOrder", params);

        if (StringUtils.isEmpty(result)) {
            return ResUtil.error("服务器无响应！");
        }
        return new Gson().fromJson(result, CommonRes.class);
    }

    public String callBackOrder(String payId, String param, Integer type, Double price, Double reallyPrice, String sign) {
        String jssign = HttpRequest.md5(payId + param + type + price + reallyPrice + key);
        if (!jssign.equals(sign)) {
            return "签名校验错误";
        }
        return "success";
    }
}
