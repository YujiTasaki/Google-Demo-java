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

public class MngDbLockParam {

	/**
	 * ロガー
	 */
	private static Logger logger = Logger.getLogger(MngDbLockParam.class);

	private String dbName = SysParamUtil.getResourceString("DB_NAME");
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

		if(MngDBAccees.S_DB_TYPE_SQLITE.equals(dbType)){
			if(realPath == null){
				throw new IllegalArgumentException("DBrealPath==null" + dbType);
			}
			this.mngDBAccees = MngSqliteDBAccees.getInstance(realPath, dbName);
		}else if(MngDBAccees.S_DB_TYPE_POSTGRESQL.equals(dbType)){
			this.mngDBAccees = new MngPgDBAccees(dbName);
		}else{
			throw new IllegalStateException("can't match dbType:" + dbType);
		}
	}
	public void close(){
		this.mngDBAccees.stop();
	}

	public synchronized void insertSample(String name, Calendar cal) throws SQLException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.mngDBAccees.getConnection();

			//reqID取得
			long id = loadMaxID(connection) + 1;

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
	private long loadMaxID(Connection connection) throws SQLException {
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
	}
}
