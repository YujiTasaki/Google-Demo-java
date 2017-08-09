package jp.co.kke.Lockstatedemo.mng.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface MngDBAccees {

	public static final String S_DB_TYPE_SQLITE = "LITE";
	public static final String S_DB_TYPE_POSTGRESQL = "PG";

	/**
	 * 終了処理
	 */
	public void stop();
	/**
	 * プーリングされたコネクション(closeでプーリングに戻る)
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException;

	/**
	 * Connection のクローズ
	 * ＊Connectionはプーリングに戻る
	 * @param connection
	 * @throws Exception
	 */
	public void close(Connection connection);
	/**
	 * Connection Statement のクローズ
	 * ＊Connectionはプーリングに戻る
	 * @param connection
	 * @param statement
	 * @throws Exception
	 */
	public void close(Connection connection,Statement statement);
	/**
	 * Connection Statement ResultSet のクローズ
	 * ＊Connectionはプーリングに戻る
	 * @param connection
	 * @param statement
	 * @param resultset
	 * @throws Exception
	 */
	public void close(Connection connection,Statement statement, ResultSet resultset);

}
