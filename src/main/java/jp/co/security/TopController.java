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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.security.form.RegiForm;
import jp.co.security.form.TodoForm;
import jp.co.security.model.RedirectModel;
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

	@RequestMapping(value = "/top", method = RequestMethod.GET)
	public String top(Locale locale, Model model,RedirectAttributes redirectAttributes,RedirectAttributes redirectAttrs) {

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
		//コントローラー間でやりとりするデータ
    	RedirectModel rdm = new RedirectModel();
    	rdm.setIsSearch("no");
    	rdm.setUsername(username);
    	rdm.setErrorMessage("");
    	redirectAttrs.addFlashAttribute("keydata", rdm);

		return "redirect:/list";
	}

    @RequestMapping(value = "/newItem", params="newItem",method = RequestMethod.POST)
    @Transactional("transactionManagerName")
    public String newItem(@Validated @ModelAttribute TodoForm form, BindingResult result, HttpSession session,Model model,RedirectAttributes redirectAttrs)
    {
    	if(result.hasErrors())
    	{
    		//コントローラー間でやりとりするデータ
        	RedirectModel rdm = new RedirectModel();
        	rdm.setIsSearch("no");
        	rdm.setUsername(form.getUsername());
        	rdm.setErrorMessage("何か入力して下さい");
        	redirectAttrs.addFlashAttribute("keydata", rdm);
    		return "redirect:/list";
    	}

    	DefaultTransactionDefinition dtDef = new DefaultTransactionDefinition();
    	TransactionStatus tSts = txMgr.getTransaction(dtDef);
		try
		{
			jdbcTemplate.update("INSERT INTO todo (content,done,email) VALUES (?, ?,?)", form.getContent(),false,form.getUsername());
			txMgr.commit(tSts);
		}
		catch(Exception ex)
		{
			txMgr.rollback(tSts);
			logger.debug("update失敗",ex.toString());
		}

		//コントローラー間でやりとりするデータ
    	RedirectModel rdm = new RedirectModel();
    	rdm.setIsSearch("no");
    	rdm.setUsername(form.getUsername());
    	rdm.setErrorMessage("");
    	redirectAttrs.addFlashAttribute("keydata", rdm);

		return "redirect:/list";
    }

    @RequestMapping(value = "/newItem", params="searchItem",method = RequestMethod.POST)
    @Transactional("transactionManagerName")
    public String searchItem(@Validated TodoForm form, BindingResult result,Model model,HttpSession session,RedirectAttributes redirectAttrs)
    {
    	String likeSQL = null;
    	if(form.getContent()!=null && !form.getContent().equals(""))
    	{
    		likeSQL = "select * from todo where email = '" + form.getUsername()+ "' and content like '%" + form.getContent() + "%'";
    	}
    	else
    	{
    		likeSQL = "select * from todo where email = '" + form.getUsername() +"'";
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

		//コントローラー間でやりとりするデータ
    	RedirectModel rdm = new RedirectModel();
    	rdm.setIsSearch("yes");
    	rdm.setUsername(form.getUsername());
    	rdm.setErrorMessage("");
    	redirectAttrs.addFlashAttribute("keydata", rdm);

		//画面にわたすリストをsessionに設定しリダイレクト先でこのデータを使用する
		session.setAttribute("mList", mList);

		return "redirect:/list";
    }

    @RequestMapping(value = "/deletedata", method = RequestMethod.POST)
    @Transactional("transactionManagerName")
    public String deletedata(@Validated TodoForm form, BindingResult result, Model model,RedirectAttributes redirectAttrs)
    {
    	DefaultTransactionDefinition dtDef = new DefaultTransactionDefinition();
    	TransactionStatus tSts = txMgr.getTransaction(dtDef);

		try
		{
			jdbcTemplate.update("delete from todo where id=?", form.getDeletePostId());
			txMgr.commit(tSts);
		}
		catch(Exception ex)
		{
			txMgr.rollback(tSts);
			logger.debug("update失敗",ex.toString());
		}

		//コントローラー間でやりとりするデータ
    	RedirectModel rdm = new RedirectModel();
    	rdm.setIsSearch("no");
    	rdm.setUsername(form.getUsername());
    	rdm.setErrorMessage("");
    	redirectAttrs.addFlashAttribute("keydata", rdm);
		return "redirect:/list";
    }

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String index(Locale locale,Model model,HttpSession session,RedirectAttributes redirectAttrs)
	{
		//リダイレクトで受け取るデータクラス
		String rIsSearch="";
		String rUsername="";
		String rErrorMessage="";
		RedirectModel tmpRM = (RedirectModel)model.asMap().get("keydata");
		if(tmpRM!=null)
		{
			rIsSearch = tmpRM.getIsSearch();
			rUsername = tmpRM.getUsername();
			rErrorMessage = tmpRM.getErrorMessage();
		}

		if(rIsSearch.equals("yes"))
		{
			List<TodoItem> mListM = (List<TodoItem>) session.getAttribute("mList");
			model.addAttribute("mList", mListM );
			model.addAttribute("username", rUsername);
			model.addAttribute("resutErrors", rErrorMessage);
			return "todo/todo";
		}

		String listSQL = "select * from todo where email='" + rUsername +"'";

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
		//コントローラー間でやりとりするデータ
    	RedirectModel rdm = new RedirectModel();
    	rdm.setIsSearch("no");
    	rdm.setUsername(rUsername);
    	rdm.setErrorMessage("");
    	redirectAttrs.addFlashAttribute("keydata", rdm);

		//画面にわたすリストをModelに設定する
		model.addAttribute("mList", mList );
		model.addAttribute("username", rUsername);
		model.addAttribute("resutErrors",rErrorMessage);
		return "todo/todo";
	}

    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    @Transactional("transactionManagerName")
    public String restore(@Validated TodoForm form, BindingResult result, Model model,RedirectAttributes redirectAttrs)
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

		//コントローラー間でやりとりするデータ
    	RedirectModel rdm = new RedirectModel();
    	rdm.setIsSearch("no");
    	rdm.setUsername(form.getUsername());
    	rdm.setErrorMessage("");
    	redirectAttrs.addFlashAttribute("keydata", rdm);

    	return "redirect:/list";
    }

    @RequestMapping(value = "/done", method = RequestMethod.POST)
    @Transactional("transactionManagerName")
    public String done(@Validated TodoForm form, BindingResult result, Model model,RedirectAttributes redirectAttrs)
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

		//コントローラー間でやりとりするデータ
    	RedirectModel rdm = new RedirectModel();
    	rdm.setIsSearch("no");
    	rdm.setUsername(form.getUsername());
    	rdm.setErrorMessage("");
    	redirectAttrs.addFlashAttribute("keydata", rdm);

    	return "redirect:/list";
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
