/**
 * CCCamp07 ScoringSystem
 * A CTF scoring bot & flag+advisory reporting system
 *
 * (C) 2007, Hans-Christian Esperer
 * hc at hcespererorg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 * * Neither the name of the H. Ch. Esperer nor the names of his
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * POSSIBILITY OF SUCH DAMAGE
 **************************************************************************/

package de.sectud.ctf07.scoringsystem;

/**
 * List of functions that may be called during a telnet session.
 * 
 * You call these functions by issuing their name, followed by the specified
 * parameters. Some functions are limited to admins, that means you have to
 * authenticate your session by calling admin() before calling any of the other
 * functions.
 * 
 * @author Hans-Christian Esperer
 * 
 */
public enum ClientFunctions {

	/**
	 * <h2>boolean reportflag(int teamID, String flag)</h2>
	 * 
	 * <p>
	 * Report a flag.
	 * </p>
	 */
	reportflag,

	/**
	 * <h2>Manual help()</h2>
	 * 
	 * <p>
	 * Return an instance of class Manual
	 * </p>
	 */
	help,

	/**
	 * <h2>void quit()</h2>
	 * 
	 * <p>
	 * Terminate telnet session
	 * </p>
	 */
	quit,

	/**
	 * <h2>void log(String stringToLog)</h2>
	 * 
	 * <p>
	 * Log something. Disabled, except for admins.
	 * </p>
	 */
	log,

	/**
	 * <h2>String readtext()</h2>
	 * 
	 * <p>
	 * Read text from stdin, until finished with '^\.$'
	 * </p>
	 */
	readtext,

	/**
	 * <h2>void repordadvisory(int teamID, String advisory)</h2>
	 * 
	 * <p>
	 * Report an advisory. Usually, you call this function like this:
	 * </p>
	 * 
	 * <pre>
	 * reportadvisory($TEAMID, readtext())
	 * </pre>
	 * 
	 * <p>
	 * Refer to the readtext() documentation if you want to know why that is so.
	 * </p>
	 */
	reportadvisory,

	/**
	 * <h2>boolean admin(optional String password)</h2>
	 * 
	 * <p>
	 * Become admin. When called from an interactive session, it is recommended
	 * not to specify the password. If called without a password, the admin()
	 * function will issue telnet command characters to disable local echo, to
	 * allow the password to be typed in in a safer way.
	 * </p>
	 */
	admin,

	/**
	 * <h2>void ladv()</h2>
	 * 
	 * <p>
	 * List all pending advisories.
	 * </p>
	 * 
	 * <p>
	 * Limited to admins.
	 * </p>
	 */
	ladv,

	/**
	 * <h2>void reject(int advisoryID, String reason)</h2>
	 * 
	 * <p>
	 * Reject an advisory.
	 * </p>
	 * 
	 * <p>
	 * The CTF organizer should have given you a detailed explanation on which
	 * advisories to reject, which ones to accept, and how to grade them.
	 * </p>
	 * 
	 * <p>
	 * Limited to admins.
	 * </p>
	 */
	reject,

	/**
	 * <h2>void accept(int advisoryID, int pointsToAwars, String reason)</h2>
	 * 
	 * <p>
	 * Accept an advisory. For admins only.
	 * </p>
	 * 
	 * <p>
	 * The CTF organizer should have given you a detailed explanation on which
	 * advisories to reject, which ones to accept, and how to grade them.
	 * </p>
	 */
	accept,

	/**
	 * <h2>void functions()</h2>
	 * 
	 * <p>
	 * Print this enum.
	 * </p>
	 */
	functions,

	/**
	 * <h2>int[] range(int elements)</h2>
	 * 
	 * <p>
	 * Disabled, except for admins.
	 * </p>
	 */
	range,

	/**
	 * <h2>Object[] array(Object... elements)</h2>
	 * 
	 * <p>
	 * Disabled, except for admins.
	 * </p>
	 */
	array,

	/**
	 * <h2>List<Object> list(Object... elements)</h2>
	 * 
	 * <p>
	 * Disabled, except for admins.
	 * </p>
	 */
	list,

	/**
	 * <h2>Map<Object, Object> dict(Object... elements)</h2>
	 * 
	 * <p>
	 * Disabled, except for admins. elements.length must be congruent to 2:0
	 * </p>
	 */
	dict,

	/**
	 * <h2>String genflags(int flagsToGenerate, String flagSeparator)</h2>
	 * 
	 * <p>
	 * Debug function. Do not use. Limited to admins.
	 * </p>
	 */
	genflags,

	/**
	 * <h2>void delete(int advisoryID)</h2>
	 * 
	 * <p>
	 * Delete an advisory. You should only delete junk. For bullshit advisories,
	 * use reject() instead. Limited to admins.
	 * </p>
	 */
	delete,

	/**
	 * <h2>void inchp(int teamID, pointsToAdd)</h2>
	 * 
	 * <p>
	 * Increase hacking/ethical/whatever points of teamID by pointsToAdd.
	 * </p>
	 * 
	 * <p>
	 * The CTF organizer should have told you what kinds of points 'hp' points
	 * are, and when to add/remove some. If not, do not use this function.
	 * Limited to admins.
	 * </p>
	 */
	inchp,

	/**
	 * <h2>void dechp(int teamID, pointsToAdd)</h2>
	 * 
	 * <p>
	 * Decrease hacking/ethical/whatever points of teamID by pointsToAdd.
	 * </p>
	 * 
	 * <p>
	 * The CTF organizer should have told you what kinds of points 'hp' points
	 * are, and when to add/remove some. If not, do not use this function.
	 * Limited to admins.
	 * </p>
	 */
	dechp,

