package com.qyd.forum.test;

import com.qyd.core.util.AlarmUtil;
import com.qyd.core.util.EmailUtil;
import com.qyd.core.util.SpringUtil;
import com.qyd.web.SimpleForumApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.internet.MimeMessage;

/**
 * @author 邱运铎
 * @date 2024-04-30 21:04
 */
@Slf4j
@SpringBootTest(classes = SimpleForumApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class EmailTest implements ApplicationContextAware, EnvironmentAware {
    private volatile static ApplicationContext context;
    private volatile static Environment environment;

    @Test
    public void SendEmailTest() {
        String to = environment.getProperty("alarm.user");
        String title = "MySelf Test EmailUtil";
        String content = "Warning!!!  Simple Forum Alarm Email From Application";
        AlarmUtil
        try {
            JavaMailSender javaMailSender = SpringUtil.getBean(JavaMailSender.class);
            MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
            mimeMessageHelper.setFrom("iqiuyunduo@163.com");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(title);
            //邮件内容，第二个参数设置为true，支持html模板
            mimeMessageHelper.setText(content, true);
            // 解决 JavaMailSender no object DCH for MIME type multipart/mixed 问题
            // 详情参考：[Email发送失败问题记录 - 一灰灰Blog](https://blog.hhui.top/hexblog/2021/10/28/211028-Email%E5%8F%91%E9%80%81%E5%A4%B1%E8%B4%A5%E9%97%AE%E9%A2%98%E8%AE%B0%E5%BD%95/)
//            Thread.currentThread().setContextClassLoader(EmailUtil.class.getClassLoader());
            javaMailSender.send(mimeMailMessage);
        } catch (Exception e) {
            log.warn("sendEmail error {}@{}, {}", title, to, e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        EmailTest.context = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        EmailTest.environment = environment;
    }
}
