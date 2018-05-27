package jp.co.security.model;

public class RedirectModel
{
	String username;
	String isSearch;
	String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getIsSearch()
	{
		return isSearch;
	}

	public void setIsSearch(String isSearch)
	{
		this.isSearch = isSearch;
	}
}
