package org.hcesperer.utils;

import java.sql.Connection;

public interface DatabaseConsumer {
	/**
	 * This method must be called before the instance this method is called on
	 * can perform any SQL operations. The SQL operations to be performed should
	 * be performed immediately after this call, followed by a final
	 * releaseConnection call.
	 * 
	 * @param conn
	 *            Connection to use
	 */
	public void acquireConnection(Connection conn);

	/**
	 * This method must be called immediately after SQL operations have been
	 * performed, as to ensure the connection can be reused. After this method
	 * is called, the instance it was called in must not make further use of the
	 * connection.
	 * 
	 * If, at any point, more SQL operations are to be perofrmed,
	 * acquireConnection must be called again.
	 */
	public void releaseConnection();
}
