package jp.co.kke.Lockstatedemo.mng.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;

public abstract class AbstractMngDBAccees implements MngDBAccees{


	/**
	 * プーリング用データソース(closeでプーリングに戻る)
	 */
	protected PoolingDataSource dataSource = null;

	/**
	 * 定期バキューム用タイマ
	 */
	protected Timer vacuumTimer = null;

	/**
	 * コネクションプール
	 */
	@SuppressWarnings("rawtypes")
	protected ObjectPool connectionPool = null;


	/**
	 * 終了処理
	 */
	@Override
	public void stop(){
		clearPool();
		closePool();
		if(this.vacuumTimer != null){
			this.vacuumTimer.cancel();
		}
	}

	private void clearPool(){
		try {
			this.connectionPool.clear();
		} catch (Exception e) {
		}
	}

	private void closePool(){
		try {
			this.connectionPool.close();
		} catch (Exception e) {
		}
	}
	/**
	 * プーリングされたコネクション(closeでプーリングに戻る)
	 * @return
	 * @throws SQLException
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}

	/**
	 * Connection のクローズ
	 * ＊Connectionはプーリングに戻る
	 * @param connection
	 * @throws Exception
	 */
	@Override
	public void close(Connection connection){
		close(connection, null, null);
	}
	/**
	 * Connection Statement のクローズ
	 * ＊Connectionはプーリングに戻る
	 * @param connection
	 * @param statement
	 * @throws Exception
	 */
	@Override
	public void close(Connection connection,Statement statement){
		close(connection, statement, null);
	}


	/**
	 * Connection Statement ResultSet のクローズ
	 * ＊Connectionはプーリングに戻る
	 * @param connection
	 * @param statement
	 * @param resultset
	 * @throws Exception
	 */
	@Override
	public void close(Connection connection, Statement statement, ResultSet resultset){
//		Exception exception = null;
		if(resultset != null){
			try {
				resultset.close();
	        }catch(Exception e){
	        	//exception = e;
			}
		}
		if(statement!= null){
			try {
				statement.close();
	        }catch(Exception e){
//	        	if(exception == null){
//	        		exception = e;
//	        	}else{
//	        		exception.addSuppressed(e);
//	        	}
			}
		}
		if(connection != null){
	        try {
				 connection.close();
	        }catch(Exception e){
//	        	if(exception == null){
//	        		exception = e;
//	        	}else{
//	        		exception.addSuppressed(e);
//	        	}
			}
		}
//		if(exception != null){//全て
//			throw exception;
//		}
	}

	/**
	 * Vacuum処理
	 */
	protected synchronized void vacuum(){
		Connection connection = null;
		Statement statement = null;
		try {
			connection = this.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate("vacuum");
		}catch(SQLException e){
			try {
				connection.rollback();
			} catch(SQLException e1) {
			}
		}finally{
			// DB切断
			close(connection,statement);
		}
	}

	protected class VacuumTask extends TimerTask {
		private AbstractMngDBAccees mngDBAccees;
		public VacuumTask(AbstractMngDBAccees mngDBAccees) {
			super();
			this.mngDBAccees = mngDBAccees;
		}
		@Override
		public void run() {
			this.mngDBAccees.vacuum();
		}
	}

}
