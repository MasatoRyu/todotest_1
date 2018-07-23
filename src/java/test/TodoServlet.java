package test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO�Ǘ��������Ȃ��T�[�u���b�g�B
 * (�S���)
 */
public class TodoServlet extends HttpServlet {

	/**
	 * �V���A���o�[�W����UID(���܂��Ȃ�)
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	* JSP�̃x�[�X�f�B���N�g���B
	*/
	private static final String JSP_BASE = "/WEB-INF/jsp/";

	/**
	 * �f�[�^�x�[�X�̃R�l�N�V������ێ����܂��B
	 */
	private Connection _pooledConnection;

	/**
	 * �\�z���܂��B
	 */
	public TodoServlet() {
		_pooledConnection = null;
	}

	@Override
	public void destroy() {
		if (_pooledConnection != null) {
			try {
				_pooledConnection.close();
			} catch (SQLException e) {
				;
			}
			_pooledConnection = null;
		}

		super.destroy();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// �v������action�p�����[�^���擾
		String action = req.getParameter("action");

		String forward = null;
		if ("login".equals(action)) {
			// ���O�C����ʂ̏���
			// login.jsp�փt�H���[�h����
			forward = JSP_BASE + "login.jsp";
		} else {
			// �s���ȃA�N�V�����̏ꍇ
			forward = doError(req, resp, "�s���ȃ��N�G�X�g�ł�");
		}

		// JSP�ւ̃t�H���[�h
		RequestDispatcher dispatcher = req.getRequestDispatcher(forward);
		dispatcher.forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// �v������action�p�����[�^���擾
		String action = req.getParameter("action");

		String forward = null;
		if ("login_action".equals(action)) {
			// ���O�C����ʂ���̓��͎�t
			forward = doLoginAction(req, resp);
		} else if ("list".equals(action)) {
			// �ꗗ��ʂ̏���
			try {
				Item[] items = getItems();
				req.setAttribute("items", items);
				forward = JSP_BASE + "list.jsp";
			} catch (SQLException e) {
				forward = doError(req, resp, e.getMessage());
			}
		} else if ("add".equals(action)) {
			// �o�^��ʂ̏���
			try {
				User[] users = getUsers();
				req.setAttribute("users", users);
				forward = JSP_BASE + "add.jsp";
			} catch (SQLException e) {
				forward = doError(req, resp, e.getMessage());
			}
		} else if ("add_action".equals(action)) {
			// �o�^��ʂ���̓��͎�t
			forward = doAddAction(req, resp);
		} else if ("finish".equals(action)) {
			// ��������
			forward = doFinishAction(req, resp);
		} else if ("search".equals(action)) {
			// ������ʂ̏���
			try {
				Item[] items = searchItems(req);
				if (items == null) {
					forward = doError(req, resp, "�s���ȃp�����[�^�ł��B");
				} else {
					req.setAttribute("items", items);
					forward = JSP_BASE + "search.jsp";
				}
			} catch (SQLException e) {
				forward = doError(req, resp, e.getMessage());
			}
		} else if ("delete".equals(action)) {
			// �폜��ʂ̏���
			try {
				Item item = getItem(req);
				if (item == null) {
					forward = doError(req, resp, "�s���ȃp�����[�^�ł��B");
				} else {
					req.setAttribute("item", item);
					forward = JSP_BASE + "delete.jsp";
				}
			} catch (SQLException e) {
				forward = doError(req, resp, e.getMessage());
			}
		} else if ("delete_action".equals(action)) {
			// �폜��ʂ���̓��͎�t
			forward = doDeleteAction(req, resp);
		} else if ("edit".equals(action)) {
			// �X�V��ʂ̏���
			try {
				Item item = getItem(req);
				if (item == null) {
					forward = doError(req, resp, "�s���ȃp�����[�^�ł��B");
				} else {
					req.setAttribute("item", item);
					User[] users = getUsers();
					req.setAttribute("users", users);
					forward = JSP_BASE + "edit.jsp";
				}
			} catch (SQLException e) {
				forward = doError(req, resp, e.getMessage());
			}
		} else if ("edit_action".equals(action)) {
			// �X�V��ʂ���̓��͎�t
			forward = doEditAction(req, resp);
		} else {
			// �s���ȃA�N�V�����̏ꍇ
			forward = doError(req, resp, "�s���ȃ��N�G�X�g�ł�");
		}

		// JSP�ւ̃t�H���[�h
		RequestDispatcher dispatcher = req.getRequestDispatcher(forward);
		dispatcher.forward(req, resp);
	}

	/**
	 * ���O�C�������������Ȃ��܂��B
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private String doLoginAction(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		String userID = req.getParameter("user_id");
		String password = req.getParameter("password");
		if (userID == null || password == null) {
			throw new ServletException("�s���ȃp�����[�^�ł��B");
		}

		try {
			// ���[�U���擾����
			User user = getUser(userID, password);
			if (user == null) {
				return doError(req, resp, "�s���ȃ��[�UID�������̓p�����[�^�ł��B");
			}

			// ���O���Z�b�V�����Ɋi�[����
			req.getSession().setAttribute("currentUser", user);

			// �A�C�e�����擾����
			Item[] items = getItems();

			// �A�C�e����v���I�u�W�F�N�g�Ɋi�[����
			req.setAttribute("items", items);

			// �ꗗ��\������
			return JSP_BASE + "list.jsp";
		} catch (SQLException e) {
			return doError(req, resp, e.getMessage());
		}
	}

	/**
	 * �o�^�v�����������܂��B
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private String doAddAction(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String name = req.getParameter("name");
			String userID = req.getParameter("user_id");
			String expireYear = req.getParameter("year");
			String expireMonth = req.getParameter("month");
			String expireDay = req.getParameter("day");
			if (name == null || userID == null || expireYear == null
					|| expireMonth == null || expireDay == null) {
				return doError(req, resp, "�s���ȃp�����[�^�ł��B");
			}
			name = new String(name.getBytes("iso-8859-1"), "utf-8");
			Item targetItem = new Item();
			User user = getUser(userID);
			if (user == null) {
				return doError(req, resp, "�s���ȃp�����[�^�ł��B");
			}
			targetItem.setUser(user);
			Date expireDate = getDate(expireYear, expireMonth, expireDay);
			if (expireDate == null) {
				return doError(req, resp, "�s���ȃp�����[�^�ł��B");
			}
			targetItem.setExpireDate(expireDate);
			targetItem.setName(name);

			executeUpdate(createInsertSQL(targetItem));

			// �A�C�e�����擾����
			Item[] items = getItems();

			// �A�C�e����v���I�u�W�F�N�g�Ɋi�[����
			req.setAttribute("items", items);

			// �ꗗ��\������
			return JSP_BASE + "list.jsp";
		} catch (SQLException e) {
			return doError(req, resp, e.getMessage());
		}
	}

	/**
	 * �����v�����������܂��B
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private String doFinishAction(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		try {
			Item currentItem = getItem(req);
			if (currentItem.getFinishedDate() == null) {
				// �����ɕύX
				Calendar calendar = Calendar.getInstance();
				currentItem
						.setFinishedDate(new Date(calendar.getTimeInMillis()));
			} else {
				// �������ɕύX
				currentItem.setFinishedDate(null);
			}

			int updateCount = executeUpdate(createUpdateSQL(currentItem));
			if (updateCount != 1) {
				return doError(req, resp, "�X�V�Ɏ��s���܂����B");
			}

			if (req.getParameter("keyword") == null) {
				// �A�C�e�����擾����
				Item[] items = getItems();

				// �A�C�e����v���I�u�W�F�N�g�Ɋi�[����
				req.setAttribute("items", items);

				// �ꗗ��\������
				return JSP_BASE + "list.jsp";
			} else {
				Item[] items = searchItems(req);

				// �A�C�e����v���I�u�W�F�N�g�Ɋi�[����
				req.setAttribute("items", items);

				// �ꗗ��\������
				return JSP_BASE + "search.jsp";
			}
		} catch (SQLException e) {
			return doError(req, resp, e.getMessage());
		}
	}

	/**
	 * �ҏW�v�����������܂��B
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private String doEditAction(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String name = req.getParameter("name");
			String userID = req.getParameter("user_id");
			String expireYear = req.getParameter("year");
			String expireMonth = req.getParameter("month");
			String expireDay = req.getParameter("day");
			if (name == null || userID == null || expireYear == null
					|| expireMonth == null || expireDay == null) {
				return doError(req, resp, "�s���ȃp�����[�^�ł��B");
			}
			name = new String(name.getBytes("iso-8859-1"), "utf-8");
			Item currentItem = getItem(req);
			User user = getUser(userID);
			currentItem.setUser(user);
			Date expireDate = getDate(expireYear, expireMonth, expireDay);
			currentItem.setExpireDate(expireDate);
			currentItem.setName(name);
			String finished = req.getParameter("finished");
			if ("true".equals(finished)) {
				if (currentItem.getFinishedDate() == null) {
					Calendar calendar = Calendar.getInstance();
					currentItem.setFinishedDate(new Date(calendar
							.getTimeInMillis()));
				}
			} else {
				currentItem.setFinishedDate(null);
			}

			int updateCount = executeUpdate(createUpdateSQL(currentItem));
			if (updateCount != 1) {
				return doError(req, resp, "�X�V�Ɏ��s���܂����B");
			}

			// �A�C�e�����擾����
			Item[] items = getItems();

			// �A�C�e����v���I�u�W�F�N�g�Ɋi�[����
			req.setAttribute("items", items);

			// �ꗗ��\������
			return JSP_BASE + "list.jsp";
		} catch (SQLException e) {
			return doError(req, resp, e.getMessage());
		}
	}

	/**
	 * �폜�v�����������܂��B
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private String doDeleteAction(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		try {
			Item currentItem = getItem(req);

			int updateCount = executeUpdate(createDeleteSQL(currentItem));
			if (updateCount != 1) {
				return doError(req, resp, "�X�V�Ɏ��s���܂����B");
			}

			// �A�C�e�����擾����
			Item[] items = getItems();

			// �A�C�e����v���I�u�W�F�N�g�Ɋi�[����
			req.setAttribute("items", items);

			// �ꗗ��\������
			return JSP_BASE + "list.jsp";
		} catch (SQLException e) {
			return doError(req, resp, e.getMessage());
		}
	}

	/**
	 * �G���[��\�����܂��B
	 * 
	 * @param req
	 * @param resp
	 * @param message
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private String doError(HttpServletRequest req, HttpServletResponse resp,
			String message) throws ServletException, IOException {
		req.setAttribute("message", message);

		// �G���[��\������
		return JSP_BASE + "error.jsp";
	}

	/**
	 * �ڑ��I�u�W�F�N�g�𐶐����܂��B
	 * 
	 * @return
	 * @throws SQLException
	 */
	private Connection getConnection() throws SQLException {
		// Connection�̏���
		if (_pooledConnection != null) {
			return _pooledConnection;
		}
		try {
			// ������
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			_pooledConnection = DriverManager.getConnection(
					"jdbc:sqlserver://rdb01.database.windows.net\\rdb02:1433;databaseName=rdb02;user=user01;password=Passw0rd2018;allowPortWithNamedInstance=true;EncryptionMethod=SSL;ValidateServerCertificate=false"
                                        );
			return _pooledConnection;
		} catch (ClassNotFoundException e) {
			_pooledConnection = null;
			throw new SQLException(e);
		} catch (SQLException e) {
			_pooledConnection = null;
			throw e;
		}
	}

	/**
	 * ���[�U���擾���܂��B
	 * 
	 * @param userID
	 * @param password
	 * @return
	 * @throws ServletException
	 */
	private User getUser(String userID, String password) throws SQLException {
		Statement statement = null;
		try {
			// SQL���𔭍s
			statement = getConnection().createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE
                        );
			ResultSet resultSet = statement
					.executeQuery("SELECT ID,NAME FROM TODO_USER WHERE ID='"
							+ userID + "' AND PASSWORD='" + password + "'");
			boolean br = resultSet.first();
			if (br == false) {
				return null;
			}
			User user = new User();
			user.setId(resultSet.getString("ID"));
			user.setName(resultSet.getString("NAME"));

			return user;
		} catch (SQLException e) {
			_pooledConnection = null;
			throw e;
		} finally {
			if (statement != null) {
				statement.close();
				statement = null;
			}
		}
	}

	/**
	 * ���[�U���擾���܂��B
	 * 
	 * @param userID
	 * @return
	 * @throws ServletException
	 */
	private User getUser(String userID) throws SQLException {
		Statement statement = null;
		try {
			// SQL���𔭍s
			statement = getConnection().createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE
                        );
			ResultSet resultSet = statement
					.executeQuery("SELECT ID,NAME FROM TODO_USER WHERE ID='"
							+ userID + "'");
			boolean br = resultSet.first();
			if (br == false) {
				return null;
			}
			User user = new User();
			user.setId(resultSet.getString("ID"));
			user.setName(resultSet.getString("NAME"));

			return user;
		} catch (SQLException e) {
			_pooledConnection = null;
			throw e;
		} finally {
			if (statement != null) {
				statement.close();
				statement = null;
			}
		}
	}

	/**
	 * ���[�U�ꗗ���擾���܂��B
	 * 
	 * @return
	 * @throws ServletException
	 */
	private User[] getUsers() throws SQLException {
		Statement statement = null;
		try {
			// SQL���𔭍s
			statement = getConnection().createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE
                        );
			ResultSet resultSet = statement
					.executeQuery("SELECT ID,NAME FROM TODO_USER");
			boolean br = resultSet.first();
			if (br == false) {
				return new User[0];
			}
			ArrayList<User> users = new ArrayList<User>();
			do {
				User user = new User();
				user.setId(resultSet.getString("ID"));
				user.setName(resultSet.getString("NAME"));
				users.add(user);
			} while (resultSet.next());

			return users.toArray(new User[0]);
		} catch (SQLException e) {
			_pooledConnection = null;
			throw e;
		} finally {
			if (statement != null) {
				statement.close();
				statement = null;
			}
		}
	}

	/**
	 * �A�C�e�����擾���܂��B
	 * 
	 * @return
	 * @throws ServletException
	 */
	private Item[] getItems() throws SQLException {
		Statement statement = null;
		try {
			// SQL���𔭍s
			statement = getConnection().createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE
                        );
			ResultSet resultSet = statement.executeQuery(createSQL(null));
			boolean br = resultSet.first();
			if (br == false) {
				return new Item[0];
			}
			ArrayList<Item> items = new ArrayList<Item>();
			do {
				Item item = new Item();
				item.setId(resultSet.getString(1));
				item.setName(resultSet.getString(2));
				User user = new User();
				user.setId(resultSet.getString(3));
				user.setName(resultSet.getString(4));
				item.setUser(user);
				item.setExpireDate(resultSet.getDate(5));
				item.setFinishedDate(resultSet.getDate(6));

				items.add(item);
			} while (resultSet.next());

			return items.toArray(new Item[0]);
		} catch (SQLException e) {
			_pooledConnection = null;
			throw e;
		} finally {
			if (statement != null) {
				statement.close();
				statement = null;
			}
		}
	}

	/**
	 * �A�C�e�����擾���܂��B
	 * 
	 * @param req
	 * @return
	 * @throws ServletException
	 */
	private Item getItem(HttpServletRequest req) throws SQLException {
		String itemID = req.getParameter("item_id");
		if (itemID == null) {
			return null;
		}
		return getItem(itemID);
	}

	/**
	 * �A�C�e�����擾���܂��B
	 * 
	 * @param id
	 * @return
	 * @throws ServletException
	 */
	private Item getItem(String id) throws SQLException {
		Statement statement = null;
		try {
			// SQL���𔭍s
			statement = getConnection().createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE
                        );
			ResultSet resultSet = statement
					.executeQuery(createSQL("TODO_ITEM.ID='" + id + "'"));
			boolean br = resultSet.first();
			if (br == false) {
				return null;
			}
			Item item = new Item();
			item.setId(resultSet.getString(1));
			item.setName(resultSet.getString(2));
			User user = new User();
			user.setId(resultSet.getString(3));
			user.setName(resultSet.getString(4));
			item.setUser(user);
			item.setExpireDate(resultSet.getDate(5));
			item.setFinishedDate(resultSet.getDate(6));

			return item;
		} catch (SQLException e) {
			_pooledConnection = null;
			throw e;
		} finally {
			if (statement != null) {
				statement.close();
				statement = null;
			}
		}
	}

	/**
	 * �A�C�e�����������܂��B
	 * 
	 * @return
	 * @throws ServletException
	 */
	private Item[] searchItems(HttpServletRequest req) throws SQLException,
			IOException {
		String keyword = req.getParameter("keyword");
		if (keyword == null) {
			return null;
		}
		keyword = new String(keyword.getBytes("iso-8859-1"), "utf-8");
		String regExp = createRegExp(keyword);
		if (regExp == null) {
			return new Item[0];
		}
		StringBuffer where = new StringBuffer();
		String[] fields = new String[] { "TODO_ITEM.NAME",
				"TODO_ITEM.EXPIRE_DATE", "TODO_USER.NAME", "TODO_USER.ID",
				"TODO_ITEM.FINISHED_DATE" };
		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			if (where.length() > 0) {
				where.append(" OR ");
			}
			where.append(field);
			where.append(" LIKE '");
			where.append(regExp);
			where.append("'");
		}

		Statement statement = null;
		try {
			// SQL���𔭍s
			statement = getConnection().createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE
                        );
			ResultSet resultSet = statement.executeQuery(createSQL(" ("
					+ where.toString() + ")"));
			boolean br = resultSet.first();
			if (br == false) {
				return new Item[0];
			}
			ArrayList<Item> items = new ArrayList<Item>();
			do {
				Item item = new Item();
				item.setId(resultSet.getString(1));
				item.setName(resultSet.getString(2));
				User user = new User();
				user.setId(resultSet.getString(3));
				user.setName(resultSet.getString(4));
				item.setUser(user);
				item.setExpireDate(resultSet.getDate(5));
				item.setFinishedDate(resultSet.getDate(6));

				items.add(item);
			} while (resultSet.next());

			return items.toArray(new Item[0]);
		} catch (SQLException e) {
			_pooledConnection = null;
			throw e;
		} finally {
			if (statement != null) {
				statement.close();
				statement = null;
			}
		}
	}

	/**
	 * INSERT/UPDATE/DELETE�������s���܂��B
	 * 
	 * @param sql
	 * @return
	 * @throws ServletException
	 */
	private int executeUpdate(String sql) throws SQLException {
		Statement statement = null;
		try {
			// SQL���𔭍s
			statement = getConnection().createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE
                        );
			int updateCount = statement.executeUpdate(sql);

			return updateCount;
		} catch (SQLException e) {
			_pooledConnection = null;
			throw e;
		} finally {
			if (statement != null) {
				statement.close();
				statement = null;
			}
		}
	}

	/**
	 * �A�C�e���擾�p��SQL���𐶐����܂��B
	 * 
	 * @param where
	 * @return
	 */
	private String createSQL(String where) {
		StringBuffer buf = new StringBuffer();
		buf
				.append("SELECT TODO_ITEM.ID,TODO_ITEM.NAME,TODO_USER.ID,TODO_USER.NAME,EXPIRE_DATE,FINISHED_DATE FROM TODO_USER,TODO_ITEM WHERE TODO_USER.ID=TODO_ITEM.USER_");
		if (where != null) {
			buf.append(" AND ");
			buf.append(where);
		}
		return buf.toString();
	}

	/**
	 * ���t�I�u�W�F�N�g���擾���܂��B
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 * @throws ServletException
	 */
	private Date getDate(String year, String month, String day) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1,
					Integer.parseInt(day));

			return new Date(calendar.getTimeInMillis());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * �ǉ��p��SQL���𐶐����܂��B
	 * 
	 * @param targetItem
	 * @return
	 */
	private String createInsertSQL(Item targetItem) {
		StringBuffer buf = new StringBuffer();
		buf.append("INSERT INTO ");
		buf.append("TODO_ITEM");
		buf.append(" (NAME,USER_,EXPIRE_DATE,FINISHED_DATE)");
		buf.append(" VALUES('");
		buf.append(targetItem.getName());
		buf.append("', '");
		buf.append(targetItem.getUser().getId());
		buf.append("', '");
		buf.append(targetItem.getExpireDate().toString());
		buf.append("', null)");

		return buf.toString();
	}

	/**
	 * �X�V�p��SQL���𐶐����܂��B
	 * 
	 * @param targetItem
	 * @return
	 */
	private String createUpdateSQL(Item targetItem) {
		StringBuffer buf = new StringBuffer();
		buf.append("UPDATE ");
		buf.append("TODO_ITEM");
		buf.append(" SET ");
		buf.append("NAME='");
		buf.append(targetItem.getName());
		buf.append("', ");
		buf.append("EXPIRE_DATE='");
		buf.append(targetItem.getExpireDate().toString());
		buf.append("', ");
		buf.append("USER_='");
		buf.append(targetItem.getUser().getId());
		buf.append("', ");
		buf.append("FINISHED_DATE=");
		if (targetItem.getFinishedDate() != null) {
			buf.append("'");
			buf.append(targetItem.getFinishedDate().toString());
			buf.append("'");
		} else {
			buf.append("null");
		}
		buf.append(" WHERE ID='");
		buf.append(targetItem.getId());
		buf.append("'");

		return buf.toString();
	}

	/**
	 * �폜�p��SQL���𐶐����܂��B
	 * 
	 * @param targetItem
	 * @return
	 */
	private String createDeleteSQL(Item targetItem) {
		StringBuffer buf = new StringBuffer();
		buf.append("DELETE FROM ");
		buf.append("TODO_ITEM");
		buf.append(" WHERE ID='");
		buf.append(targetItem.getId());
		buf.append("'");

		return buf.toString();
	}

	/**
	 * ���K�\���𐶐����܂��B
	 * 
	 * @param keyword
	 * @return
	 */
	private String createRegExp(String keyword) {
		StringTokenizer tokenizer = new StringTokenizer(keyword, " \t");
		if (tokenizer.countTokens() == 0) {
			return null;
		}
		StringBuffer buf = new StringBuffer();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (buf.length() > 0) {
				buf.append("|");
			}
			buf.append(token);
		}
		return "(" + buf.toString() + ")";
	}

}