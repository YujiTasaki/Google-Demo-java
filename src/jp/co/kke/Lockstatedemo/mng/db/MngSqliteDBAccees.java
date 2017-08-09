package jp.co.kke.Lockstatedemo.mng.db;

import java.io.File;
import java.util.Timer;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.JournalMode;
import org.sqlite.SQLiteConfig.LockingMode;
import org.sqlite.SQLiteConfig.SynchronousMode;

import jp.co.kke.Lockstatedemo.util.SysParamUtil;

/**
 * SQLite用データベースアクセス管理クラス
 */
public class MngSqliteDBAccees extends AbstractMngDBAccees{

	private static MngSqliteDBAccees instance  = null;
	/**
	 * SQL JDBCドライバ
	 */
	private static final String S_JDBC_DRIVER_NAME = "org.sqlite.JDBC";
	/**
	 * SQL JDBC URLヘッダ文字列
	 */
	private static final String S_JDBC_HEADER = "jdbc:sqlite:";

	/**
	 * データベースファイルパス
	 */
	private String dbPath;

	public static MngDBAccees getInstance(String realPath, String dbName) throws ClassNotFoundException{
		if(instance == null){
			instance = new MngSqliteDBAccees(realPath, dbName);
		}
		return instance;
	}

	/**
	 * コンストラクタ
	 * @param dbName
	 * @throws ClassNotFoundException
	 */
	private MngSqliteDBAccees(String realPath, String dbName) throws ClassNotFoundException {
		super();
		this.dbPath = getDbPath(realPath, dbName);
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
		//Sqliteはデータベースファイルあたり同時アクセス１ユーザのみなので、
		//ここではコネクションの状態チェックやWait機能の為使用する。
		GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
		poolConfig.maxActive = 1;//プーリングは１つのみ
		poolConfig.maxIdle = 1;//同上
		poolConfig.minIdle = 1;//同上
		poolConfig.maxWait = SysParamUtil.getResourceInt("DB_POOLING_MAX_WAIT");//プーリングのウエイト(msec)
		this.connectionPool = new GenericObjectPool(null, poolConfig);

		SQLiteConfig config = new SQLiteConfig();
		config.setSharedCache(true);					// 共有キャッシュを許可(def:false)
		config.setReadUncommited(true); 				// 読み込み処理はコミットしない
		config.setCacheSize(SysParamUtil.getResourceInt("DB_SQLITE_CACHE_SIZE")); 			// キャッシュサイズ(64k)(def:10000)
		config.setBusyTimeout(SysParamUtil.getResourceString("DB_SQLITE_BUSY_TIMEOUT"));	// テーブルがロック時の待ち時間(msec) (def:5000)
		//config.setLockingMode(LockingMode.NORMAL);	// 複数のユーザーがデータベース ファイルにアクセス可能。が、複数ユーザアクセスするとロックエラー発生。単一ユーザならExclusiveの方が速い。(def:Exclusive)
		config.setLockingMode(LockingMode.EXCLUSIVE);	// 単一ユーザのみデータベースアクセス可能。Exclusiveの方が速い。(def:Exclusive)
		config.setJournalMode(JournalMode.WAL);			// ジャーナルモードをWAL(write ahead log)に
		config.setSynchronous(SynchronousMode.NORMAL);	// データベース ファイルを含むインメモリ キャッシュのデータベース接続同期モードを設定。すべての重大な局面で同期を行うが頻度低し(def:OFF)
		//下記設定が最速と思われるが、ファイル同期を取らなくなる
		//config.setJournalMode(JournalMode.MEMORY);	// ジャーナルモードをMemoryに
		//config.setSynchronous(SynchronousMode.OFF);	// データベース ファイルを含むインメモリ キャッシュのデータベース接続同期モードを設定。同期をとらない。JournalMode.MEMORY時はOFFでなければ高速化効果低し(def:OFF)
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(getUrl(), config.toProperties());
		new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
		this.dataSource = new PoolingDataSource(this.connectionPool);

		//定期バキューム用タイマ
		long vacumInterval = SysParamUtil.getResourceLong("DB_VACUUM_INTERVAL_MILLIS",0);
		if(vacumInterval > 0){
			this.vacuumTimer = new Timer();
			this.vacuumTimer.scheduleAtFixedRate(new VacuumTask(this), vacumInterval,  vacumInterval);
		}
	}

	private String getDbPath(String realPath, String dbName){
		StringBuilder res = new StringBuilder();
		res.append(realPath)
			.append(File.separator)
			.append(SysParamUtil.getResourceString("DB_SQLITE_DIR_PATH"))
			.append(File.separator)
			.append(dbName)
			.append(".db");
		return res.toString();
	}

	/**
	 * データベースURL取得
	 * @param dbName
	 * @return
	 */
	private String getUrl(){
		StringBuilder res = new StringBuilder();
		res.append(S_JDBC_HEADER);
		res.append(getDBPath());
		return res.toString();
	}

	/**
	 * データベースファイルパス取得
	 * @param dbName
	 * @return
	 */
	private String getDBPath(){
		return (new File(dbPath)).getAbsolutePath();
	}


}
