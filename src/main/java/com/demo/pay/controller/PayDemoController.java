package com.demo.pay.controller;

import com.demo.pay.dto.CommonRes;
import com.demo.pay.service.PayDemoService;
import com.demo.pay.utils.ResUtil;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PayDemoController {

    private static final Logger log = LoggerFactory.getLogger(PayDemoController.class);

    @Autowired
    private PayDemoService payDemoService;

    @RequestMapping("/enQrcode")
    public void enQrcode(HttpServletResponse resp, String url) throws IOException {
        if (url != null && !"".equals(url)) {
            ServletOutputStream stream = null;
            try {
                int width = 200;//图片的宽度
                int height = 200;//高度
                stream = resp.getOutputStream();
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix m = writer.encode(url, BarcodeFormat.QR_CODE, height, width);
                MatrixToImageWriter.writeToStream(m, "png", stream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    stream.flush();
                    stream.close();
                }
            }
        }
    }

    @RequestMapping("/deQrcode")
    public CommonRes deQrcode(String base64) {
        if (base64 != null && !"".equals(base64)) {
            try {
                MultiFormatReader multiFormatReader = new MultiFormatReader();
                byte[] bytes1 = Base64.getDecoder().decode(base64);
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);
                BufferedImage image = ImageIO.read(bais);
                //定义二维码参数
                Map hints = new HashMap();
                hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                //获取读取二维码结果
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
                Result result = multiFormatReader.decode(binaryBitmap, hints);

                return ResUtil.success(result.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ResUtil.error();
    }

    @RequestMapping("/deQrcode2")
    public CommonRes deQrcode2(@RequestParam("file") MultipartFile file) {
        if (file != null) {
            try {
                MultiFormatReader multiFormatReader = new MultiFormatReader();
                byte[] bytes1 = file.getBytes();
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);
                BufferedImage image = ImageIO.read(bais);
                //定义二维码参数
                Map hints = new HashMap();
                hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                //获取读取二维码结果
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
                Result result = multiFormatReader.decode(binaryBitmap, hints);

                log.info(result.getText());
                return ResUtil.success(result.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ResUtil.error();
    }

    /**
     * 创建订单
     *
     * @param param 订单保存的信息
     * @param type  支付方式 1|微信 2|支付宝
     * @param price 订单价格
     * @return
     */
    @RequestMapping("/createOrder")
    public CommonRes createOrder(String param, Integer type, String price) {
        if (type == null) {
            return ResUtil.error("请传入支付方式=>1|微信 2|支付宝");
        }
        if (type != 1 && type != 2) {
            return ResUtil.error("支付方式错误=>1|微信 2|支付宝");
        }
        if (price == null) {
            return ResUtil.error("请传入订单金额");
        }
        Double priceD;
        try {
            priceD = Double.valueOf(price);
        } catch (Exception e) {
            return ResUtil.error("请传入订单金额");
        }
        if (priceD < 0) {
            return ResUtil.error("订单金额必须大于0");
        }
        if (param == null) {
            param = "";
        }
        return payDemoService.createOrder(param, type, price);
    }

    @RequestMapping("/closeOrder")
    public CommonRes closeOrder(String orderId) {
        if (orderId == null) {
            return ResUtil.error("请传入云端订单号");
        }
        return payDemoService.closeOrder(orderId);
    }

    @RequestMapping("/getOrder")
    public CommonRes getOrder(String orderId) {
        if (orderId == null) {
            return ResUtil.error("请传入云端订单号");
        }
        return payDemoService.getOrder(orderId);
    }

    @RequestMapping("/checkOrder")
    public CommonRes checkOrder(String orderId) {
        if (orderId == null) {
            return ResUtil.error("请传入云端订单号");
        }
        return payDemoService.checkOrder(orderId);

    }

    @RequestMapping("/callBackOrder")
    public String callBackOrder(String payId, String param, Integer type, Double price, Double reallyPrice, String sign) {
        if (payId == null) {
            return "请传入订单号";
        }
        if (type == null) {
            return "请传入支付方式=>1|微信 2|支付宝";
        }
        if (type != 1 && type != 2) {
            return "支付方式错误=>1|微信 2|支付宝";
        }
        if (price == null || reallyPrice == null) {
            return "请传入订单金额与实际支付金额";
        }
        if (price < 0 || reallyPrice < 0) {
            return "订单金额与实际支付金额必须大于0";
        }
        if (param == null) {
            param = "";
        }
        return payDemoService.callBackOrder(payId, param, type, price, reallyPrice, sign);
    }
}
