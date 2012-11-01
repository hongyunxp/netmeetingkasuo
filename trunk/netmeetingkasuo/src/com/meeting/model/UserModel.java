package com.meeting.model;

import java.io.Serializable;

public class UserModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -309962348810680966L;

	private String usercode;
	private String password;
	private String sessionid;
	private String username;
	private int userrole;// 0 表示管理员，1表示普通用户
	private String useremail;
	private String userpic;
	private String createtime;
	private String updatetime;

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getUserrole() {
		return userrole;
	}

	public void setUserrole(int userrole) {
		this.userrole = userrole;
	}

	public String getUseremail() {
		return useremail;
	}

	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}

	public String getUserpic() {
		return userpic;
	}

	public void setUserpic(String userpic) {
		this.userpic = userpic;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public String toString() {
		return "UserModel [createtime=" + createtime + ", password=" + password
				+ ", sessionid=" + sessionid + ", updatetime=" + updatetime
				+ ", usercode=" + usercode + ", useremail=" + useremail
				+ ", username=" + username + ", userpic=" + userpic
				+ ", userrole=" + userrole + "]";
	}

}
