package life.majiang.community.community.controller;

import life.majiang.community.community.Provider.GitHubProvider;
import life.majiang.community.community.dto.AccessTokenDTO;
import life.majiang.community.community.dto.GithubUser;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired(required = false)
    private UserMapper userMapper;
    @Autowired
    private GitHubProvider GitHubProvider;
    //常量配置在文件中
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.client.uri}")
    private String clientUri;

    @GetMapping("/callback")

    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletRequest request){
        System.out.println("111122");
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setRedirect_uri(clientUri);
        accessTokenDTO.setState(state);
        String accessToken = GitHubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = GitHubProvider.getUser(accessToken);
        System.out.println("1111");
        if (githubUser != null) {
            User user = new User();
            user.setToken(UUID.randomUUID().toString());
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            System.out.println("main方法==========执行开始2");
            userMapper.insert(user);
            System.out.println("main方法==========执行开始3");
            //存入session
            request.getSession().setAttribute("user",githubUser);
        }
        return "redirect:/";

    }
    @GetMapping("/lists")
    public void lists(){
       System.out.println("22222");
    }
}
