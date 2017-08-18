package jp.co.kke.Lockstatedemo.mng;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

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
				res = rs.getString("key");
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
			long id = loadSampleMaxID(connection) + 1;

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
	 * ID最大値取得
	 * @param statement
	 * @return
	 * @throws SQLException
	 */
	private long loadSampleMaxID(Connection connection) throws SQLException {
		long res = 0;
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.createStatement();
			String query = "SELECT MAX(id) as max_id FROM T_SAMPLE;";
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

	public static void main(String[] args) throws MessagingException, IOException, MsgException, ClassNotFoundException, SQLException {
		String realPath = "D:\\Data\\ProgramData\\Eclipse\\workspaces\\Lock\\LockStateCalendar\\LockstateDemo\\WebContent";
		MngDbLockParam mngDbLockParam = new MngDbLockParam(realPath);
		mngDbLockParam.insertSample("test1", Calendar.getInstance());
		mngDbLockParam.insertSample("test2", Calendar.getInstance());
		mngDbLockParam.insertSample("test3", Calendar.getInstance());
		mngDbLockParam.insertSample("test4", Calendar.getInstance());

		String key = "key1";
		System.out.println(key + ":" + mngDbLockParam.getParam("key1"));
		String val = "val1";
		mngDbLockParam.setParam(key, val);
		System.out.println(key + ":" + mngDbLockParam.getParam("key1"));
		mngDbLockParam.setParam(key, null);
		System.out.println(key + ":" + mngDbLockParam.getParam("key1"));
		mngDbLockParam.setParam(key, val);
		System.out.println(key + ":" + mngDbLockParam.getParam("key1"));
		mngDbLockParam.close();
	}
}
