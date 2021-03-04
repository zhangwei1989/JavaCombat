package org.combat.projects.user.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.combat.projects.user.domain.User;
import org.combat.projects.user.service.UserService;
import org.combat.projects.user.service.impl.UserServiceImpl;
import org.combat.web.mvc.controller.PageController;

/**
 * 用户管理控制器
 */
@Path("/user")
public class UserController implements PageController  {

    private UserService userService = new UserServiceImpl();

    @GET
    @POST
    @Path("/register")
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        System.out.println(request);

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.printf("username: %s, password: %s \n", username, password);

        if (username == null || password == null) {
            return "register.jsp";
        }

        if (userService.register(new User(username, password))) {
            return "success.jsp";
        }

        return "failed.jsp";
    }
}
