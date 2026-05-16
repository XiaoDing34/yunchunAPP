package edu.tianjinagriculture.yunchunapp.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import edu.tianjinagriculture.yunchunapp.VO.LoginVO;
import edu.tianjinagriculture.yunchunapp.VO.UserInfoVO;
import edu.tianjinagriculture.yunchunapp.VO.UserPhoneVo;
import edu.tianjinagriculture.yunchunapp.converter.UserConverter;
import edu.tianjinagriculture.yunchunapp.dto.FeedbackDTO;
import edu.tianjinagriculture.yunchunapp.dto.UserLoginDTO;
import edu.tianjinagriculture.yunchunapp.dto.UserRegisterDTO;
import edu.tianjinagriculture.yunchunapp.dto.WxLoginInfoDTO;
import edu.tianjinagriculture.yunchunapp.entity.Feedback;
import edu.tianjinagriculture.yunchunapp.entity.User;
import edu.tianjinagriculture.yunchunapp.mapper.FeedbackMapper;
import edu.tianjinagriculture.yunchunapp.mapper.UserMapper;
import edu.tianjinagriculture.yunchunapp.service.UserService;


import edu.tianjinagriculture.yunchunapp.utils.RedisUtil;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    UserConverter userConverter;

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    FeedbackMapper feedbackMapper;

    @Override
    public LoginVO wechatLogin(String code) {
        try {
            // 一行代码搞定，换取用户信息
            //System.out.println(wxMaService.getUserService().getSessionInfo(code));
            WxMaJscode2SessionResult w=wxMaService.getUserService().getSessionInfo(code);
            System.out.println(w.getOpenid());
            System.out.println(w.getUnionid());
            System.out.println(w.getSessionKey());

            //查询用户是否已经注册
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("openid", w.getOpenid());
            User user=userMapper.selectOne(queryWrapper);
            if (user==null){
                user = new User();
                user.setOpenid(w.getOpenid());
                user.setUnionid(w.getUnionid());
                user.setSessionKey(w.getSessionKey());
                user.setStatus(2);
                userMapper.insert(user);
            }else {
                user.setSessionKey(w.getSessionKey());
                //user.setLastLoginTime(LocalDateTime.now());
                //user.setLastLoginIp(loginIp);
                //user.setUpdateTime(LocalDateTime.now());
                user.setUpdateTime(null);
                userMapper.updateById(user);
            }
            StpUtil.login(user.getUserId());
            UserInfoVO userInfoVO=new UserInfoVO();
            userInfoVO.setNickname(user.getNickname());
            userInfoVO.setUsername(user.getUsername());
            userInfoVO.setAvatar(user.getAvatarUrl());
            userInfoVO.setCreateTime(user.getCreateTime());
            userInfoVO.setOrganization(user.getOrganization());
            userInfoVO.setPosition(user.getPosition());
            userInfoVO.setPhone(user.getPhone());
            LoginVO loginVO=new LoginVO();
            loginVO.setToken(StpUtil.getTokenValue());
            loginVO.setNeedCompleteProfile(user.getStatus()==2);
            //这里我还不想写审核判断，后面会继续完善，默认为false
            loginVO.setNeedAudit(false);
            loginVO.setUserInfo(userInfoVO);
            System.out.println(loginVO);
            return loginVO;
        } catch (WxErrorException e) {
            throw new RuntimeException("微信登录失败: " + e.getMessage());
        }
    }

    //更新用户头像
    @Value("${file.upload-dir}")
    private String uploadDir; // 例如 /data/uploads/
    @Override
    public String updateAvatar(Integer userId, MultipartFile file) {
        // 确保目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();  // 创建多级目录
        }
        // 校验文件类型
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new RuntimeException("只能上传图片");
        }
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = userId + "_" + System.currentTimeMillis() + ext;
        File dest = new File(uploadDir, filename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e);
        }
        String avatarUrl = "/uploads/" + filename; // 访问路径
        //System.out.println(avatarUrl);
        // 更新数据库
        //System.out.println(userId);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).set("avatar_url", avatarUrl);
        userMapper.update(null, updateWrapper);
        //User user = new User();
        //user.setId(userId);
        //user.setAvatarUrl(avatarUrl);

        return avatarUrl;
    }

    //上传用户昵称
    @Override
    public void updateNickname(Integer userId, String nickname) {
//        User user = new User();
//        user.setId(userId);
//        user.setNickname(nickname);
//        userMapper.updateById(user);
        //System.out.println(userId+"-"+nickname);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).set("nickname", nickname);
        userMapper.update(null, updateWrapper);
    }

    @Override
    public String decryptAndBindPhone(Integer userId, String encryptedData, String iv) {
        // 从数据库获取当前用户的 session_key
        User user = userMapper.selectById(userId);
        if (user == null || user.getSessionKey() == null) {
            throw new RuntimeException("用户未登录或session_key已失效");
        }
        String sessionKey = user.getSessionKey();
        // 使用 SDK 解密
        WxMaPhoneNumberInfo phoneInfo = wxMaService.getUserService()
                .getPhoneNoInfo(sessionKey, encryptedData, iv);
        String phone = phoneInfo.getPhoneNumber();
        // 绑定手机号到用户表
        user.setPhone(phone);
        userMapper.updateById(user);
        return phone;
    }

    @Override
    public User getUserByName(String name) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", name);
        User u = userMapper.selectOne(queryWrapper);
        return u;
    }

    @Override
    public User saveAppUser(UserRegisterDTO userRegisterDTO) {
        try {
//            System.out.println("=== 开始保存用户 ===");
//            System.out.println("用户名: " + userRegisterDTO.getUsername());
//            System.out.println("联系方式: " + userRegisterDTO.getPhone());
//            System.out.println("公司: " + userRegisterDTO.getOrganization());
//            System.out.println("职位: " + userRegisterDTO.getPosition());
//            System.out.println("密码: " + (userRegisterDTO.getPassword() != null ? "[已提供]" : "[为空]"));
//            System.out.println("确认密码: " + (userRegisterDTO.getConfirmPassword() != null ? "[已提供]" : "[为空]"));

            // 检查必填字段
            if (userRegisterDTO.getUsername() == null || userRegisterDTO.getUsername().isEmpty()) {
                System.out.println("错误: 用户名不能为空");
                throw new RuntimeException("用户名不能为空");
            }
            if (userRegisterDTO.getPhone() == null || userRegisterDTO.getPhone().isEmpty()) {
                System.out.println("错误: 联系方式不能为空");
                throw new RuntimeException("联系方式不能为空");
            }
            if (userRegisterDTO.getPassword() == null || userRegisterDTO.getPassword().isEmpty()) {
                System.out.println("错误: 密码不能为空");
                throw new RuntimeException("密码不能为空");
            }

            // 检查密码是否一致
            if (userRegisterDTO.getConfirmPassword() != null && !userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
                System.out.println("错误: 两次输入的密码不一致");
                throw new RuntimeException("两次输入的密码不一致");
            }

            // 检查用户名是否已存在
            QueryWrapper<User> existingUserQuery = new QueryWrapper<>();
            existingUserQuery.eq("username", userRegisterDTO.getUsername());
            User existingUser = getOne(existingUserQuery);
            if (existingUser != null) {
                System.out.println("错误: 用户名已存在");
                throw new RuntimeException("用户名已存在");
            }
            System.out.println(userRegisterDTO);
            System.out.println(userRegisterDTO.getPhone());
            System.out.println(userRegisterDTO.getCode());
            String code=(String) redisUtil.get(userRegisterDTO.getPhone());
            if (code==null){
                throw new RuntimeException("验证码已过期");
            }
            if (!code.equals(userRegisterDTO.getCode())) {
                throw new RuntimeException("验证码错误");
            }

//            //将用户密码以SHA-256算法盐值加密后存储
//            System.out.println("开始加密密码...");
//            String salt = userRegisterDTO.getContact() + "YUNCHUNERP";
//            System.out.println("盐值: " + salt);
//            userRegisterDTO.setPassword(new SimpleHash("SHA-256", userRegisterDTO.getPassword(),
//                    ByteSource.Util.bytes(salt), 1024).toString());
//            System.out.println("密码加密完成");

            //记录用户创建时间
            //appUserList.setCreateDate(new Date(System.currentTimeMillis()));
            //System.out.println("设置创建时间: " + appUserList.getCreateDate());

            String pw_hash = BCrypt.hashpw(userRegisterDTO.getPassword(), BCrypt.gensalt());
            userRegisterDTO.setPassword(pw_hash);
            User user=userConverter.toEntity(userRegisterDTO);
            boolean result = save(user);

            // 测试代码：保存后立即查询，验证是否保存成功
            System.out.println("=== 注册测试信息 ===");
            System.out.println("保存结果: " + result);
            System.out.println("保存的用户名: " + user.getUsername());
            System.out.println("保存的联系方式: " + user.getPhone());
            // 查询刚保存的用户
            QueryWrapper<User> testQuery = new QueryWrapper<>();
            testQuery.eq("username", user.getUsername());
            User testUser = getOne(testQuery);
            System.out.println("保存后查询结果: " + (testUser != null ? "存在" : "不存在"));
            if (testUser != null) {
                System.out.println("用户ID: " + testUser.getUserId());
                System.out.println("用户的注册时间: " + testUser.getCreateTime());
            }

            return user;
        } catch (Exception e) {
            System.out.println("保存用户失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public String test(Integer id) {
        return userMapper.selectRoleById(id);
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) {

        String username = userLoginDTO.getUsername();
        User user=getUserByName(username);
        if (user == null) {
            return "用户不存在！";
        }

        //验证密码
        String password = userLoginDTO.getPassword();
        String storedHash=user.getPassword();
        boolean isMatch = BCrypt.checkpw(password, storedHash);
        if (!isMatch) {
            return "密码错误！";
        }
        StpUtil.login(user.getUserId());
        return "登录成功！";
    }

    @Override
    public User updateLoginInfo(Integer userId,WxLoginInfoDTO wxLoginInfoDTO) {
        System.out.println(wxLoginInfoDTO.getPhone());
        System.out.println(wxLoginInfoDTO.getCode());
        String code=(String) redisUtil.get(wxLoginInfoDTO.getPhone());
        System.out.println("存储的code:"+code);
        if (code==null){
            throw new RuntimeException("验证码已过期");
        }
        if (!code.equals(wxLoginInfoDTO.getCode())) {
            throw new RuntimeException("验证码错误");
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).set("organization",wxLoginInfoDTO.getOrganization()).set("position",wxLoginInfoDTO.getPosition()).set("phone",wxLoginInfoDTO.getPhone()).set("status",1);
        userMapper.update(null, updateWrapper);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return getOne(queryWrapper);
    }

    //根据id查询用户信息
    @Override
    public User getUserById(Integer userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public UserInfoVO getUserInfoById(Integer userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        User user = getOne(queryWrapper);
        UserInfoVO userInfoVO=new UserInfoVO();
        userInfoVO.setAvatar(user.getAvatarUrl());
        userInfoVO.setNickname(user.getNickname());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setOrganization(user.getOrganization());
        userInfoVO.setPosition(user.getPosition());
        userInfoVO.setCreateTime(user.getCreateTime());
        userInfoVO.setPhone(user.getPhone());
        return userInfoVO;
    }

    @Override
    public List<UserPhoneVo> getAll(Integer userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<User> list=list(queryWrapper);
        List<UserPhoneVo> userPhoneVoList=new ArrayList<UserPhoneVo>();
        for (User user : list) {
            UserPhoneVo userPhoneVo=new UserPhoneVo();
            if (user.getUsername()==null){
                userPhoneVo.setUsername(user.getNickname());
            }else {
                userPhoneVo.setUsername(user.getUsername());
            }
            userPhoneVo.setPhone(user.getPhone());
            userPhoneVoList.add(userPhoneVo);
        }
        return userPhoneVoList;
    }

    @Override
    public void sendVerifyCode(String phone) {
        System.out.println(phone);
        int appid=1401126809;
        String appKey="0b86bd12083ab92cee68fb7efd4e17b6";
        int templateId=2344121;
        String smsSign="天津云春科技";
        //生成六位验证码，存入redis
        String code= RandomUtil.randomNumbers(6);
        System.out.println(code);
        Boolean flag= redisUtil.set(phone,code,60);
        System.out.println(flag);
        //发送验证码
        try{
            String []params={code,"1"};
            SmsSingleSender smsSingleSender=new SmsSingleSender(appid,appKey);
            SmsSingleSenderResult result=smsSingleSender.sendWithParam("86",phone,templateId,params,smsSign,"","");
            System.out.println(result);
        }catch (HTTPException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public String postFeedback(FeedbackDTO feedbackDTO) {
        Integer userId=StpUtil.getLoginIdAsInt();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        User user=getOne(queryWrapper);
        Feedback feedback=new Feedback();
        //System.out.println(user.getUsername());
        feedback.setUsername(user.getUsername()==null?user.getNickname():user.getUsername());
        //System.out.println(feedbackDTO.getContent());
        feedback.setContent(feedbackDTO.getContent());
        try {
            Integer result = feedbackMapper.insert(feedback);
            System.out.println("result = " + result);
        } catch (Exception e) {
            e.printStackTrace();   // 或 log.error("插入失败", e);
            throw e;               // 仍然抛出，让调用方知道失败
        }
        return "success";

    }
}
