package jp.co.kke.Lockstatedemo.mng;

public class MsgException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public MsgException() {
		super();
	}

	public MsgException(String message, Throwable cause) {
		super(message, cause);
	}

	public MsgException(String message) {
		super(message);
	}

	public MsgException(Throwable cause) {
		super(cause);
	}
}
