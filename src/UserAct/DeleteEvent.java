package UserAct;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

//登出
@WebServlet("/UserAct.DeleteEvent")
public class DeleteEvent implements Servlet{
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
        ResultSet rs = null, rsId = null, rsEvent = null;;

        try{
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HTTPServer", "root", "lcy333668");
            stmt = conn.createStatement();

            HttpSession session = ((HttpServletRequest)request).getSession(false);
            String username = (String)session.getAttribute("username");

            String req = request.getParameter("delete");
            int line = 0;
            if(req == null || req.isEmpty()){
                RequestDispatcher dispatcher = request.getRequestDispatcher("/UserAct/NullDelete.html");
                dispatcher.forward(request, response);
            }
            else{
                line = Integer.parseInt(req);

                rs = stmt.executeQuery("select eventCnt from users where username = '" + username + "'");
                rs.next();
                int eventCnt = rs.getInt(1);

                if(line > eventCnt){
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/UserAct/SegmentationFault.html");
                    dispatcher.forward(request, response);
                }
                else{
                    rsId = stmt.executeQuery("select userid from users where username = '" + username + "'");
                    rsId.next();
                    int userid = rsId.getInt(1);

                    rsEvent = stmt.executeQuery("select * from e" + userid + " order by date");
                    while(line -- > 0) rsEvent.next();
                    String event = rsEvent.getString("info");
                    stmt.executeUpdate("delete from e" + userid + " where info = '" + event + "'");
                    stmt.executeUpdate("update users set eventCnt = eventCnt - 1 where username = '" + username + "'");

                    ((HttpServletResponse)response).sendRedirect("/crm/Events");
                }
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
            if(rs != null)
                try{
                    rs.close();
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
        return "UserAct.DeleteEvent";
    }

    @Override
    public void destroy(){

    }
}