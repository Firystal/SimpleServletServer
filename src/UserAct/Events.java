package UserAct;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//登出
@WebServlet("/UserAct.Events")
public class Events implements Servlet{
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

        Connection conn = null;
        Statement stmt = null;
        ResultSet rsId = null, rsEvent = null;

        try{
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HTTPServer", "root", "lcy333668");
            stmt = conn.createStatement();

            HttpSession session = ((HttpServletRequest)request).getSession(false);
            String username = (String)session.getAttribute("username");

            rsId = stmt.executeQuery("select userid from users where username = '" + username + "'");
            rsId.next();
            int userid = rsId.getInt(1);
            rsEvent = stmt.executeQuery("select * from e" + userid + " order by date");

            int eventCnt = 0;
            String result = "";
            Date now = Date.valueOf(LocalDate.now());
            Date lastDate = null;
            List<Date> datesToDelete = new ArrayList<>();

            while(rsEvent.next()){
                boolean flag = true;
                Date eventDate = rsEvent.getDate("date");
                if(eventDate.compareTo(now) < 0){
                    datesToDelete.add(eventDate);
                    flag = false;
                }else if(!eventDate.equals(lastDate)){
                    lastDate = eventDate;
                    if (eventDate.equals(now))
                        result += "<h2>今日</h2>";
                    else result += "<h2>" + eventDate + "</h2>";
                }

                if(flag){
                    eventCnt++;
                    result += "<strong>" + eventCnt + "</strong>&nbsp;&nbsp;&nbsp;" + rsEvent.getString("info") + "<br>";
                }
            }

            for(Date date : datesToDelete){
                int cnt = stmt.executeUpdate("delete from e" + userid + " where date = '" + date + "'");
                stmt.executeUpdate("update users set eventCnt = eventCnt - " + cnt + " where username = '" + username + "'");
            }

            PrintWriter out = response.getWriter();
            out.print("""
                    <html>
                        <head>
                            <meta charset="utf-8"/>
                            <title>事件簿</title>
                        </head>
                        <body>
                            <form action="DeleteEvent" method="post">
                                <label for="username">删除事件（输入事件编号）</label> <br>
                                <input type="text" name="delete"/> <br><br>
                                <input type="submit" value="删除"/> *过期事件会自动删除，添加过期事件是无效添加<hr>
                            </form>
                            <form action="AddEvent" method="post">
                                <label for="username">添加事件</label> <br>
                                
                                <label for="username">日期</label>&nbsp;&nbsp;
                                <input type="text" name="year"/>年&nbsp;
                                <input type="text" name="month"/>月&nbsp;
                                <input type="text" name="date"/>日 <br><br>
                                
                                <textarea rows="10" cols="40" name="event" placeholder="在此输入具体内容"></textarea> <br><br>
                                
                                <input type="submit" value="添加"/><hr>
                            </form>
                            <a href="Main">返回主界面</a> <br><br>
                            <h1>事件簿</h1>""" + result + """
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
            if(rsEvent != null)
                try{
                    rsEvent.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            if(rsId != null)
                try{
                    rsId.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
        }
    }

    @Override
    public String getServletInfo(){
        return "UserAct.Events";
    }

    @Override
    public void destroy(){

    }
}