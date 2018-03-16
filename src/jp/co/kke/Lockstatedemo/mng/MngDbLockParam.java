package jp.co.kke.Lockstatedemo.mng;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import jp.co.kke.Lockstatedemo.mng.db.MngDBAccees;
import jp.co.kke.Lockstatedemo.mng.db.MngPgDBAccees;
import jp.co.kke.Lockstatedemo.mng.db.MngSqliteDBAccees;
import jp.co.kke.Lockstatedemo.util.SysParamUtil;

/**
 * DBアクセス管理クラス
 * @author kke
 */
public class MngDbLockParam {

	/**
	 * ロガー
	 */
	private static Logger logger = Logger.getLogger(MngDbLockParam.class);
	/**
	 * データベース名
	 */
	private String dbName = SysParamUtil.getResourceString("DB_NAME");
	/**
	 * データベースタイプ
	 * ##PG(postgresql)
	　*　##LITE(SQLITE)
	 */
	private String dbType = SysParamUtil.getResourceString("DB_TYPE");
	/**
	 * SQLite用データベースアクセス管理クラス
	 */
	private MngDBAccees mngDBAccees;


	/**
	 * コンストラクタ
	 * @throws ClassNotFoundException
	 */
	public MngDbLockParam(String realPath) throws ClassNotFoundException {
		super();
		//データベースタイプでmngDBAcceesの切り替え
		if(MngDBAccees.S_DB_TYPE_SQLITE.equals(dbType)){
			if(realPath == null){
				throw new IllegalArgumentException("DBrealPath==null" + dbType);
			}
			this.mngDBAccees = MngSqliteDBAccees.getInstance(realPath, dbName);
		}else if(MngDBAccees.S_DB_TYPE_POSTGRESQL.equals(dbType)){
			this.mngDBAccees = new MngPgDBAccees(dbName);
		}else{
			throw new IllegalStateException
			("can't match dbType:" + dbType);
		}
	}

	/**
	 * DBプールの解放
	 */
	public void close(){
		try {
			if(this.mngDBAccees != null){
				this.mngDBAccees.stop();
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * T_PARAMテーブルからデータ取得
	 * @param key
	 * @return
	 * @throws SQLException
	 */
	public String getParam(String key) throws SQLException{
		String res = null;
		Connection connection = null;
		try {
			connection = this.mngDBAccees.getConnection();
			res = getParam(connection, key);
	    }finally{
			mngDBAccees.close(connection);
	    }
		return res;
	}
	/**
	 * T_PARAMテーブルにデータ設定
	 * @param key
	 * @param val
	 * @throws SQLException
	 */
	public void setParam(String key, String val) throws SQLException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.mngDBAccees.getConnection();
			// 自動コミット:OFF
			connection.setAutoCommit(false);
			if(getParam(connection, key) == null) {
				if(val != null) {
					String query = "INSERT INTO T_PARAM(key,val) VALUES(?, ?);";
					preparedStatement = connection.prepareStatement(query);
					int index = 1;
					preparedStatement.setString(index++, key);
					preparedStatement.setString(index++, val);
					preparedStatement.executeUpdate();
				}
			}else {
				if(val != null) {
					String query = "UPDATE T_PARAM SET val = ? WHERE key = ?;";
					preparedStatement = connection.prepareStatement(query);
					int index = 1;
					preparedStatement.setString(index++, val);
					preparedStatement.setString(index++, key);
					preparedStatement.executeUpdate();
				}else {
					String query = "DELETE FROM T_PARAM WHERE key = ?;";
					preparedStatement = connection.prepareStatement(query);
					int index = 1;
					preparedStatement.setString(index++, key);
					preparedStatement.executeUpdate();
				}
			}
			// 手動コミット ＆ 自動コミット:ON
			connection.commit();
			connection.setAutoCommit(true);
		}catch(SQLException e){
			logger.error("# Failed in inserting the data(s) to database.", e);
			try {
				if(connection != null){
					connection.rollback();
				}
			} catch(Exception e1) {
			}
			throw e;
		}finally{
			// DB切断
			mngDBAccees.close(connection, preparedStatement);
	    }
	}

	/**
	 * T_PARAMテーブルからデータ取得(コネクション継続)
	 * @param connection
	 * @param key
	 * @return
	 * @throws SQLException
	 */
	private String getParam(Connection connection, String key) throws SQLException{
		String res = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			String query = "SELECT * FROM T_PARAM WHERE KEY = ?;";
			preparedStatement = connection.prepareStatement(query);
			int index = 1;
			preparedStatement.setString(index++, key);
			rs = preparedStatement.executeQuery();
			// データ格納
			if(rs.next()) {
				res = rs.getString("val");
			}
	    }finally{
			//Statement,ResultSetのみクローズ
	    	mngDBAccees.close(null,preparedStatement, rs);
	    }
		return res;
	}

