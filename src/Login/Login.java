package Login;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

//登录处理
@WebServlet("/Login.Login")
public class Login implements Servlet{
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
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try{
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HTTPServer", "root", "lcy333668");
            stmt = conn.createStatement();

            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;UTF-8");

            String username = request.getParameter("username");
            String password = request.getParameter("password");

            RequestDispatcher dispatcher = null;
            if(username == null || username.isEmpty())
                dispatcher = request.getRequestDispatcher("/Login/NullUsername.html");
            else if(password == null || password.isEmpty())
                dispatcher = request.getRequestDispatcher("/Login/NullPassword.html");
            else{
                rs = stmt.executeQuery("select password from users where username = '" + username + "' and password = '" + password + "'");
                if(rs.next()){
                    HttpSession session = ((HttpServletRequest)request).getSession();
                    session.setAttribute("username", username);

                    ((HttpServletResponse)response).sendRedirect("/crm/Main");
                }
                else{
                    dispatcher = request.getRequestDispatcher("/Login/Incorrect.html");
                    dispatcher.forward(request, response);
                }
            }

            if(dispatcher != null) dispatcher.forward(request, response);

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            if(conn != null)
                try{
                    conn.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            if(stmt != null)
                try{
                    stmt.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            if(rs != null)
                try{
                    rs.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
        }
    }

    @Override
    public String getServletInfo(){
        return "Login.Login handling form submissions";
    }

    @Override
    public void destroy(){

    }
}