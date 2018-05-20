package jp.co.security.model;

public class TodoItem
{
	private String id;
	private String content;
	private String email;
	private boolean done;

	public String getEmail()
	{
		return email;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
	public boolean getDone()
	{
		return done;
	}
	public void setDone(boolean done)
	{
		this.done = done;
	}


}