	/**
	 * T_SAMPLEテーブルにデータ設定
	 * @param name
	 * @param cal
	 * @throws SQLException
	 */
	public void insertSample(String name, Calendar cal) throws SQLException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.mngDBAccees.getConnection();

			//reqID取得
			long id = loadSampleMaxID(connection, "T_SAMPLE") + 1;

			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO T_SAMPLE(");
			query.append("id,");
			query.append("name,");
			query.append("update_ts");
			query.append(") VALUES (");
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('?').append(')');
			query.append(';');
			preparedStatement = connection.prepareStatement(query.toString());
			// 自動コミット:OFF
			connection.setAutoCommit(false);
			int index = 1;
			preparedStatement.setLong(index++, id);
			preparedStatement.setString(index++, name);
			preparedStatement.setLong(index++, cal.getTimeInMillis());
			//logger.info(query.toString());
			preparedStatement.executeUpdate();
			// 手動コミット ＆ 自動コミット:ON
			connection.commit();
			connection.setAutoCommit(true);
		}catch(SQLException e){
			logger.error("# Failed in inserting the data(s) to database.", e);
			try {
				if(connection != null){
					connection.rollback();
				}
			} catch(Exception e1) {
			}
			throw e;
		}finally{
			// DB切断
			mngDBAccees.close(connection, preparedStatement);
	    }
	}



	/**
	 * T_EVENT_USERテーブルにデータ設定
	 * @param event_id
	 * @param user_id
	 * @throws SQLException
	 */
	public void insertEventUser(String event_id, String user_id) throws SQLException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.mngDBAccees.getConnection();

			//reqID取得
			long id = loadSampleMaxID(connection, "T_EVENT_USER") + 1;

			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO T_EVENT_USER(");
			query.append("id,");
			query.append("event_id,");
			query.append("user_id,");
			query.append("delete_flg");
			query.append(") VALUES (");
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('0').append(')');
			query.append(';');
			preparedStatement = connection.prepareStatement(query.toString());
			// 自動コミット:OFF
			connection.setAutoCommit(false);
			int index = 1;
			preparedStatement.setLong(index++, id);
			preparedStatement.setString(index++, event_id);
			preparedStatement.setString(index++, user_id);
			//logger.info(query.toString());
			preparedStatement.executeUpdate();
			// 手動コミット ＆ 自動コミット:ON
			connection.commit();
			connection.setAutoCommit(true);

		}catch(Exception e){
			logger.error("# Failed in inserting the data(s) to database.", e);
			try {
				if(connection != null){
					connection.rollback();
				}
			} catch(Exception e1) {
			}
			throw e;
		}finally{
			// DB切断
			mngDBAccees.close(connection, preparedStatement);
	    }
	}

	/**
	 * T_EVENT_USERテーブルからデータ取得
	 * @param eventId
	 * @return
	 * @throws SQLException
	 */
	public List<String> getEventUser(String eventId) throws SQLException{
		List<String> res = null;
		Connection connection = null;
		try {
			connection = this.mngDBAccees.getConnection();
			res = getEventUser(connection, eventId);
	    }finally{
			mngDBAccees.close(connection);
	    }
		return res;
	}


	/**
	 * T_EVENT_USERテーブルからデータ取得(コネクション継続)
	 * @param connection
	 * @param eventId
	 * @return
	 * @throws SQLException
	 */
	private List<String> getEventUser(Connection connection, String eventId) throws SQLException{
		List<String> list = new ArrayList<String>();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			String query = "SELECT * FROM T_EVENT_USER WHERE EVENT_ID = ? AND DELETE_FLG = 0;";
			preparedStatement = connection.prepareStatement(query);
			int index = 1;
			preparedStatement.setString(index++, eventId);
			rs = preparedStatement.executeQuery();
			// データ格納
			//if(rs.next()) {
			//	res = rs.getString("user_id");
			//}
			while(rs.next()){
			    list.add(rs.getString("user_id"));
			}
		}
		catch(Exception e) {
			System.out.println(e);
	    }finally{
			//Statement,ResultSetのみクローズ
	    	mngDBAccees.close(null,preparedStatement, rs);
	    }
		//return res;
		return list;
	}


	/**
	 * T_EVENT_USERテーブルのデータ更新
	 * @param user_id
	 * @throws SQLException
	 */
	public void updateEventUser(String user_id) throws SQLException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.mngDBAccees.getConnection();

			//reqID取得
			//long id = loadSampleMaxID(connection, "T_EVENT_USER") + 1;

			StringBuilder query = new StringBuilder();
			query.append("UPDATE T_EVENT_USER ");
			query.append("SET DELETE_FLG ");
			query.append("= ");
			query.append("'1' ");
			query.append("WHERE USER_ID ");
			query.append("= ");
			query.append("?");
			query.append(";");
			preparedStatement = connection.prepareStatement(query.toString());
			// 自動コミット:OFF
			connection.setAutoCommit(false);
			int index = 1;
			preparedStatement.setString(index++, user_id);

			//logger.info(query.toString());
			preparedStatement.executeUpdate();
			// 手動コミット ＆ 自動コミット:ON
			connection.commit();
			connection.setAutoCommit(true);

		}catch(Exception e){
			logger.error("# Failed in inserting the data(s) to database.", e);
			try {
				if(connection != null){
					connection.rollback();
				}
			} catch(Exception e1) {
			}
			throw e;
		}finally{
			// DB切断
			mngDBAccees.close(connection, preparedStatement);
	    }
	}

	/* === T_DEVICE_INFOテーブル関連 START ======================================================= */
	/**
	 * T_DEVICE_INFOテーブルにデータ新規登録
	 * @param calender_id
	 * @param device_id
	 * @throws SQLException
	 */
	public void insertDeviceInfo(String calender_id, String device_id, String add_sttime, String add_edtime) throws SQLException{

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.mngDBAccees.getConnection();

			//reqID取得
			long id = loadSampleMaxID(connection, "T_DEVICE_INFO") + 1;

			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO T_DEVICE_INFO(");
			query.append("id,");
			query.append("calender_id,");
			query.append("device_id,");
			query.append("add_sttime,");
			query.append("add_edtime,");
			query.append("delete_flg");
			query.append(") VALUES (");
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('0').append(')');
			query.append(';');
			preparedStatement = connection.prepareStatement(query.toString());
			// 自動コミット:OFF
			connection.setAutoCommit(false);
			int index = 1;
			preparedStatement.setLong(index++, id);
			preparedStatement.setString(index++, calender_id);
			preparedStatement.setString(index++, device_id);
			//logger.info(query.toString());
			preparedStatement.executeUpdate();
			// 手動コミット ＆ 自動コミット:ON
			connection.commit();
			connection.setAutoCommit(true);

		}catch(Exception e){
			logger.error("# Failed in inserting the data(s) to database.", e);
			try {
				if(connection != null){
					connection.rollback();
				}
			} catch(Exception e1) {
			}
			throw e;
		}finally{
			// DB切断
			mngDBAccees.close(connection, preparedStatement);
	    }
	}

	/**
	 * T_DEVICE_INFOテーブルからデータ取得
	 * @param calender_id
	 * @return
	 * @throws SQLException
	 */
	public List<String> getDeviceInfo(String calender_id) throws SQLException{
		List<String> res = null;
		Connection connection = null;
		try {
			connection = this.mngDBAccees.getConnection();
			res = getDeviceInfo(connection, calender_id);
	    }finally{
			mngDBAccees.close(connection);
	    }
		return res;
	}

	/**
	 * T_DEVICE_INFOテーブルからデータ取得(コネクション継続)
	 * @param connection
	 * @param calender_id
	 * @return
	 * @throws SQLException
	 */
	private List<String> getDeviceInfo(Connection connection, String calender_id) throws SQLException{
		List<String> list = new ArrayList<String>();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			String query = "SELECT * FROM T_DEVICE_INFO WHERE CALENDER_ID = ? AND DELETE_FLG = 0;";
			preparedStatement = connection.prepareStatement(query);
			int index = 1;
			preparedStatement.setString(index++, calender_id);
			rs = preparedStatement.executeQuery();
			while(rs.next()){
			    list.add(rs.getString("device_id"));
			    list.add(rs.getString("add_sttime"));
			    list.add(rs.getString("add_edtime"));
			}
		}
		catch(Exception e) {
			System.out.println(e);
	    }finally{
			//Statement,ResultSetのみクローズ
	    	mngDBAccees.close(null,preparedStatement, rs);
	    }
		//return res;
		return list;
	}

	/**
	 * T_DEVICE_INFOテーブルのデータ更新(delete_flgの更新)
	 * @param calender_id
	 * @throws SQLException
	 */
	public void deleteDeviceInfo(String calender_id) throws SQLException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.mngDBAccees.getConnection();

			StringBuilder query = new StringBuilder();
			query.append("UPDATE T_DEVICE_INFO ");
			query.append("SET DELETE_FLG ");
			query.append("= ");
			query.append("'1' ");
			query.append("WHERE CALENDER_ID ");
			query.append("= ");
			query.append("?");
			query.append(";");
			preparedStatement = connection.prepareStatement(query.toString());
			// 自動コミット:OFF
			connection.setAutoCommit(false);
			int index = 1;
			preparedStatement.setString(index++, calender_id);

			//logger.info(query.toString());
			preparedStatement.executeUpdate();
			// 手動コミット ＆ 自動コミット:ON
			connection.commit();
			connection.setAutoCommit(true);

		}catch(Exception e){
			logger.error("# Failed in inserting the data(s) to database.", e);
			try {
				if(connection != null){
					connection.rollback();
				}
			} catch(Exception e1) {
			}
			throw e;
		}finally{
			// DB切断
			mngDBAccees.close(connection, preparedStatement);

	    }
	}

	/**
	 * T_DEVICE_INFOテーブルから全データ取得
	 * @param
	 * @return
	 * @throws SQLException
	 */
	public List<String> getDeviceAllUser() throws SQLException{
		//logger.info("START : T_DEVICE_INFOテーブルから全データ取得");
		List<String> res = null;
		Connection connection = null;
		try {
			connection = this.mngDBAccees.getConnection();
			res = getDeviceAllUser(connection);
	    }finally{
			mngDBAccees.close(connection);
	    }
		return res;
	}

	/**
	 * T_DEVICE_INFOテーブルから全データ取得(コネクション継続)
	 * @param connection
	 * @param disagree_mail
	 * @return
	 * @throws SQLException
	 */
	private List<String> getDeviceAllUser(Connection connection) throws SQLException{
		//logger.info("START : T_DISAGREE_USERテーブルから全データ取得(コネクション継続)");
		List<String> list = new ArrayList<String>();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			String query = "SELECT * FROM T_DEVICE_INFO WHERE DELETE_FLG = 0;";
			preparedStatement = connection.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			logger.info(rs);
			while(rs.next()){
			    list.add(rs.getString("calender_id"));
			    list.add(rs.getString("device_id"));
			    list.add(rs.getString("add_sttime"));
			    list.add(rs.getString("add_edtime"));
			}
		}
		catch(Exception e) {
			System.out.println(e);
	    }finally{
			//Statement,ResultSetのみクローズ
	    	mngDBAccees.close(null,preparedStatement, rs);
	    }
		//return res;
		return list;
	}

	/* === T_DEVICE_INFOテーブル関連 END ======================================================== */

	/* === T_EVENT_INFOテーブル関連 START ======================================================= */
	/**
	 * T_EVENT_INFOテーブルにデータ新規登録
	 * @param event_id
	 * @param calender_id
	 * @param start_datetime
	 * @param end_datetime
	 * @param member
	 * @throws SQLException
	 */
	public void insertEventInfo(String event_id, String calender_id, String start_datetime, String end_datetime, String user_id, String mail_addr) throws SQLException{
		//logger.info("START : T_DEVICE_INFOテーブルにデータ新規登録");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.mngDBAccees.getConnection();

			//reqID取得
			long id = loadSampleMaxID(connection, "T_EVENT_INFO") + 1;

			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO T_EVENT_INFO(");
			query.append("id,");
			query.append("event_id,");
			query.append("calender_id,");
			query.append("start_datetime,");
			query.append("end_datetime,");
			query.append("user_id,");
			query.append("mail_addr,");
			query.append("delete_flg");
			query.append(") VALUES (");
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('0').append(')');
			query.append(';');
			preparedStatement = connection.prepareStatement(query.toString());
			// 自動コミット:OFF
			connection.setAutoCommit(false);
			int index = 1;
			preparedStatement.setLong(index++, id);
			preparedStatement.setString(index++, event_id);
			preparedStatement.setString(index++, calender_id);
			preparedStatement.setString(index++, start_datetime);
			preparedStatement.setString(index++, end_datetime);
			preparedStatement.setString(index++, user_id);
			preparedStatement.setString(index++, mail_addr);
			//logger.info(query.toString());
			preparedStatement.executeUpdate();
			// 手動コミット ＆ 自動コミット:ON
			connection.commit();
			connection.setAutoCommit(true);

		}catch(Exception e){
			logger.error("# Failed in inserting the data(s) to database.", e);
			try {
				if(connection != null){
					connection.rollback();
				}
			} catch(Exception e1) {
			}
			throw e;
		}finally{
			// DB切断
			mngDBAccees.close(connection, preparedStatement);
	    }
	}

	/**
	 * T_EVENT_INFOテーブルからデータ取得
	 * @param eventId
	 * @return
	 * @throws SQLException
	 */
	public List<String> getEventInfo(String eventId) throws SQLException{
		List<String> res = null;
		Connection connection = null;
		try {
			connection = this.mngDBAccees.getConnection();
			res = getEventInfo(connection, eventId);
	    }finally{
			mngDBAccees.close(connection);
	    }
		return res;
	}


	/**
	 * T_EVENT_INFOテーブルからデータ取得(コネクション継続)
	 * @param connection
	 * @param eventId
	 * @return
	 * @throws SQLException
	 */
	private List<String> getEventInfo(Connection connection, String eventId) throws SQLException{
		List<String> list = new ArrayList<String>();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		//logger.info("入力EVENT_ID" + eventId);
		try {
			String query = "SELECT * FROM T_EVENT_INFO WHERE EVENT_ID = ? AND DELETE_FLG = 0;";
			preparedStatement = connection.prepareStatement(query);
			int index = 1;
			preparedStatement.setString(index++, eventId);
			rs = preparedStatement.executeQuery();
			//logger.info(query.toString());
			// データ格納
			while(rs.next()){
				//logger.info("取得するデータ EVENT_ID=" + eventId + "USER_ID=" + rs.getString("user_id"));
			    list.add(rs.getString("calender_id"));
			    list.add(rs.getString("start_datetime"));
			    list.add(rs.getString("end_datetime"));
			    list.add(rs.getString("user_id"));
			    list.add(rs.getString("mail_addr"));
			    //logger.info("Listの内容⇒" + list);
			}
		}
		catch(Exception e) {
			System.out.println(e);
	    }finally{
			//Statement,ResultSetのみクローズ
	    	mngDBAccees.close(null,preparedStatement, rs);
	    }
		//return res;
		return list;
	}

	/**
	 * T_EVENT_INFOテーブルのデータ更新(delete_flgの更新)
	 * @param event_id
	 * @throws SQLException
	 */
	public void deleteEventInfo(String event_id) throws SQLException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		//logger.info("TRACE : deleteEventInfo START");
		try {
			connection = this.mngDBAccees.getConnection();

			StringBuilder query = new StringBuilder();
			query.append("UPDATE T_EVENT_INFO ");
			query.append("SET DELETE_FLG ");
			query.append("= ");
			query.append("'1' ");
			query.append("WHERE EVENT_ID ");
			query.append("= ");
			query.append("?");
			query.append(";");
			preparedStatement = connection.prepareStatement(query.toString());
			// 自動コミット:OFF
			connection.setAutoCommit(false);
			int index = 1;
			preparedStatement.setString(index++, event_id);
			//logger.info(event_id);
			//logger.info(query.toString());
			preparedStatement.executeUpdate();
			// 手動コミット ＆ 自動コミット:ON
			connection.commit();
			connection.setAutoCommit(true);

		}catch(Exception e){
			logger.error("# Failed in inserting the data(s) to database.", e);
			try {
				if(connection != null){
					connection.rollback();
				}
			} catch(Exception e1) {
			}
			throw e;
		}finally{
			// DB切断
			mngDBAccees.close(connection, preparedStatement);

	    }
	}

	/* === T_EVENT_INFOテーブル関連 END ========================================================= */

	/* === T_DISAGREE_USERテーブル関連 START ==================================================== */
	/**
	 * T_DISAGREE_USERテーブルにデータ新規登録
	 * @param disagree_mail
	 * @throws SQLException
	 */
	public void insertDisagreeUser(String disagree_mail) throws SQLException{
		//logger.info("START : T_DEVICE_INFOテーブルにデータ新規登録");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.mngDBAccees.getConnection();

			//reqID取得
			long id = loadSampleMaxID(connection, "T_DISAGREE_USER") + 1;

			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO T_DISAGREE_USER(");
			query.append("id,");
			query.append("mail_addr,");
			query.append("delete_flg");
			query.append(") VALUES (");
			query.append('?').append(',');
			query.append('?').append(',');
			query.append('0').append(')');
			query.append(';');
			preparedStatement = connection.prepareStatement(query.toString());
			// 自動コミット:OFF
			connection.setAutoCommit(false);
			int index = 1;
			preparedStatement.setLong(index++, id);
			preparedStatement.setString(index++, disagree_mail);
			//logger.info(query.toString());
			preparedStatement.executeUpdate();
			// 手動コミット ＆ 自動コミット:ON
			connection.commit();
			connection.setAutoCommit(true);

		}catch(Exception e){
			logger.error("# Failed in inserting the data(s) to database.", e);
			try {
				if(connection != null){
					connection.rollback();
				}
			} catch(Exception e1) {
			}
			throw e;
		}finally{
			// DB切断
			mngDBAccees.close(connection, preparedStatement);
	    }
	}

	/**
	 * T_DISAGREE_USERテーブルから全データ取得
	 * @param
	 * @return
	 * @throws SQLException
	 */
	public List<String> getDisagreeAllUser() throws SQLException{
		//logger.info("START : T_DISAGREE_USERテーブルから全データ取得");
		List<String> res = null;
		Connection connection = null;
		try {
			connection = this.mngDBAccees.getConnection();
			res = getDisagreeAllUser(connection);
	    }finally{
			mngDBAccees.close(connection);
	    }
		return res;
	}

	/**
	 * T_DISAGREE_USERテーブルから全データ取得(コネクション継続)
	 * @param connection
	 * @param disagree_mail
	 * @return
	 * @throws SQLException
	 */
	private List<String> getDisagreeAllUser(Connection connection) throws SQLException{
		//logger.info("START : T_DISAGREE_USERテーブルから全データ取得(コネクション継続)");
		List<String> list = new ArrayList<String>();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			String query = "SELECT * FROM T_DISAGREE_USER WHERE DELETE_FLG = 0;";
			preparedStatement = connection.prepareStatement(query);
			rs = preparedStatement.executeQuery();
			logger.info(rs);
			while(rs.next()){
			    list.add(rs.getString("mail_addr"));
			}
		}
		catch(Exception e) {
			System.out.println(e);
	    }finally{
			//Statement,ResultSetのみクローズ
	    	mngDBAccees.close(null,preparedStatement, rs);
	    }
		//return res;
		return list;
	}

	/**
	 * T_DISAGREE_USERテーブルから個別データ取得
	 * @param disagree_mail
	 * @return
	 * @throws SQLException
	 */
	public List<String> getDisagreeUser(String disagree_mail) throws SQLException{
		//logger.info("START : T_DISAGREE_USERテーブルから個別データ取得");
		List<String> res = null;
		Connection connection = null;
		try {
			connection = this.mngDBAccees.getConnection();
			res = getDisagreeUser(connection, disagree_mail);
	    }finally{
			mngDBAccees.close(connection);
	    }
		return res;
	}

	/**
	 * T_DISAGREE_USERテーブルから個別データ取得(コネクション継続)
	 * @param connection
	 * @param disagree_mail
	 * @return
	 * @throws SQLException
	 */
	private List<String> getDisagreeUser(Connection connection, String disagree_mail) throws SQLException{
		//logger.info("START : T_DISAGREE_USERテーブルから個別データ取得(コネクション継続)");
		List<String> list = new ArrayList<String>();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			String query = "SELECT * FROM T_DISAGREE_USER WHERE MAIL_ADDR = ? AND DELETE_FLG = 0;";
			preparedStatement = connection.prepareStatement(query);
			int index = 1;
			preparedStatement.setString(index++, disagree_mail);
			logger.info(query.toString());
			rs = preparedStatement.executeQuery();
			while(rs.next()){
			    list.add(rs.getString("mail_addr"));
			}
		}
		catch(Exception e) {
			System.out.println(e);
	    }finally{
			//Statement,ResultSetのみクローズ
	    	mngDBAccees.close(null,preparedStatement, rs);
	    }
		//return res;
		return list;
	}

	/**
	 * T_DISAGREE_USERテーブルのデータ更新(delete_flgの更新)
	 * @param disagree_mail
	 * @throws SQLException
	 */
	public void deleteDisagreeUser(String mail) throws SQLException{
		//logger.info("START : T_DISAGREE_USERテーブルのデータ更新(delete_flgの更新)");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.mngDBAccees.getConnection();

			StringBuilder query = new StringBuilder();
			query.append("UPDATE T_DISAGREE_USER ");
			query.append("SET DELETE_FLG ");
			query.append("= ");
			query.append("'1' ");
			query.append("WHERE MAIL_ADDR ");
			query.append("= ");
			query.append("?");
			query.append(";");
			preparedStatement = connection.prepareStatement(query.toString());
			// 自動コミット:OFF
			connection.setAutoCommit(false);
			int index = 1;
			preparedStatement.setString(index++, mail);

			logger.info(query.toString());
			preparedStatement.executeUpdate();
			// 手動コミット ＆ 自動コミット:ON
			connection.commit();
			connection.setAutoCommit(true);

		}catch(Exception e){
			logger.error("# Failed in inserting the data(s) to database.", e);
			try {
				if(connection != null){
					connection.rollback();
				}
			} catch(Exception e1) {
			}
			throw e;
		}finally{
			// DB切断
			mngDBAccees.close(connection, preparedStatement);

	    }
	}

	/* === T_DISAGREE_USERテーブル関連 END ====================================================== */

	/**
	 * ID最大値取得
	 * @param statement
	 * @return
	 * @throws SQLException
	 */
	private long loadSampleMaxID(Connection connection, String dbname) throws SQLException {
		long res = 0;
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.createStatement();
			String query = "SELECT MAX(id) as max_id FROM " + dbname + ";";
			rs = statement.executeQuery(query);
			// データ格納
			if(rs.next()) {
				res = rs.getLong("max_id");
			}
	    }finally{
			//Statement,ResultSetのみクローズ
	    	mngDBAccees.close(null,statement, rs);
	    }
		return res;
	}

}
