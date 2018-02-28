package pl.model;

public class UserInfo {
	private String userName;
	private String userPW;
	private String age;
	private String birthDay;
	private String email;
	private String sex;
	@Override
	public String toString() {
		return "UserInfo [userName=" + userName + ", userPW=" + userPW
				+ ", age=" + age + ", birthDay=" + birthDay + ", email="
				+ email + ", sex=" + sex + "]";
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPW() {
		return userPW;
	}
	public void setUserPW(String userPW) {
		this.userPW = userPW;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
}
