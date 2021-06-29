package base;

import com.google.common.collect.Maps;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.NetworkMode;
import com.relevantcodes.extentreports.ReporterType;
import lombok.extern.slf4j.Slf4j; //日志对象
import org.junit.jupiter.api.AfterAll; //它用于表示在当前测试类中的所有测试后应执行注解方法。
import org.junit.jupiter.api.BeforeAll; //它用于表示在当前测试类中的所有测试之前应该执行注解的方法。
import org.junit.jupiter.api.BeforeEach; //它用于表示@Test在当前类中的每个方法之前应该执行注解方法。
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired; //自动依赖注入
import org.springframework.boot.test.context.SpringBootTest; //SpringBootTest模块
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity; //Http？
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate; //RestTemplate类是spring-web模块中进行HTTP访问的REST客户端核心类。RestTemplate请求使用阻塞式IO，适合低并发的应用场景。

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //加载一个EmbeddedWebApplicationContext并提供一个真正的servlet环境。嵌入式servlet容器启动并在随机端口上侦听。加载一个EmbeddedWebApplicationContext并提供一个真正的servlet环境。嵌入式servlet容器启动并在随机端口上侦听。
@Slf4j
public class InitHttpCase {
    //测试报告输出目录
    private static final String REPORTS_LOCATION = "test-reports\\index.html";
    //测试报告对象
    private static ExtentReports extent;
    //主机地址
    protected static String host;
    @LocalServerPort
    protected int port;
    @Autowired
    protected TestRestTemplate restTemplate;
    //注册扩展模型，用于捕获测试状况
    @RegisterExtension
    public DefaultTestWatcher testWatcher = new DefaultTestWatcher(extent);

    //在所有测试之前，执行下面注解的方法，应该是设定了一些报告的基本显示信息
    @BeforeAll
    static void initAll() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            host = inetAddress.getHostAddress();
            Map<String, String> info = Maps.newHashMap();
            info.put("Host Address", host);
            info.put("Host Name", inetAddress.getHostName());
            info.put("User Name", System.getenv().get("USERNAME"));
            extent = new ExtentReports(REPORTS_LOCATION, true, NetworkMode.OFFLINE);
            //生成数据库
            extent.startReporter(ReporterType.DB, REPORTS_LOCATION);
            //系统信息
            extent.addSystemInfo(info);
        } catch (UnknownHostException e) {
            log.error("测试初始化异常", e);
        }
    }

    @BeforeEach
    public void initEach() {
        InitHttpCase.host = String.format("localhost:%s", port);
    }

    @AfterAll
    public static void afterAll() {
        extent.close();
    }


    //使用postForObject请求接口
    public static String defaultPostForObject(RestTemplate template, MultiValueMap<String, Object> paramMap, String url) {
        return template.postForObject(url, paramMap, String.class);
    }

    //使用postForEntity请求接口
    public static String defaultPostForEntity(RestTemplate template, MultiValueMap<String, Object> paramMap, String url) {
        HttpHeaders headers = new HttpHeaders();
        return defaultExchange(template, paramMap, url, headers);
    }

    public static String defaultPostForEntity(RestTemplate template, MultiValueMap<String, Object> paramMap, String url, HttpHeaders headers) {
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(paramMap, headers);
        ResponseEntity<String> response2 = template.postForEntity(url, httpEntity, String.class);
        return response2.getBody();
    }

    //使用exchange请求接口,它可以指定请求的HTTP类型
    public static String defaultExchange(RestTemplate template, MultiValueMap<String, Object> paramMap, String url) {
        HttpHeaders headers = new HttpHeaders();
        return defaultExchange(template, paramMap, url, headers);
    }

    public static String defaultExchange(RestTemplate template, MultiValueMap<String, Object> paramMap, String url, HttpHeaders headers) {
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(paramMap, headers);
        ResponseEntity<String> response3 = template.exchange(url, HttpMethod.POST, httpEntity, String.class);
        return response3.getBody();
    }
}