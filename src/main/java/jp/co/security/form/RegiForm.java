package jp.co.security.form;

import java.io.Serializable;

public class RegiForm implements Serializable
{
	private String email;
	private String name;
	private String password;
	private String authority;


	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
	public String getAuthority()
	{
		return authority;
	}
	public void setAuthority(String authority)
	{
		this.authority = authority;
	}


}
