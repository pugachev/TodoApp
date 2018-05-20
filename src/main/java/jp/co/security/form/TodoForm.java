package jp.co.security.form;

import java.io.Serializable;

public class TodoForm implements Serializable
{
	private String id;
	private String content;
	private boolean done;

	public String getId() {
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
