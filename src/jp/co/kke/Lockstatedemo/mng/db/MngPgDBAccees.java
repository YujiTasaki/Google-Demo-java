package jp.co.kke.Lockstatedemo.mng.db;

import java.util.Timer;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import jp.co.kke.Lockstatedemo.util.SysParamUtil;

public class MngPgDBAccees extends AbstractMngDBAccees{
	/**
	 * SQL JDBCドライバ
	 */
	private static final String S_JDBC_DRIVER_NAME = "org.postgresql.Driver";
	/**
	 * SQL JDBC URLヘッダ文字列
	 */
	private static final String S_JDBC_HEADER = "jdbc:postgresql:";

	/**
	 * データベース名
	 */
	private String dbName;

	/**
	 * コンストラクタ
	 * @param dbName
	 * @throws ClassNotFoundException
	 */
	public MngPgDBAccees(String dbName) throws ClassNotFoundException {
		super();
		this.dbName = dbName;
		init();
	}

	/**
	 * 初期化
	 * @param dbName
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void init() throws ClassNotFoundException{
		Class.forName(S_JDBC_DRIVER_NAME);
		//DataSourceプーリングを使用する
		GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
		//同時にプールから取り出すことのできるオブジェクトの最大数を制御。
		poolConfig.maxActive = SysParamUtil.getResourceInt("DB_POOLING_MAX_ACTIVE", 21);
		//プール内に保持できる未使用のオブジェクトの最大数を制御
		poolConfig.maxIdle = SysParamUtil.getResourceInt("DB_POOLING_MAX_IDLE", 2);
		//プール内に存在可能なアイドル接続数の最大数を指定
		//接続切断時、DBCPはこの接続をプール内に溜めようとしますが
		//その時プール内にこの数以上の接続が存在した場合、接続は溜められず削除
		poolConfig.minIdle = SysParamUtil.getResourceInt("DB_POOLING_MIN_IDLE", 10);//
		//プーリングのウエイト(msec)
		poolConfig.maxWait = SysParamUtil.getResourceInt("DB_POOLING_MAX_WAIT",6000);
		if(SysParamUtil.getResourceBoolean("DB_POOLING_WHEN_EXHAUSTED_BLOCK")){
			poolConfig.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
		}
		this.connectionPool = new GenericObjectPool(null, poolConfig);

		//ConnectionFactoryインスタンス
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(getUrl(), getDbId(),getDbPass());

		//PreparedStatementオブジェクトをプーリングする場合、
		//プーリング用のKeyedObjectPoolオブジェクトを生成するためのKeyedObjectPoolFactoryインスタンス。
		KeyedObjectPoolFactory stmtPoolFactory = null;
		//Connectionが有効であるかどうかを検査するためのSQL文
		String validationQuery = null;
		//プールから取り出されたConnectionを読み込み専用にする
		boolean defaultReadOnly = false;
		//プールから取り出されたConnectionを自動コミットモードにする
		boolean defaultAutoCommit = true;

		new PoolableConnectionFactory(connectionFactory, connectionPool, stmtPoolFactory, validationQuery, defaultReadOnly, defaultAutoCommit);
		this.dataSource = new PoolingDataSource(this.connectionPool);

		//定期バキューム用タイマ
		long vacumInterval = SysParamUtil.getResourceLong("DB_VACUUM_INTERVAL_MILLIS",0);
		if(vacumInterval > 0){
			this.vacuumTimer = new Timer();
			this.vacuumTimer.scheduleAtFixedRate(new VacuumTask(this), vacumInterval,  vacumInterval);
		}
	}

	/**
	 * @return dbName
	 */
	public String getDbName() {
		return dbName;
	}
	/**
	 * @return dbID
	 */
	public String getDbId() {
		return SysParamUtil.getResourceString("DB_ID");
	}

	/**
	 * @return dbPass
	 */
	public String getDbPass() {
		return SysParamUtil.getResourceString("DB_PASS");
	}

	/**
	 * @return dbHost
	 */
	public String getDbHost() {
		return SysParamUtil.getResourceString("DB_HOST", "localhost");
	}

	/**
	 * @return dbPort
	 */
	public int getDbPort() {
		return SysParamUtil.getResourceInt("DB_PORT", 5432);
	}

	/**
	 * データベースURL取得
	 * @param dbUrl
	 * @return
	 */
	private String getUrl(){
		StringBuilder res = new StringBuilder();
		res.append(S_JDBC_HEADER);
		res.append("//");
		res.append(getDbHost());
		res.append(':');
		res.append(getDbPort());
		res.append('/');
		res.append(getDbName());
		return res.toString();
	}
}
