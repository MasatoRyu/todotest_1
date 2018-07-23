package test;

/**
 * ユーザ情報を保持します。
 */
public class User {
	
	/**
	 * IDを保持します。
	 */
	private String _id;
	
	/**
	 * 名前を保持します。
	 */
	private String _name;

	
	
	/**
	 * 構築します。
	 */
	public User() {
		_id = null;
		_name = null;
	}
	
	/**
	 * IDを取得します。
	 * @return
	 */
	public String getId() {
		return _id;
	}
	
	/**
	 * IDを設定します。
	 * @param id
	 */
	public void setId(String id) {
		_id = id;
	}
	
	/**
	 * 名前を取得します。
	 * @return
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * 名前を設定します。
	 * @param name
	 */
	public void setName(String name) {
		_name = name;
	}
	
}