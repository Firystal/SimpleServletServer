package UserAct;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

//登出
@WebServlet("/UserAct.AddEvent")
public class AddEvent implements Servlet{
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

            String year = request.getParameter("year");
            String month = request.getParameter("month");
            String date = request.getParameter("date");
            String event = request.getParameter("event");

            String username = (String)session.getAttribute("username");
            rsId = stmt.executeQuery("select userid from users where username = '" + username + "'");
            rsId.next();
            int userid = rsId.getInt(1);

            if(year == null || year.isEmpty() || month == null || month.isEmpty() || date == null || date.isEmpty()
                || event == null || event.isEmpty()){
                RequestDispatcher dispatcher = request.getRequestDispatcher("/UserAct/NullEvent.html");
                dispatcher.forward(request, response);
            }
            else{
                rsEvent = stmt.executeQuery("select * from e" + userid + " where date = '" + year + "-" + month + "-" + date + "' and info = '" + event + "'");
                if(rsEvent.next()){
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/UserAct/SameEvent.html");
                    dispatcher.forward(request, response);
                }
                else{
                    stmt.executeUpdate("insert into e" + userid + " values('" + year + "-" + month + "-" + date + "', '" + event + "')");
                    stmt.executeUpdate("update users set eventCnt = eventCnt + 1 where username = '" + username + "'");
                }

                ((HttpServletResponse)response).sendRedirect("/crm/Events");
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
        return "UserAct.AddEvent";
    }

    @Override
    public void destroy(){

    }
}