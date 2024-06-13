import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;

//主界面
@WebServlet("/Main")
public class Main implements Servlet{
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

            if(session == null || session.getAttribute("username") == null){
                RequestDispatcher dispatcher = request.getRequestDispatcher("/LogOutMain.html");
                dispatcher.forward(request, response);
            }
            else{
                PrintWriter out = response.getWriter();
                String username = (String)session.getAttribute("username");

                boolean hasEvent = false;
                rsId = stmt.executeQuery("select userid from users where username = '" + username + "'");
                rsId.next();
                int userid = rsId.getInt(1);
                rsEvent = stmt.executeQuery("select date from e" + userid + " order by date");
                if(rsEvent.next()){
                    Date eventDate = rsEvent.getDate(1);
                    Date now = Date.valueOf(LocalDate.now());
                    if(now.equals(eventDate)) hasEvent = true;
                }

                out.print("""
                    <html lang="zh-CN">
                        <head>
                            <meta charset="utf-8"/>
                            <title>日历</title>
                            <style>
                                .right-align{
                                  text-align: right;
                                }
                              </style>
                        </head>
                        <body>
                            <div class="right-align">
                                <strong>你好 <a href="/crm/UserInfo">""" + username + """
                            </a><strong>
                            </div>
                            <br><br><br><br><br><br><br><br>
                            <script type="text/javascript">
                                var date = new Date();
                                var year = date.getFullYear();
                                var month = date.getMonth();
                                var day = date.getDate();
                                var dayOfWeek = date.getDay();
                                var arr = ["日", "一", "二", "三", "四", "五", "六"];
                                document.write("<h1><center>" + year + "年" + (month + 1) + "月" + day + "日</center></h1><br><center><h2>星期" + arr[dayOfWeek] + "</h2></center>");
                            </script>
                                        
                            <br><br><br><br><br><br><br><br>
                            <center>
                                <a href="/crm/LogOut">退出登录</a>&nbsp;&nbsp;&nbsp;
                                <a href="/crm/Close">账号注销</a>&nbsp;&nbsp;&nbsp;
                                <a href="/crm/Events">查看事件""" + (hasEvent ? "（今日有待办）" : "<br>") + """
                            </a>
                            </center>
                        </body>
                    </html>
                    """);
            }
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
            if(rsId != null)
                try{
                    rsId.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            if(rsEvent != null)
                try{
                    rsEvent.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
        }
    }

    @Override
    public String getServletInfo(){
        return "Main";
    }

    @Override
    public void destroy(){

    }
}