package edu.tianjinagriculture.yunchunapp.config;

import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CosConfig {

    //COS容器配置
    @Value("${tencent.cos.access-key}")
    private String accessKey;
    @Value("${tencent.cos.secret-key}")
    private String secretKey;
    @Value("${tencent.cos.region}")
    private String region;

    @Bean
    public COSClient cosClient() {
        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        ClientConfig config = new ClientConfig(new Region(region));
        config.setHttpProtocol(HttpProtocol.https); // 使用 HTTPS
        return new COSClient(cred, config);
    }

    /**
     * [Sa-Token 权限认证] 配置类
     *
     * @author click33
     */
    @Configuration
    public class SaTokenConfigure implements WebMvcConfigurer {

        /**
         * CORS 跨域处理策略
         */
        @Bean
        public SaCorsHandleFunction corsHandle() {
            return (req, res, sto) -> {
                res.
                        // 允许指定域访问跨域资源
                                setHeader("Access-Control-Allow-Origin", "*")
                        // 允许所有请求方式
                        .setHeader("Access-Control-Allow-Methods", "POST, GET,PUT, OPTIONS, DELETE")
                        // 有效时间
                        .setHeader("Access-Control-Max-Age", "3600")
                        // 允许的header参数
                        .setHeader("Access-Control-Allow-Headers", "*");

                // 如果是预检请求，则立即返回到前端
                SaRouter.match(SaHttpMethod.OPTIONS)
                        .free(r -> System.out.println("--------OPTIONS预检请求，不做处理"))
                        .back();
            };
        }
    }
}
