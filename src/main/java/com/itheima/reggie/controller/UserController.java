package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import jdk.nashorn.internal.ir.CallNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    //发送短信验证码
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        //生成随机4位数
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("code="+code);
        //调用阿里云提供的短信服务API完成发送短信
        //SMSUtil.sendMessage("瑞吉外卖","",phone,code);
        // 保存验证码
        session.setAttribute(phone,code);

        return R.success("短信验证码发送成功");
    }

    //移动端用户登录
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从Session中获取验证码
        Object codeInSession = session.getAttribute(phone);
        //进行验证码比对
        if(codeInSession !=null && codeInSession.equals(code)){
            //如果能够比对成功,说明登录成功
            //判断当前手机号对应的用户是否为新用户,完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user =userService.getOne(queryWrapper);
            if(user ==null){
              //新用户注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登陆失败");
    }
}
