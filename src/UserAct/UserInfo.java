package UserAct;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import jakarta.servlet.http.*;
import java.sql.*;

//用户信息
@WebServlet("/UserAct.UserInfo")
public class UserInfo implements Servlet{
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
        String username = (String)session.getAttribute("username");
        int userid = 0, eventCnt = 0;

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try{
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HTTPServer", "root", "lcy333668");
            stmt = conn.createStatement();

            PrintWriter out = response.getWriter();

            rs = stmt.executeQuery("select userid, eventCnt from users where username = '" + username + "'");
            rs.next();
            userid = rs.getInt(1);
            eventCnt = rs.getInt(2);

            out.print("""
                    <html lang="zh-CN">
                        <head>
                            <meta charset="utf-8"/>
                            <title>用户信息</title>
                        </head>
                        <body>
                            <h1>""" + username + """
                            </h1> <hr>
                            <h2>用户id</h2>""" + userid + """
                            <br>
                            <h2>用户事件数量</h2>""" + eventCnt + """
                            <br><br>
                            <a href="#" onclick="window.history.back(); return false">返回</a>
                        </body>
                    </html>
            """);

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
        return "UserAct.UserInfo";
    }

    @Override
    public void destroy(){

    }
}