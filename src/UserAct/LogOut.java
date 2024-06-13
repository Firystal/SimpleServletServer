package UserAct;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;

//登出
@WebServlet("/UserAct.LogOut")
public class LogOut implements Servlet{
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

        HttpSession session = ((HttpServletRequest)request).getSession(false);
        if(session != null){
            session.invalidate();
            ((HttpServletResponse)response).sendRedirect("/crm/Main");
        }
    }

    @Override
    public String getServletInfo(){
        return "UserAct.LogOut";
    }

    @Override
    public void destroy(){

    }
}