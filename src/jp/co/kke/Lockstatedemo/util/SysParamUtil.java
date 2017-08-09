package jp.co.kke.Lockstatedemo.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

/**
 * System Param Propertis File Maneger Class
 * システム関連プロパティ管理クラス
 */
public class SysParamUtil {
	/**
	 * System Param Propertis File Name
	 * System関連プロパティーファイル名
	 */
	public static String S_SYSTEM_PARAM_PROPERTIS_FILE = "sys_param";

	/**
	 * EoD ConfDir System.getProperty() Key
	 * System.getProperty()で取得するEoDシステムconfディレクトリキー
	 */
//	public static final String S_SYSTEM_PROPERTIES_CONF_DIR = "eod.conf.dir";

	/**
	 * ResourceBundle取得
	 * ＊環境変数に"eod.conf.dir"でconfディレクトリ指定がある場合は ディレクトリを探し、
	 * もしディレクトリが存在すれば[confディレクトリ]/modejudge_param.propertiesファイルを参照する。
	 * 環境変数無指定もしくは当該ディレクトリがなければ、jarファイル内を含めたクラスパス中のmodejudge_param.propertiesを参照する。
	 * @return
	 */
	public static ResourceBundle getResourceBundle(){
//		ResourceBundle res = null;
//		String confDirName = System.getProperty(S_SYSTEM_PROPERTIES_CONF_DIR);
//		if(confDirName != null){
//			File confDir = Paths.get(confDirName).toFile();
//			if(confDir.exists() && confDir.isDirectory()){
//				URLClassLoader urlLoader;
//				try {
//					urlLoader = new URLClassLoader(new URL[]{confDir.toURI().toURL()});
//				} catch (MalformedURLException e) {//環境変数指定があり該当ディレクトリがあるのにエラー時はException
//					throw new IllegalStateException(String.format(" Can't found dir %s.", confDir), e);
//				}
//				res = ResourceBundle.getBundle("dictionary", Locale.getDefault(), urlLoader);
//			}
//		}
//		if(res == null){
//			res = ResourceBundle.getBundle(S_SYSTEM_PARAM_PROPERTIS_FILE);
//		}
		return ResourceBundle.getBundle(S_SYSTEM_PARAM_PROPERTIS_FILE);
	}

