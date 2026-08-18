package com.mall.security;

import com.mall.dto.CaptchaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 图形验证码：生成随机 4 位码，绘制 PNG 图片返回 base64，并存入 Redis（5 分钟有效，一次性使用）
 */
@Service
public class CaptchaService {

    private static final String CAPTCHA_PREFIX = "mall:captcha:";
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public CaptchaVO generate() {
        String code = randomCode(4);
        String key = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(CAPTCHA_PREFIX + key, code, 5, TimeUnit.MINUTES);
        String img = "data:image/png;base64," + Base64.getEncoder().encodeToString(draw(code));
        return CaptchaVO.builder().key(key).img(img).build();
    }

    public boolean validate(String key, String code) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(code)) {
            return false;
        }
        String redisKey = CAPTCHA_PREFIX + key;
        String cached = redisTemplate.opsForValue().get(redisKey);
        if (cached == null) {
            return false;
        }
        redisTemplate.delete(redisKey); // 一次性使用
        return cached.equalsIgnoreCase(code);
    }

    private String randomCode(int len) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt(r.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private byte[] draw(String code) {
        int width = 120;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        Random r = new Random();
        // 背景
        g.setColor(new Color(245, 247, 250));
        g.fillRect(0, 0, width, height);
        // 干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(r.nextInt(200), r.nextInt(200), r.nextInt(200)));
            g.drawLine(r.nextInt(width), r.nextInt(height), r.nextInt(width), r.nextInt(height));
        }
        // 字符
        g.setFont(new Font("Arial", Font.BOLD, 26));
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(r.nextInt(120), r.nextInt(120), r.nextInt(120)));
            g.drawString(String.valueOf(code.charAt(i)), 20 + i * 24, 28);
        }
        g.dispose();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("验证码生成失败", e);
        }
    }
}