	/**
	 * <h2>void cls()</h2>
	 * 
	 * <p>
	 * Send ANSI clear screen characters
	 * </p>
	 */
	cls,

	/**
	 * <h2>Team createteam(String teamName)</h2>
	 * 
	 * <p>
	 * Create a new participating team. Use the methods of the returned class
	 * instance to customize settings. One common use of this class is
	 * </p>
	 * 
	 * <p>
	 * createteam("teamname").sethost("teamHost")
	 * </p>
	 * 
	 * <p>
	 * Note that someone having access to an instance of class 'Team' can
	 * manipulate its properties in any way they like, whether they're admin or
	 * not. That is why there is no de-admin function. If you want to lose your
	 * admin status, you have to quit(), and reconnect.
	 * </p>
	 */
	createteam,

	/**
	 * <h2>void listteams()</h2>
	 * 
	 * <p>
	 * List all teams. Admins also get to see the current scores of the teams.
	 * Shortcut: lt()
	 * </p>
	 */
	listteams,

	/**
	 * <h2>Team getteam(int teamID)</h2>
	 * 
	 * <p>
	 * Return the team teamID or null if the team does not exist.
	 * </p>
	 * 
	 * <p>
	 * Note that someone having access to an instance of class 'Team' can
	 * manipulate its properties in any way they like, whether they're admin or
	 * not. That is why there is no de-admin function. If you want to lose your
	 * admin status, you have to quit(), and reconnect.
	 * </p>
	 */
	getteam,

	/**
	 * <h2>Class getClass(String canonicalClassName)</h2>
	 * 
	 * <p>
	 * Return an arbitrary java class. Note that if you use this function, you
	 * can totally mess up the whole scoring bot. Do not use it, unless you are
	 * a scoring bot developer. You have been warned.
	 * </p>
	 * 
	 * <p>
	 * Limited to admins.
	 * </p>
	 */
	getclass,

	/**
	 * <h2>void dir()</h2>
	 * 
	 * <p>
	 * List all global and local variables. If a global and a local variable
	 * have the same name, both are displayed. Local variables override global
	 * ones; if you want to access a global variable that has been overridden by
	 * a local variable, quit() and reconnect.
	 * </p>
	 */
	dir,

	/**
	 * <h2>Service createService(serviceName)</h2>
	 * 
	 * <p>
	 * Create a service. Use the methods of class 'Service' to set things up for
	 * the service. Common usage:
	 * </p>
	 * 
	 * <pre>
	 * Service s = createService(&quot;smtp&quot;)
	 * s.setinterval(300)
	 * s.setscript(&quot;smtpservice_testscript.py&quot;)
	 * </pre>
	 * 
	 * <p>
	 * Note that someone having access to an instance of class 'Service' can
	 * manipulate its properties in any way they like, whether they're admin or
	 * not. That is why there is no de-admin function. If you want to lose your
	 * admin status, you have to quit(), and reconnect.
	 * </p>
	 * 
	 * <p>
	 * Limited to admins.
	 * </p>
	 */
	createservice,

	/**
	 * <h2>Service getservice(int serviceID)</h2>
	 * 
	 * <p>
	 * Return the instance of class Service representing the service with the ID
	 * serviceID or null if no service with the specified ID is found.
	 * </p>
	 * 
	 * <p>
	 * Note that someone having access to an instance of class 'Service' can
	 * manipulate its properties in any way they like, whether they're admin or
	 * not. That is why there is no de-admin function. If you want to lose your
	 * admin status, you have to quit(), and reconnect.
	 * </p>
	 * 
	 * <p>
	 * Limited to admins.
	 * </p>
	 */
	getservice,

	/**
	 * <h2>void lt()</h2>
	 * 
	 * <p>
	 * Shortcut for listteams(). Refer to the documentation of listteams() for
	 * more information.
	 * </p>
	 */
	lt,
	/**
	 * <h2>void listservices()</h2>
	 * 
	 * <p>
	 * List all services, their checking intervals and testscripts.
	 * </p>
	 * 
	 * <p>
	 * Limited to admins.
	 * </p>
	 */
	listservices,

	/**
	 * <h2>void ls()</h2>
	 * 
	 * <p>
	 * Shortcut for listservices(). Refer to the listservices documentation for
	 * more information.
	 * </p>
	 */
	ls,

	/**
	 * <h2>void gc()</h2>
	 * 
	 * <p>
	 * Explicitely invoke the java garbage collector. You usually don't need
	 * this, except if you fiddeled with the getclass function, which you
	 * shouldn't.
	 * </p>
	 * 
	 * <p>
	 * Limited to admins.
	 * </p>
	 */
	gc,

	/**
	 * <h2>void tree(optional boolean showParseTree)</h2>
	 * 
	 * <p>
	 * Enables/Disables displaying the issued commands as parse trees. Nice, but
	 * not really useful feature, except for educational purposes :P
	 * </p>
	 */
	tree,

	/**
	 * <h2>void clearstats()</h2>
	 * 
	 * <p>
	 * Clear all stats tables
	 * </p>
	 */
	clearstats,

	/**
	 * <h2>void zeropoints()</h2>
	 * <p>
	 * Reset all teams' points to zero
	 * </p>
	 */
	zeropoints,

	/**
	 * <h2>void deleteflags()</h2>
	 * <p>
	 * Delete all flags
	 * </p>
	 */
	deleteflags
}
