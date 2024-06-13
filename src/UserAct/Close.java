package UserAct;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

//登出
@WebServlet("/UserAct.Close")
public class Close implements Servlet{
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
        if(session == null || session.getAttribute("username") == null){
            System.out.println("NullSession");
            return;
        }
        else System.out.println("YES");
        String username = (String)session.getAttribute("username");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try{
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HTTPServer", "root", "lcy333668");
            stmt = conn.createStatement();

            rs = stmt.executeQuery("select userid from users where username = '" + username + "'");
            rs.next();
            int userid = rs.getInt(1);

            stmt.executeUpdate("delete from users where username = '" + username + "'");
            stmt.execute("drop table e" + userid);

            session.invalidate();
            PrintWriter out = response.getWriter();
            out.print("""               
                    <html>
                        <head>
                            <meta charset="utf-8"/>
                            <title>注销成功</title>
                        </head>
                        <body>
                            <h1>账号已注销！</h1>
                            <a href="/crm/Main">回到主页面</a>
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
        return "UserAct.Close";
    }

    @Override
    public void destroy(){

    }
}