	/**
	 * Get Double Value From Resource
	 * バンドルリソースよりキー文字列に対応する格納文字列を検索して double として返す
	 * 参照するバンドルリソースファイル名は "sys_param.properties" である
	 * @param key キー文字列
	 * @return 格納値　
	 * @throws IllegalArgumentException 未設定、値変換不可等
	 */
	public static double getResourceDouble (String key) throws IllegalArgumentException{
		double aResult;
		try {
			String aValStr = getResourceBundle().getString(key);
			if(aValStr != null){
				aValStr = aValStr.trim();
			}
			aResult = Double.parseDouble(aValStr);
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format(" Can't get %s.properties. Key=%s", S_SYSTEM_PARAM_PROPERTIS_FILE, key), e);
		}
		return (aResult);
	}

	/**
	 * Get Double Value From Resource
	 * バンドルリソースよりキー文字列に対応する格納文字列を検索して double として返す
	 * 参照するバンドルリソースファイル名は "sys_param.properties" である
	 * @param key キー文字列
	 * @param defValue デフォルト値 (未設定時)
	 * @throws IllegalArgumentException 値変換不可等
	 */
	public static double getResourceDouble (String key, double defValue) throws IllegalArgumentException{
		double aResult = defValue;
		String aValStr = null;
		try {
			aValStr = getResourceBundle().getString(key);
			if(aValStr != null){
				aValStr = aValStr.trim();
			}
		} catch (Exception e) {//null時はデフォルト値
			return (defValue);
		}
		try {
			aResult = Double.parseDouble(aValStr);
		} catch (Exception e) {
			throw new IllegalArgumentException(" Can't parse " + S_SYSTEM_PARAM_PROPERTIS_FILE + ".properties. Key=" + key, e);
		}
		return (aResult);
	}

	/**
	 * Get Boolean Value From Resource
	 * バンドルリソースよりキー文字列に対応する格納文字列を検索して boolean として返す
	 * 参照するバンドルリソースファイル名は "sys_param.properties" である
	 * @param key キー文字列
	 * @return 格納値　
	 * @throws IllegalArgumentException 未設定、値変換不可等
	 */
	public static boolean getResourceBoolean (String key) throws IllegalArgumentException{
		boolean aResult;
		try {
			String aValStr = getResourceBundle().getString(key);
			if(aValStr != null){
				aValStr = aValStr.trim();
			}
			//aResult = Boolean.parseBoolean(aValStr);
			aResult = Boolean.valueOf(aValStr).booleanValue();
		} catch (Exception e) {
			throw new IllegalArgumentException(" Can't get " + S_SYSTEM_PARAM_PROPERTIS_FILE + ".properties. Key=" + key, e);
		}
		return (aResult);
	}

	/**
	 * Get Boolean Value From Resource
	 * バンドルリソースよりキー文字列に対応する格納文字列を検索して boolean として返す
	 * 参照するバンドルリソースファイル名は "sys_param.properties" である
	 * @param key キー文字列
	 * @return 格納値　
	 * @param defValue デフォルト値 (未設定時)
	 * @throws IllegalArgumentException 値変換不可等
	 */
	public static boolean getResourceBoolean (String key, boolean defValue) throws IllegalArgumentException{
		boolean aResult = defValue;
		String aValStr = null;
		try {
			aValStr = getResourceBundle().getString(key);
			if(aValStr != null){
				aValStr = aValStr.trim();
			}
		} catch (Exception e) {
			return (defValue);
		}
		try {
			//aResult = Boolean.parseBoolean(aValStr);
			aResult = Boolean.valueOf(aValStr).booleanValue();
		} catch (Exception e) {
			throw new IllegalArgumentException(" Can't parse " + S_SYSTEM_PARAM_PROPERTIS_FILE + ".properties. Key=" + key, e);
		}
		return (aResult);
	}


	/**
	 * Get Int Value From Resource
	 * バンドルリソースよりキー文字列に対応する格納文字列を検索して int として返す
	 * 参照するバンドルリソースファイル名は "sys_param.properties" である
	 * @param key キー文字列
	 * @return 格納値　
	 * @throws IllegalArgumentException 未設定、値変換不可等
	 */
	public static int getResourceInt (String key) throws IllegalArgumentException{
		int aResult;
		try {
			String aValStr = getResourceBundle().getString(key);
			if(aValStr != null){
				aValStr = aValStr.trim();
			}
			aResult = Integer.parseInt(aValStr);
		} catch (Exception e) {
			throw new IllegalArgumentException(" Can't get " + S_SYSTEM_PARAM_PROPERTIS_FILE + ".properties. Key=" + key, e);
		}
		return (aResult);
	}

	/**
	 * Get Int Value From Resource
	 * バンドルリソースよりキー文字列に対応する格納文字列を検索して int として返す
	 * 参照するバンドルリソースファイル名は "sys_param.properties" である
	 * @param key キー文字列
	 * @return 格納値　
	 * @param defValue デフォルト値 (未設定時)
	 * @throws IllegalArgumentException 値変換不可等
	 */
	public static int getResourceInt (String key, int defValue)  throws IllegalArgumentException{
		int aResult = defValue;
		String aValStr = null;
		try {
			ResourceBundle resource = ResourceBundle.getBundle(S_SYSTEM_PARAM_PROPERTIS_FILE);
			aValStr = resource.getString(key);
			if(aValStr != null){
				aValStr = aValStr.trim();
			}
		} catch (Exception e) {
			return (defValue);
		}
		try {
			aResult = Integer.parseInt(aValStr);
		} catch (Exception e) {
			throw new IllegalArgumentException(" Can't parse " + S_SYSTEM_PARAM_PROPERTIS_FILE + ".properties. Key=" + key, e);
		}
		return (aResult);
	}

	/**
	 * Get Long Value From Resource
	 * バンドルリソースよりキー文字列に対応する格納文字列を検索して long として返す
	 * 参照するバンドルリソースファイル名は "sys_param.properties" である
	 * @param key キー文字列
	 * @return 格納値　
	 * @throws IllegalArgumentException 未設定、値変換不可等
	 */
	public static long getResourceLong (String key) throws IllegalArgumentException{
		long aResult;
		try {
			String aValStr = getResourceBundle().getString(key);
			if(aValStr != null){
				aValStr = aValStr.trim();
			}
			aResult = Long.parseLong(aValStr);
		} catch (Exception e) {
			throw new IllegalArgumentException(" Can't get " + S_SYSTEM_PARAM_PROPERTIS_FILE + ".properties. Key=" + key, e);
		}
		return (aResult);
	}

	/**
	 * Get Long Value From Resource
	 * バンドルリソースよりキー文字列に対応する格納文字列を検索して long として返す
	 * 参照するバンドルリソースファイル名は "sys_param.properties" である
	 * @param key キー文字列
	 * @return 格納値　
	 * @param defValue デフォルト値 (未設定時)
	 * @throws IllegalArgumentException 値変換不可等
	 */
	public static long getResourceLong (String key, long defValue)  throws IllegalArgumentException{
		long aResult = defValue;
		String aValStr = null;
		try {
			aValStr = getResourceBundle().getString(key);
			if(aValStr != null){
				aValStr = aValStr.trim();
			}
		} catch (Exception e) {//null時はデフォルト値
			return (defValue);
		}
		try {
			aResult = Long.parseLong(aValStr);
		} catch (Exception e) {
			throw new IllegalArgumentException(" Can't parse " + S_SYSTEM_PARAM_PROPERTIS_FILE + ".properties. Key=" + key, e);
		}
		return (aResult);
	}
	/**
	 * Get String Value From Resource
	 * バンドルリソースよりキー文字列に対応する格納文字列を検索して得る<p>
	 * 参照するバンドルリソースファイル名は "sys_param.properties" である
	 * @param key キー文字列
	 * @return 格納値　
	 * @throws IllegalArgumentException 未設定、値変換不可等
	 */
	public static String getResourceString(String key) throws IllegalArgumentException{
		String sVal;	// キーに対応する値
		try {
			sVal = getResourceBundle().getString(key);
			if(sVal != null){
				sVal = sVal.trim();
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(" Can't get " + S_SYSTEM_PARAM_PROPERTIS_FILE + ".properties. Key=" + key, e);
		}
		return sVal;
	}

	/**
	 * Get String Value From Resource
	 * バンドルリソースよりキー文字列に対応する格納文字列を検索して得る<p>
	 * 参照するバンドルリソースファイル名は "sys_param.properties" である
	 * @param key キー文字列
	 * @return 格納値　
	 * @param defValue デフォルト値 (未設定時)
	 * @throws IllegalArgumentException 値変換不可等
	 */
	public static String getResourceString(String key, String defValue) throws IllegalArgumentException{
		String sVal = defValue;	// キーに対応する値
		try {
			sVal = getResourceBundle().getString(key);
			if(sVal != null){
				sVal = sVal.trim();
			}
		} catch (Exception e) {//null時はデフォルト値
			return (defValue);
		}
		return sVal;
	}

	/**
	 * Get Keys From Resource
	 * バンドルリソースよりキー文字列リスト取得
	 * @return バンドルリソースキー文字列リスト取得
	 */
	public static List<String> getResourceKeys(){
		List<String> res = new ArrayList<String>();
		Enumeration<String> enume = getResourceBundle().getKeys();
		while(enume.hasMoreElements()){
			res.add(enume.nextElement());
		}
		return res;
	}
}