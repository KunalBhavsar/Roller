package com.rollingscenes.src.exceptions;

public class BackgroundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final int tid;
    final String threadName;

    /**
     * @param e original exception
     * @param tid id of the thread where exception occurred
     * @param threadName name of the thread where exception occurred
     */
    public BackgroundException(Throwable e, int tid, String threadName) {
        super(e);
        this.tid = tid;
        this.threadName = threadName;
    }
}
