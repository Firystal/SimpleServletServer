package Login;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;

//登录界面
@WebServlet("/Login.LoginUI")
public class LoginUI implements Servlet{
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) throws ServletException{
        this.config = config;
    }

    @Override
    public ServletConfig getServletConfig(){
        return config;
    }

    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException{
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;UTF-8");

        RequestDispatcher dispatcher = request.getRequestDispatcher("/Login/Login.html");
        dispatcher.forward(request, response);
    }

    @Override
    public String getServletInfo(){
        return "Login.LoginUI";
    }

    @Override
    public void destroy(){

    }
}