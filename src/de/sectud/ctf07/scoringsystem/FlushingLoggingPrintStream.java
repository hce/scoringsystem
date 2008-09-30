package de.sectud.ctf07.scoringsystem;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Duplex PrintStream. This class replicates most of the methods the standard
 * PrintStream class offers, while writing the output to two separate streams,
 * one of them being intended to be used as a logging stream. The logging stream
 * is not flushed explicitly, the other is.
 * 
 * @author Hans-Christian Esperer
 * 
 */
public final class FlushingLoggingPrintStream extends Object {
	private final PrintStream flushedOutStream;

	private final PrintStream notflushedLogStream;

	public static final FlushingLoggingPrintStream NULLSTREAM = new FlushingLoggingPrintStream(
			null, null);

	public static final FlushingLoggingPrintStream STDERRSTREAM = new FlushingLoggingPrintStream(
			System.err, null);

	public FlushingLoggingPrintStream(PrintStream output, PrintStream logFile) {
		this.flushedOutStream = output;
		this.notflushedLogStream = logFile;
	}

	public void println(String s) {
		if (this.notflushedLogStream != null) {
			this.notflushedLogStream.print("O ");
			this.notflushedLogStream.println(s);
		}
		if (flushedOutStream != null) {
			this.flushedOutStream.println(s);
			this.flushedOutStream.flush();
		}
	}

	public void println() {
		println("");
	}

	public void print(String s) {
		if (this.notflushedLogStream != null) {
			this.notflushedLogStream.print(s);
		}
		if (this.flushedOutStream != null) {
			this.flushedOutStream.print(s);
			this.flushedOutStream.flush();
		}
	}

	public void write(byte[] b) throws IOException {
		if (this.notflushedLogStream != null) {
			this.notflushedLogStream.write(b);
		}
		if (this.flushedOutStream != null) {
			this.flushedOutStream.write(b);
			this.flushedOutStream.flush();
		}
	}

	public void format(String fmt, Object... args) {
		String s = String.format(fmt, args);
		print(s);
	}

	public void printf(String fmt, Object... args) {
		format(fmt, args);
	}

	public void close() {
		if (notflushedLogStream != null) {
			notflushedLogStream.println("L closing");
			notflushedLogStream.close();
		}
		if (flushedOutStream != null) {
			flushedOutStream.close();
		}
	}

	public void printError(Throwable t) {
		t.printStackTrace(System.err);
		println("Error: " + t.getLocalizedMessage());
	}
}
