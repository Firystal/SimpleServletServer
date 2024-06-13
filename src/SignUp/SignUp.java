package SignUp;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

//注册处理
@WebServlet("/SignUp.SignUp")
public class SignUp implements Servlet{
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
            String confirmPassword = request.getParameter("confirmPassword");

            System.out.println(password + " " + confirmPassword);

            RequestDispatcher dispatcher = null;
            if(username == null || username.isEmpty())
                dispatcher = request.getRequestDispatcher("/SignUp/NullUsername.html");
            else if(!nameIsValid(username, stmt))
                dispatcher = request.getRequestDispatcher("/SignUp/InvalidUsername.html");
            else if(password == null || password.isEmpty())
                dispatcher = request.getRequestDispatcher("/SignUp/NullPassword.html");
            else if(!passwordIsValid(password))
                dispatcher = request.getRequestDispatcher("/SignUp/InvalidPassword.html");
            else if(confirmPassword == null || confirmPassword.isEmpty())
                dispatcher = request.getRequestDispatcher("/SignUp/NullConfirmPassword.html");
            else if(!confirmPassword.equals(password))
                dispatcher = request.getRequestDispatcher("/SignUp/UnequalPassword.html");
            else{
                boolean flag = true;
                rs = stmt.executeQuery("select username from users where username = '" + username + "'");
                while(rs.next())
                    if(username.equals(rs.getString(1))){
                        dispatcher = request.getRequestDispatcher("/SignUp/UserNameExists.html");
                        flag = false;
                        break;
                    }

                if(flag){
                    //添加注册成功的用户，并添加一张表，用来记录该用户的事件
                    stmt.executeUpdate("insert into users(username, password, eventCnt) values('" + username + "', '" + password + "', 0)");
                    rs = stmt.executeQuery("select userid from users where username = '" + username + "' order by userid desc");
                    rs.next();
                    int userid = rs.getInt(1);
                    stmt.execute("drop table if exists e" + userid);
                    stmt.execute(
                            "create table e" + userid + "(" +
                                    "date date not null," +
                                    "info varchar(1000))"
                    );

                    dispatcher = request.getRequestDispatcher("/SignUp/Successfully.html");
                }
            }
            if(dispatcher != null) dispatcher.forward(request, response);

        }catch(SQLException e){
            throw new RuntimeException(e);
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
        return "SignUp.SignUp handling form submissions";
    }

    @Override
    public void destroy(){

    }


    //检查用户名的合法性
    public static boolean nameIsValid(String name, Statement stmt){
        for(char i : name.toCharArray())
            if(i == '\\') return false;

        ResultSet rs =  null;
        try{
            rs = stmt.executeQuery("select length('" + name + "')");
            rs.next();
            if(rs.getInt(1) > 30) return false;
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            if(rs != null)
                try{
                    rs.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
        }

        return true;
    }
    //检查密码的合法性
    public static boolean passwordIsValid(String password){
        if(password.length() < 7 || password.length() > 16) return false;

        for(int i : password.toCharArray())
            if(i == '\\' || (i < 33 || i > 126)) return false;

        return true;
    }
}