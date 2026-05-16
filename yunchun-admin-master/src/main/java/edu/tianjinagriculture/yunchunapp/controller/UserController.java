package edu.tianjinagriculture.yunchunapp.controller;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import edu.tianjinagriculture.yunchunapp.VO.LoginVO;
import edu.tianjinagriculture.yunchunapp.VO.UserInfoVO;
import edu.tianjinagriculture.yunchunapp.VO.UserPhoneVo;
import edu.tianjinagriculture.yunchunapp.commom.Result;
import edu.tianjinagriculture.yunchunapp.dto.*;
import edu.tianjinagriculture.yunchunapp.entity.User;
import edu.tianjinagriculture.yunchunapp.service.UserService;
import edu.tianjinagriculture.yunchunapp.service.impl.WechatLoginServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/appUser")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    WechatLoginServiceImpl wechatLoginService;


    //用户注册
    @PostMapping("/saveAppUser")
    public Result<User> saveAppUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        User user=userService.saveAppUser(userRegisterDTO);
        return Result.success("测试成功",user);
    }
    /**
     * 更新微信登录用户的公司，职位等信息
     */
    @PutMapping("/updateLoginInfo")
    public Result<User> updateLoginInfo(@RequestBody WxLoginInfoDTO wxLoginInfoDTO){
        Integer userId = StpUtil.getLoginIdAsInt();
        User user=userService.updateLoginInfo(userId,wxLoginInfoDTO);
        return Result.success("登录成功！",user);
    }
    /**
     * 微信一键登录用户注册
     */
    @PostMapping("/wechatLogin")
    public Result<LoginVO> wechatLogin(@RequestParam String code) {
        System.out.println("开始调用登录");
        LoginVO loginVO=userService.wechatLogin(code);
        return Result.success("登录成功！",loginVO);
    }
    /**
     * 发送验证码
     */
    @PostMapping("/sendVerifyCode")
    public Result<String> sendVerifyCode(@RequestBody String pone){
        userService.sendVerifyCode(pone);
        return Result.success("发送成功！");
    }
    /**
     * 用户账号密码登录
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        String r=userService.login(userLoginDTO);
        if(r.equals("登录成功！")) {
            return Result.success(StpUtil.getTokenValue());
        }
        return Result.error(r);
    }
    /**
     * 上传头像
     * @param file 上传的文件
     * @param request 用于获取当前登录用户
     * @return 头像URL
     */
    @PostMapping("/avatar")
    //@SaCheckLogin
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Integer userId = StpUtil.getLoginIdAsInt();
        String avatarUrl = userService.updateAvatar(userId, file);
        return Result.success(avatarUrl);
    }

    //更新用户昵称
    @PutMapping("/nickname")
    @SaCheckLogin
    public Result<String> updateNickname(@RequestBody String nickname, HttpServletRequest request) {
        Integer userId = StpUtil.getLoginIdAsInt();
        //String nickname = body.get("nickname");
        System.out.println(userId);
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new RuntimeException("昵称不能为空");
        }
        // 可增加敏感词过滤
        userService.updateNickname(userId, nickname);
        return Result.success("上传成功");
    }

    //更新用户手机号
    @PostMapping("/phone")
    @SaCheckLogin
    public Result<String> bindPhone(@RequestParam String encryptedData, @RequestParam String iv) {
        Integer userId = StpUtil.getLoginIdAsInt();
        //String encryptedData = body.get("encryptedData");
        //String iv = body.get("iv");
        String phone = userService.decryptAndBindPhone(userId, encryptedData, iv);
        return Result.success(phone);
    }

    /**
     * 查询当前登录的用户信息
     */
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        Integer userId = StpUtil.getLoginIdAsInt();
        System.out.println(userService.getUserInfoById(userId));
        return Result.success(userService.getUserInfoById(userId));
    }

    /**
     * 获取所有用户
     * @return
     */
    @SaCheckRole(value = "负责人")
    @GetMapping("/getAll")
    //@SaCheckPermission("user.list")
    public Result<List<UserPhoneVo>> getAll() {
        //List<User> list=userService.list();
        //return Result.success("获取成功",list);
        Integer userId = StpUtil.getLoginIdAsInt();
        return Result.success(userService.getAll(userId));
    }

    @RequestMapping("/wxLogin")
    public Result<WxMaJscode2SessionResult> wxLogin(@RequestParam String code){
        WxMaJscode2SessionResult s1=wechatLoginService.code2Session(code);
        System.out.println(s1);
        return Result.success("微信登录成功！",s1);
    }


    @RequestMapping("/feedback")
    public Result<String> postFeedback(@RequestBody FeedbackDTO feedbackDTO){
        System.out.println(feedbackDTO);
        return Result.success(userService.postFeedback(feedbackDTO));
    }

    // 登录接口
    @RequestMapping("/doLogin")
    public Result<SaTokenInfo> doLogin(Integer userId) {
        // 第1步，先登录上
        StpUtil.login(userId);
        // 第2步，获取 Token  相关参数
        SaTokenInfo SaTokenInfo = StpUtil.getTokenInfo();
        // 第3步，返回给前端
        return Result.success(SaTokenInfo);
    }

    // 查询登录状态
    @RequestMapping("/isLogin")
    public String isLogin() {
        return "当前会话是否登录：" + StpUtil.isLogin();
    }

    /**
     * 退出登录，清除token
     */
    @RequestMapping("/logout")
    public Result<String> logout() {
        StpUtil.logout();
        return Result.success("退出登录！");
    }

    /**
     * 测试
     */
    @SaCheckRole(value = "超级管理员")
    //@SaCheckPermission("get")
    @GetMapping("/test")
    public Result<String> test(Integer id) {
        //List<String> permission=userService.test(id);
        //System.out.println(permission);
        return Result.success("测试成功");
    }
}
