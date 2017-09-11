package jp.co.kke.Lockstatedemo.mng;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.mail.MessagingException;

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
		String res = null;
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


	/**
	 * 練習用
	 * @param args
	 * @throws MessagingException
	 * @throws IOException
	 * @throws MsgException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws MessagingException, IOException, MsgException, ClassNotFoundException, SQLException {
		//String realPath = "D:\\Data\\ProgramData\\Eclipse\\workspaces\\Lock\\LockStateCalendar\\LockstateDemo\\WebContent";

		//DBにイベントIDとユーザーIDを登録
		String REAL_PATH        = SysParamUtil.getResourceString("DIR_PATH");
		MngDbLockParam mngDbLockParam = new MngDbLockParam(REAL_PATH);
		mngDbLockParam.insertEventUser("1", "2");

		//DB検索
		//String res = mngDbLockParam.getParam("key1");
		List<String> res2 = mngDbLockParam.getEventUser("1");
		System.out.println(res2);

		mngDbLockParam.updateEventUser("2");

		List<String> res3 = mngDbLockParam.getEventUser("1");
		System.out.println(res3);


//		String realPath = "C:\\workspace\\LockstateDemo\\WebContent";
//		MngDbLockParam mngDbLockParam = new MngDbLockParam(realPath);
//		mngDbLockParam.insertSample("test1", Calendar.getInstance());
//		mngDbLockParam.insertSample("test2", Calendar.getInstance());
//		mngDbLockParam.insertSample("test3", Calendar.getInstance());
//		mngDbLockParam.insertSample("test4", Calendar.getInstance());
//
//		String key = "key1";
//		System.out.println(key + ":" + mngDbLockParam.getParam("key1"));
//		String val = "val1";
//		mngDbLockParam.setParam(key, val);
//		System.out.println(key + ":" + mngDbLockParam.getParam("key1"));
//		mngDbLockParam.setParam(key, null);
//		System.out.println(key + ":" + mngDbLockParam.getParam("key1"));
//		mngDbLockParam.setParam(key, val);
//		System.out.println(key + ":" + mngDbLockParam.getParam("key1"));
//		mngDbLockParam.close();
	}
}
