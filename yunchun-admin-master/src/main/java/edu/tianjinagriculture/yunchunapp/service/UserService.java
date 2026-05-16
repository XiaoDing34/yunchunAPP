package edu.tianjinagriculture.yunchunapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.tianjinagriculture.yunchunapp.VO.LoginVO;
import edu.tianjinagriculture.yunchunapp.VO.UserInfoVO;
import edu.tianjinagriculture.yunchunapp.VO.UserPhoneVo;
import edu.tianjinagriculture.yunchunapp.dto.*;
import edu.tianjinagriculture.yunchunapp.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface UserService extends IService<User> {
    LoginVO wechatLogin(String code);

    String updateAvatar(Integer userId, MultipartFile file);

    void updateNickname(Integer userId, String nickname);

    String decryptAndBindPhone(Integer userId, String encryptedData, String iv);
    User getUserByName(String name);
//
    User saveAppUser(UserRegisterDTO userRegisterDTO);

    String test(Integer id);

    String login(UserLoginDTO userLoginDTO);

    User updateLoginInfo(Integer userId, WxLoginInfoDTO wxLoginInfoDTO);

    User getUserById(Integer userId);

    UserInfoVO getUserInfoById(Integer userId);

    List<UserPhoneVo> getAll(Integer userId);

    void sendVerifyCode(String pone);

    String postFeedback(FeedbackDTO feedbackDTO);
}
