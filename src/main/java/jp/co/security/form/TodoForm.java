package jp.co.security.form;

import java.io.Serializable;

import javax.validation.constraints.Size;

public class TodoForm implements Serializable
{
	private String id;
	private String deletePostId;



	public String getDeletePostId()
	{
		return deletePostId;
	}
	public void setDeletePostId(String deletePostId)
	{
		this.deletePostId = deletePostId;
	}
	@Size(min=1,max=30)
	private String content;
	private String username;
	private boolean done;

	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getId()
	{
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean getDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}


}
