package de.sectud.ctf07.scoringsystem;

/**
 * Testscript return codes
 * 
 * This class interprets the returncodes of testscripts. It can also calculate a
 * return value based on the specified state.
 * 
 * @author Hans-Christian Esperer
 * 
 */
public final class ReturnCode {
	public enum Success {
		SUCCESS("_[32mSUCCESS_[0m"), FAILURE("_[31mFAILURE_[0m");

		private final String string;

		private Success(String string) {
			this.string = string.replace('_', (char) 27);
		}

		@Override
		public String toString() {
			return this.string;
		}
	};

	public enum ErrorType {
		VALUE, BITFIELD
	}

	public enum ErrorValues {
		CONNECTIONERROR("Connection error"), WRONGFLAG("Wrong flag"), FUNCLACK(
				"Service lacks functionality"), TIMEOUT(
				"Service response timeout"), STATUSUNKNOWN("Status unknown"), GENERICERROR(
				"Error"), PROTOCOLERROR("Protocol violation");

		private final String description;

		private ErrorValues(String d) {
			this.description = d;
		}

		public String getDescription() {
			return this.description;
		}
	}

	private enum Colors {
		SUCCESS("green"), WARNING("#aaaa00"), CRITICAL("red");

		private final String color;

		private Colors(final String color) {
			this.color = color;
		}

		public String getColor() {
			return this.color;
		}
	}

	private final Success success;

	private final ErrorType errorType;

	private final ErrorValues reason;

	private final Colors color;

	public String getColor() {
		return color.getColor();
	}

	private ReturnCode(final Success success, final ErrorType errorType,
			final ErrorValues reason) {
		super();
		this.success = success;
		this.errorType = errorType;
		this.reason = reason;

		if (this.success == Success.SUCCESS) {
			this.color = Colors.SUCCESS;
		} else {
			if (this.errorType == ErrorType.BITFIELD) {
				this.color = Colors.CRITICAL;
			} else {
				switch (this.reason) {
				case FUNCLACK:
				case STATUSUNKNOWN:
					this.color = Colors.WARNING;
					break;
				default:
					this.color = Colors.CRITICAL;
				}
			}
		}
	}

	public int ordinal() {
		return success.ordinal() + (errorType.ordinal() << 1)
				+ (reason.ordinal() << 2);
	}

	/**
	 * Create a ReturnCode instance from the specified parameters.
	 * 
	 * @param failure
	 *            Type of failure / success
	 * @param reason
	 *            Reason for failure to have the specified value
	 * @return instance of class ReturnCode representing the specified values
	 */
	public static ReturnCode makeReturnCode(Success failure, ErrorValues reason) {
		return new ReturnCode(failure, ErrorType.VALUE, reason);
	}

	/**
	 * Create a ReturnCode instance from 'value'. Value is a return value,
	 * usually passed by a testscript to its parent process.
	 * 
	 * @param value
	 *            value to use
	 * @return according ReturnCode instance
	 */
	public static ReturnCode fromOrdinal(int value) {
		int suc = value % 2;
		int type = (value >> 1) % 2;
		int retval = value >> 2;
		Success s = Success.values()[suc];
		ErrorType t = ErrorType.values()[type];
		switch (t) {
		case BITFIELD:
			throw new IllegalArgumentException("Not yet implemented: BITFIELD");
		case VALUE:
			ErrorValues[] values = ErrorValues.values();
			if (retval >= values.length) {
				throw new IllegalArgumentException("Invalid return code "
						+ String.valueOf(retval));
			}
			ErrorValues v = values[retval];
			return new ReturnCode(s, t, v);
		}
		throw new IllegalArgumentException(
				"WTF!? Someone fiddled seriously with the code...");
	}

	public ErrorType getErrorType() {
		return errorType;
	}

	public ErrorValues getReason() {
		return reason;
	}

	public Success getSuccess() {
		return success;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((errorType == null) ? 0 : errorType.hashCode());
		result = PRIME * result + ((reason == null) ? 0 : reason.hashCode());
		result = PRIME * result + ((success == null) ? 0 : success.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ReturnCode other = (ReturnCode) obj;
		if (errorType == null) {
			if (other.errorType != null)
				return false;
		} else if (!errorType.equals(other.errorType))
			return false;
		if (reason == null) {
			if (other.reason != null)
				return false;
		} else if (!reason.equals(other.reason))
			return false;
		if (success == null) {
			if (other.success != null)
				return false;
		} else if (!success.equals(other.success))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("type: %s | reason: %s", success.toString(),
				reason.getDescription());
	}

	public static void main(String[] args) {
		ReturnCode t = ReturnCode.makeReturnCode(Success.FAILURE,
				ErrorValues.TIMEOUT);
		System.out.println(t.toString());
		System.out.println(t.ordinal());
	}
}
