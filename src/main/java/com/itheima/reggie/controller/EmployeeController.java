package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LongSummaryStatistics;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    //员工登录
    @PostMapping("/login")
    public R <Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1将页面提交的密码password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //3 查询失败
        if(emp == null){
            return  R.error("登录失败");
        }
        //4密码错误
        if(!emp.getPassword().equals(password)){
            return  R.error("登录失败");
        }
        //5 员工状态
        if(emp.getStatus() == 0){
            return  R.error("账号已被禁用");
        }
        //登录成功 将员工id存入session 返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    //员工退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        //设置初始密码，进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }
    //员工信息分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    //根据id修改员工账号状态
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        //log.info(employee.toString());
//        Long empId = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        long id = Thread.currentThread().getId();
        log.info("线程id为:{}",id);
        employeeService.updateById(employee);
        return R.success("已修改员工状态");
    }
    //根据id查询员工信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("未找到该员工信息");
    }


}
