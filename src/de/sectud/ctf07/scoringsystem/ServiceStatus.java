package de.sectud.ctf07.scoringsystem;

public final class ServiceStatus {
	private final ReturnCode returnCode;

	private final String statusMessage;

	private final int executingHost;

	public ReturnCode getReturnCode() {
		return returnCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public int getExecutingHost() {
		return executingHost;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((returnCode == null) ? 0 : returnCode.hashCode());
		result = PRIME * result
				+ ((statusMessage == null) ? 0 : statusMessage.hashCode());
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
		final ServiceStatus other = (ServiceStatus) obj;
		if (returnCode == null) {
			if (other.returnCode != null)
				return false;
		} else if (!returnCode.equals(other.returnCode))
			return false;
		if (statusMessage == null) {
			if (other.statusMessage != null)
				return false;
		} else if (!statusMessage.equals(other.statusMessage))
			return false;
		return true;
	}

	public ServiceStatus(final ReturnCode returnCode,
			final String statusMessage, final int executingHost) {
		super();
		this.returnCode = returnCode;
		this.statusMessage = statusMessage;
		this.executingHost = executingHost;
	}

}
