import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

//初始化数据库/重构数据库
public class DataBaseInit{
    public static void main(String[] args){
        Connection conn = null;
        Statement stmt = null;
        try{
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "lcy333668");
            stmt = conn.createStatement();

            //重构数据库
            stmt.execute("drop database if exists HTTPServer");
            stmt.execute("create database HTTPServer");
            stmt.execute("use HTTPServer");

            //创建用户表:
            //用户名长度不超过30长度（一个汉字为3个长度），不得含有'\'符号
            //密码7-16位，只允许含有数字、字母和除'\'以外的特殊符号
            stmt.execute("drop table if exists users");
            stmt.execute(
                    "create table users(" +
                            "username varchar(30) not null," +
                            "userid int not null auto_increment," +
                            "password varchar(17) not null," +
                            "eventCnt int not null default 0," +
                            "primary key(userid));"
            );
            stmt.execute("alter table users auto_increment = 10000");

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
        }
    }
}