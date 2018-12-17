package sql;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

public class sql {
    
    // JDBC 驱动名及数据库 URL
    private  final String Class_Name = "com.mysql.jdbc.Driver";  
    private  String DB_URL;
    
    // 数据库的用户名与密码，需要根据自己的设置
    private static final String USER = "shixiuser";
    private static final String PASS = "shixipass";
    
    public Connection connection = null;
    
    public sql(String serverIP){
    	try {
    		DB_URL = "jdbc:mysql://"+serverIP+":3306/shixi";
    		Class.forName(Class_Name);
    		connection = DriverManager.getConnection(DB_URL,USER,PASS);
            //执行SQL指令函数
            System.out.println("SQL连接成功!");
        }  catch (SQLException e) {
        	JOptionPane.showMessageDialog(null, "连接SQL失败！", "错误", JOptionPane.ERROR_MESSAGE);
            System.err.println(e.getMessage());
        } catch(Exception e) {
        	JOptionPane.showMessageDialog(null, "连接SQL失败！", "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    //关闭mysql数据库连接
	public void closeConnection() {
    	 try {
        	 if (connection != null) {
                 connection.close();
                 System.out.println("SQL关闭成功");
             }
         } catch (SQLException e) {
        	 JOptionPane.showMessageDialog(null, "关闭SQL失败！", "错误", JOptionPane.ERROR_MESSAGE);
             System.err.println(e);
         }
     }
     
	private boolean returnExist(String sql,String error) throws SQLException {
        	try {
        		Statement statement = connection.createStatement();
                statement.setQueryTimeout(10); // 将超时设置为10秒.
                // 执行查询语句,并把查询结果返回到rs集中
                ResultSet rs =statement.executeQuery(sql+";");
                //判断是否有值
                if(rs.next()) {
                	return true;
                }else
                	return false;
        	}catch(Exception e) {
        		e.printStackTrace();
    			System.out.print(error+"\n");
        	}
        	return false;
      }
     
	private ResultSet returnRs(String sql,String error) throws SQLException {
      	try {
      		Statement statement = connection.createStatement();
            statement.setQueryTimeout(10); // 将超时设置为10秒.
            // 执行查询语句,并把查询结果返回到rs集中
            ResultSet rs =statement.executeQuery(sql+";");
            if(rs.next())
            	return rs;
            else
            	return null;
      	}catch(Exception e) {
      		e.printStackTrace();
      		System.out.print(error+"\n");
  			return null;
      	}
    }
	
	private List<Map<String, Object>> returnList(String sql,String error) throws SQLException {
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
      	try {
      		Statement statement = connection.createStatement();
            statement.setQueryTimeout(10); // 将超时设置为10秒.
            // 执行查询语句,并把查询结果返回到rs集中
            ResultSet rs =statement.executeQuery(sql+";");
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs.next()) { 
            	Map<String, Object> rowData = new HashMap<String, Object>();
            	for (int i = 1; i <= columnCount; i++) {
            		rowData.put("a", rs.getObject(i));
            	}
            	list.add(rowData);
            }
            return list; 
      	}catch(Exception e) {
      		e.printStackTrace();
      		System.out.print(error+"\n");
  			return null;
      	}
    }
	     
	private boolean returnDateSuccess(String sql,String error) throws SQLException {
		try {
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(10); // 将超时设置为10秒.
			// 执行插入user语句
			statement.executeUpdate(sql+";");
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			System.out.print(error+"\n");
		}
		return false;
	}

     public boolean selectUser(String name,String password) throws SQLException{
    	 String sql="select * from user where name='"+name+"' and password='"+password+"'" ;
    	 String error="查询用户失败";
    	 return returnExist(sql, error);
       }
      
     public boolean selectUserName(String name) throws SQLException {
    	 String sql="select * from user where name='"+name+"'" ;
    	 String error="查询用户名失败";
    	 return returnExist(sql, error);
     }
     
     public int selectUserOnline(String name) throws SQLException {
    	 String sql="select * from user where name='"+name+"'" ;
    	 String error="查询用户在线状态失败";
    	 return returnRs(sql, error).getInt("online");
      }
     
     public String selectRoomIp(int port) throws SQLException {
    	 String sql="select ip from room where port='"+port+"'" ;
    	 String error="查询用户在线状态失败";
    	 return returnRs(sql, error).getString("ip");
      }
     
     public int selectRoomPort(String master) throws SQLException {
    	 String sql="select port from room where master='"+master+"'" ;
    	 String error="查询房间端口失败";
    	 return returnRs(sql, error).getInt("port");
      }
     
     public  Object[] selectRoomPort() throws SQLException {
    	 String sql="select port from room" ;
    	 String error="查询房间端口失败";
    	 return returnList(sql, error).toArray();
     }
     
     public  Object[] selectRoomMaster() throws SQLException {
    	 String sql="select Master from room" ;
    	 String error="查询房间房主失败";
    	 return returnList(sql, error).toArray();
     }
     
     public String selectRoomMaster(int port) throws SQLException {
    	 String sql="select master from room where port="+port+"" ;
    	 String error="查询房主失败";
    	 return returnRs(sql, error).getString("master");
      }
     
     public boolean selectRoomUserExist(int port) throws SQLException {
    	 String sql="select * from roomUser where roomPort="+port+"" ;
    	 String error="查询房间内的用户失败";
    	 return returnExist(sql,error);
      }
     
     public Object[] selectRoomUser(int port) throws SQLException {
    	 String sql="select roomUser from roomUser where roomPort="+port+"" ;
    	 String error="查询房间内的用户失败";
    	 return returnList(sql, error).toArray();
       }
     
 	public boolean insertUser(String name,String password) throws SQLException {
 		String sql="insert into user(name,password) values('" + name + "','" + password +"')" ;
 		String error="插入用户数据失败";
 		return  returnDateSuccess(sql, error);
	}
	
	public boolean insertRoom(String master,int port,String ip) throws SQLException {
 		String sql="insert into room(master,port,ip) values('" + master + "'," + port +",'"+ip+"')" ;
 		String error="插入房间失败";
 		return  returnDateSuccess(sql, error);
	}
    
	public boolean insertRoomUser(int port,String user) throws SQLException {
		String sql="insert into roomUser(roomPort,roomUser ) values(" + port + ",'" + user +"')" ;
 		String error="插入房间内的用户失败";
 		return  returnDateSuccess(sql, error);
	}
	
	public boolean updateUserFalse(String name) throws SQLException {
		String sql="update user set online=0 where name='" +  name + "'" ;
 		String error="更改用户在线状态false失败";
 		return  returnDateSuccess(sql, error);
	}
	
	public boolean updateUserTrue(String name) throws SQLException {
		String sql="update user set online=1 where name='" +  name + "'" ;
 		String error="更改用户在线状态true失败";
 		return  returnDateSuccess(sql, error);
	}
	
	public boolean deleteRoom(int port) throws SQLException {
		String sql="DELETE from room where port='"+port+"'" ;
 		String error="删除房间失败";
 		return  returnDateSuccess(sql, error);
	}
	
	public boolean deleteRoomUser(int port,String user) throws SQLException {
		String sql="DELETE from roomUser where roomPort='"+port+"' and roomUser='"+user+"'" ;
 		String error="删除房间内的用户失败";
 		return  returnDateSuccess(sql, error);
	}
	
	public boolean deleteRoomAllUser(int port) throws SQLException {
		String sql="DELETE from roomUser where roomPort='"+port+"'" ;
 		String error="删除房间内所有用户失败";
 		return  returnDateSuccess(sql, error);
	}
	

}