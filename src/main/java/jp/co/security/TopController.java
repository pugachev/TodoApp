package jp.co.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.security.form.RegiForm;
import jp.co.security.form.TodoForm;
import jp.co.security.model.TodoItem;



@Controller
public class TopController
{
	private static final Logger logger = LoggerFactory.getLogger(TopController.class);

	@Autowired
    private JdbcTemplate jdbcTemplate;

	@Autowired
	private PlatformTransactionManager txMgr;

    @ModelAttribute
    public TodoForm setUpTodoForm()
    {
    	TodoForm form = new TodoForm();
        return form;
    }

    @ModelAttribute
    public RegiForm setUpRegiForm()
    {
    	RegiForm form = new RegiForm();
        return form;
    }

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String login(Locale locale, Model model)
	{
		return "login";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@Transactional("transactionManagerName")
	public String regei(@Validated RegiForm form, BindingResult result, Model model,HttpSession session)
	{
		String rcvName = form.getName();
		String rcvPassword  = form.getPassword();

		logger.debug("名前:Email",rcvName,rcvPassword);

    	DefaultTransactionDefinition dtDef = new DefaultTransactionDefinition();
    	TransactionStatus tSts = txMgr.getTransaction(dtDef);

		try
		{
			jdbcTemplate.update("INSERT INTO users (name,password,authority) VALUES (?,?,?)",rcvName,rcvPassword,"ROLE_ADMIN");
			 txMgr.commit(tSts);
		}
		catch(Exception ex)
		{
			txMgr.rollback(tSts);
			logger.debug("新規作成失敗",ex.toString());
		}

		return "login";
	}

    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @Transactional("transactionManagerName")
    public String detail(@Validated TodoForm form, BindingResult result, Model model,HttpSession session,@RequestParam(value = "username") String username)
    {
    	String rcv = form.getId() ;

    	model.addAttribute("id", rcv);
    	model.addAttribute("username", username);

		return "todo/detail";
    }

	@RequestMapping(value = "/top", method = RequestMethod.GET)
	public String top(Locale locale, Model model,RedirectAttributes redirectAttributes) {

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String username="";
		String tmp="";
		Collection<? extends GrantedAuthority> prinipals= ((UserDetails)principal).getAuthorities();
		if (principal instanceof UserDetails) {
		  username = ((UserDetails)principal).getUsername();
		  tmp = ((UserDetails)principal).getAuthorities().toString();

		} else {
		  username =  ((UserDetails)principal).getUsername();
		}

		if(tmp.equals("[ROLE_USER]"))
		{
			return "login";
		}

		model.addAttribute("username", username);

		return "redirect:/list?isSearche=no&username="+username;
	}

    @RequestMapping(value = "/newItem", params="newItem",method = RequestMethod.POST)
    @Transactional("transactionManagerName")
    public String newItem(@Validated TodoForm form, BindingResult result, Model model,@RequestParam(value = "username") String username)
    {
    	DefaultTransactionDefinition dtDef = new DefaultTransactionDefinition();
    	TransactionStatus tSts = txMgr.getTransaction(dtDef);

		try
		{
			jdbcTemplate.update("INSERT INTO todo (content,done,email) VALUES (?, ?,?)", form.getContent(),false,username);
			txMgr.commit(tSts);
		}
		catch(Exception ex)
		{
			txMgr.rollback(tSts);
			logger.debug("update失敗",ex.toString());
		}

		return "redirect:/list?isSearche=no&username="+username;
    }

    @RequestMapping(value = "/newItem", params="searchItem",method = RequestMethod.POST)
    @Transactional("transactionManagerName")
    public String searchItem(@Validated TodoForm form, BindingResult result, Model model,HttpSession session,@RequestParam(value = "username") String username)
    {
    	String likeSQL = null;
    	if(form.getContent()!=null && !form.getContent().equals(""))
    	{
    		likeSQL = "select * from todo where email = '" + username+ "' and content like '%" + form.getContent() + "%'";
    	}
    	else
    	{
    		likeSQL = "select * from todo where email = '" + username +"'";
    	}

		//テーブルtodoから全データを取得する
		List<Map<String, Object>> ret = jdbcTemplate.queryForList(likeSQL);
		//画面にわたすデータのリストを生成する
		List<TodoItem> mList = new ArrayList<TodoItem>();
		for(int i=0;i<ret.size();i++)
		{
			TodoItem tmp = new TodoItem();
			tmp.setId(ret.get(i).get("id").toString());
			tmp.setContent(ret.get(i).get("content").toString());
			tmp.setEmail(ret.get(i).get("email").toString());
			if(ret.get(i).get("done").toString().equals("false"))
			{
				tmp.setDone(false);
			}
			else
			{
				tmp.setDone(true);
			}
			mList.add(tmp);
		}

		//画面にわたすリストをsessionに設定しリダイレクト先でこのデータを使用する
		session.setAttribute("mList", mList);

		return "redirect:/list?isSearche=yes&username="+username;
    }

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String index(Locale locale,@RequestParam("isSearche") String isSearche, Model model,HttpSession session,@RequestParam(value = "username") String username)
	{
		if(isSearche.equals("yes"))
		{
			List<TodoItem> mListM = (List<TodoItem>) session.getAttribute("mList");
			model.addAttribute("mList", mListM );
			model.addAttribute("username", username);
			return "todo/todo";
		}

		String listSQL = "select * from todo where email='" + username +"'";

		//テーブルtodoから全データを取得する
		List<Map<String, Object>> ret = jdbcTemplate.queryForList(listSQL);
		//画面にわたすデータのリストを生成する
		List<TodoItem> mList = new ArrayList<TodoItem>();
		for(int i=0;i<ret.size();i++)
		{
			TodoItem tmp = new TodoItem();
			tmp.setId(ret.get(i).get("id").toString());
			tmp.setContent(ret.get(i).get("content").toString());
			tmp.setEmail(ret.get(i).get("email").toString());
			if(ret.get(i).get("done").toString().equals("false"))
			{
				tmp.setDone(false);
			}
			else
			{
				tmp.setDone(true);
			}
			mList.add(tmp);
		}
		//画面にわたすリストをModelに設定する
		model.addAttribute("mList", mList );
		model.addAttribute("username", username);
		return "todo/todo";
	}

    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    @Transactional("transactionManagerName")
    public String restore(@Validated TodoForm form, BindingResult result, Model model,@RequestParam(value = "username") String username)
    {
    	DefaultTransactionDefinition dtDef = new DefaultTransactionDefinition();
    	TransactionStatus tSts = txMgr.getTransaction(dtDef);

    	List<Map<String, Object>> ret = jdbcTemplate.queryForList("select * from todo WHERE id=?",new Object[]{form.getId()});
    	if(ret.size()>0)
    	{
    		TodoItem upItem = new TodoItem();
    		upItem.setId(form.getId());
    		upItem.setContent(form.getContent());
    		upItem.setDone(form.getDone());

    		SqlParameterSource param = new BeanPropertySqlParameterSource(upItem);

    		try
    		{
    			jdbcTemplate.update("UPDATE todo SET done = ? WHERE id = ?",false,form.getId());
    			 txMgr.commit(tSts);
    		}
    		catch(Exception ex)
    		{
    			txMgr.rollback(tSts);
    			logger.debug("update失敗",ex.toString());
    		}
    	}
    	else
    	{
    		logger.debug("update対象なし");
    	}



//        return "redirect:/list?isSearche=no";
    	return "redirect:/list?isSearche=no&username="+username;
    }

    @RequestMapping(value = "/done", method = RequestMethod.POST)
    @Transactional("transactionManagerName")
    public String done(@Validated TodoForm form, BindingResult result, Model model,@RequestParam(value = "username") String username)
    {

    	DefaultTransactionDefinition dtDef = new DefaultTransactionDefinition();
    	TransactionStatus tSts = txMgr.getTransaction(dtDef);

    	List<Map<String, Object>> ret = jdbcTemplate.queryForList("select * from todo WHERE id=?",new Object[]{form.getId()});
    	if(ret.size()>0)
    	{
    		TodoItem upItem = new TodoItem();
    		upItem.setId(form.getId());
    		upItem.setContent(form.getContent());
    		upItem.setDone(form.getDone());

    		SqlParameterSource param = new BeanPropertySqlParameterSource(upItem);

    		try
    		{
    			jdbcTemplate.update("UPDATE todo SET done = ? WHERE id = ?",true,form.getId());
    			 txMgr.commit(tSts);
    		}
    		catch(Exception ex)
    		{
    			txMgr.rollback(tSts);
    			logger.debug("update失敗",ex.toString());
    		}
    	}
    	else
    	{
    		logger.debug("update対象なし");
    	}



//        return "redirect:/list?isSearche=no";
    	return "redirect:/list?isSearche=no&username="+username;
    }

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public String test(Locale locale, Model model,@RequestParam(value = "username") String username)
	{
		return "redirect:/list?isSearche=no&username="+username;
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public String user(Locale locale, Model model)
	{
		return "user";
	}

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String admin(Locale locale, Model model)
	{
		return "admin";
	}

	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String error(Locale locale, Model model)
	{
		return "error";
	}

	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public String permission(Locale locale, Model model)
	{
		return "403";
	}

	@RequestMapping(value = "/regi", method = RequestMethod.GET)
	public String regi(Locale locale, Model model)
	{
		return "regi";
	}

	@RequestMapping(value = "/toLogin", method = RequestMethod.GET)
	public String toLogin(Locale locale, Model model)
	{
		return "login";
	}
}
