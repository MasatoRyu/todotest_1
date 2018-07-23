package test;

import java.sql.Date;

/**
 * �A�C�e������ێ����܂��B
 */
public class Item {

	/**
	 * ID��ێ����܂��B
	 */
	private String _id;
	
	/**
	 * ���O��ێ����܂��B
	 */
	private String _name;
	
	/**
	 * �S���҂�ێ����܂��B
	 */
	private User _user;
	
	/**
	 * ������ێ����܂��B
	 */
	private Date _expireDate;
	
	/**
	 * �I������������ێ����܂��B
	 */
	private Date _finishedDate;
	
	
	
	/**
	 * �\�z���܂��B
	 */
	public Item() {
		_id = null;
		_name = null;
		_user = null;
		_expireDate = null;
		_finishedDate = null;
	}
	
	/**
	 * ID���擾���܂��B
	 * @return
	 */
	public String getId() {
		return _id;
	}
	
	/**
	 * ID��ݒ肵�܂��B
	 * @param id
	 */
	public void setId(String id) {
		_id = id;
	}
	
	/**
	 * ���O���擾���܂��B
	 * @return
	 */
	public String getName() {
		return _name;
	}

	/**
	 * ���O��ݒ肵�܂��B
	 * @param name
	 */
	public void setName(String name) {
		_name = name;
	}
	
	/**
	 * ���[�U���擾���܂��B
	 * @return
	 */
	public User getUser() {
		return _user;
	}

	/**
	 * ���[�U��ݒ肵�܂��B
	 * @param user
	 */
	public void setUser(User user) {
		_user = user;
	}
	
	/**
	 * �������擾���܂��B
	 * @return
	 */
	public Date getExpireDate() {
		return _expireDate;
	}
	
	/**
	 * ������ݒ肵�܂��B
	 * @param expireDate
	 */
	public void setExpireDate(Date expireDate) {
		_expireDate = expireDate;
	}
	
	/**
	 * �I���������擾���܂��B
	 * @return
	 */
	public Date getFinishedDate() {
		return _finishedDate;
	}
	
	/**
	 * �I��������ݒ肵�܂��B
	 * @param finishedDate
	 */
	public void setFinishedDate(Date finishedDate) {
		_finishedDate = finishedDate;
	}
}