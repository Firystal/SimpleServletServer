package SignUp;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;

//注册界面
@WebServlet("/SignUp.SignUpUI")
public class SignUpUI implements Servlet{
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

        RequestDispatcher dispatcher = request.getRequestDispatcher("/SignUp/SignUp.html");
        dispatcher.forward(request, response);
    }

    @Override
    public String getServletInfo(){
        return "SignUp.SignUpUI";
    }

    @Override
    public void destroy(){

    }
}