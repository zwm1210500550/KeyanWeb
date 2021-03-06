package myservlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myjavabean.model.User;
import myjavabean.util.DBHelper;

/**
 * Servlet implementation class ModifyUserInfoServlet
 */
@WebServlet("/modifyuserinfoservlet")
public class ModifyUserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private User user; 
	private DBHelper dbHelper;
	
	private String postreason;
	private String uname;
	private String uemailaddress;
	private String upassword;
	private String uorganization;
	private String ucontactway;
	private String udatetime;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ModifyUserInfoServlet() {
        super();
        
        user = new User();
        dbHelper = new DBHelper();
        
        postreason = "";
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		System.out.println("获取或修改用户个人信息请求。");
		
		postreason = request.getParameter("postreason");
		if (postreason == null || postreason.equals("")) {
			System.out.println("没有请求原因信息，不予响应。");
			out.println("failed_postreason_null");
			return;
		}
		else {
			uemailaddress = request.getParameter("uemailaddress");
			if (uemailaddress == null || uemailaddress.equals("")) { // 获取邮箱参数失败（出错）
				System.out.println("获取邮箱参数失败（出错）。邮箱：" + user.getUemailaddress());
				out.println("failed_emailaddress_null");
				return;
			}
			else { // 拿到邮箱，尝试获取信息
				user.setUemailaddress( new String(uemailaddress.getBytes(), "UTF-8") );
				dbHelper.init();
				if ( dbHelper.containsUemailaddress(user.getUemailaddress()) ) { // 该邮箱可用
					try {
						if (postreason.equals("get")) { // 获取个人信息
							String sql =  "select * from user where uemailaddress=\'" + user.getUemailaddress() + "\';";
							ResultSet resultSet = dbHelper.selectSql(sql);
							if (resultSet.next()) {
								user.setUname( new String(resultSet.getString(1).getBytes(), "UTF-8") );
								user.setUpassword( new String(resultSet.getString(3).getBytes(), "UTF-8") );
								user.setUorganization( new String(resultSet.getString(4).getBytes(), "UTF-8") );
								user.setUcontactway( new String(resultSet.getString(5).getBytes(), "UTF-8") );
								user.setUdatetime( new String(resultSet.getString(6).getBytes(), "UTF-8") );
							}
							
							String returnString = user.getUname() + "|" // uname
									+ user.getUorganization() + "|" // uorganization
									+ user.getUcontactway(); // ucontactway
							System.out.println("请求成功（获取信息）。");
							out.println("success|" + returnString);
						}
						else if (postreason.equals("modify")) { // 修改个人信息
							uname = request.getParameter("uname");
							uorganization = request.getParameter("uorganization");
							ucontactway = request.getParameter("ucontactway");
							
							if (uname != null && !uname.equals("")) {
								user.setUname( new String(uname.getBytes(), "UTF-8") );
							}
							if (uorganization != null && !uorganization.equals("")) {
								user.setUorganization( new String(uorganization.getBytes(), "UTF-8") );
							}
							if (ucontactway != null && !ucontactway.equals("")) {
								user.setUcontactway( new String(ucontactway.getBytes(), "UTF-8") );
							}
							
							if ( 0 == dbHelper.update(user) ) { // 出错
								System.out.println("修改用户个人信息出错！");
								out.println("failed_modify_error");
							}
							else { // 成功
								System.out.println("修改用户个人信息成功！");
								out.println("success");
							}
						}
						else if (postreason.equals("upassword")) { // 修改密码
							user.setUpassword(request.getParameter("upassword"));
							if ( 0 == dbHelper.updateUpassword(user) ) { // 出错
								System.out.println("修改密码出错！");
								out.println("failed_modify_error");
							}
							else { // 成功
								System.out.println("修改密码成功！");
								out.println("success");
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("获取或修改用户个人信息类异常！");
					}
				}
				else { // 该邮箱未被注册
					System.out.println("该邮箱未被注册: " + user.getUemailaddress());
					out.println("failed_emailaddress_is_not_signed_up");
					return;
				}
			} // else
		} // else
		dbHelper.close();
		System.out.println("完");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
