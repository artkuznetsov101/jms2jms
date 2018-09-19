package jms2jms;

import org.ini4j.Wini;

public class Config {

	public static String NAME = "jms2jms.ini";

	public static class FROM {
		static String BROKER_URI;
		static String DEST_TYPE;
		static String DEST_NAME;
		static String USERNAME;
		static String PASSWORD;
		static String CLIENTID;
		static String SUBSCRIPTION_NAME;
		static int TIMEOUT;
	}

	public static class TO {
		static String BROKER_URI;
		static String DEST_TYPE;
		static String DEST_NAME;
		static String USERNAME;
		static String PASSWORD;
		static String CLIENTID;
		static String SUBSCRIPTION_NAME;
		static int TIMEOUT;
	}

	public static class COMMON {
		static int TIMEOUT;
	}

	public static void setConfig(Wini ini) {
		if ((Config.COMMON.TIMEOUT = ini.get("COMMON", "TIMEOUT", Integer.TYPE).intValue()) == 0)
			throw new IllegalArgumentException("COMMON->TIMEOUT parameter not specified in ini file. Exit");

		if ((Config.FROM.BROKER_URI = ini.get("FROM", "BROKER_URI")) == null)
			throw new IllegalArgumentException("FROM->BROKER_URI parameter not specified in ini file. Exit");
		if ((Config.FROM.DEST_TYPE = ini.get("FROM", "DEST_TYPE")) == null)
			throw new IllegalArgumentException("FROM->DEST_TYPE parameter not specified in ini file. Exit");
		if ((Config.FROM.DEST_NAME = ini.get("FROM", "DEST_NAME")) == null)
			throw new IllegalArgumentException("FROM->DEST_NAME parameter not specified in ini file. Exit");

		Config.FROM.USERNAME = ini.get("FROM", "USERNAME");
		Config.FROM.PASSWORD = ini.get("FROM", "PASSWORD");
		Config.FROM.CLIENTID = ini.get("FROM", "CLIENTID");
		Config.FROM.SUBSCRIPTION_NAME = ini.get("FROM", "SUBSCRIPTION_NAME");

		if ((Config.TO.BROKER_URI = ini.get("TO", "BROKER_URI")) == null)
			throw new IllegalArgumentException("TO->BROKER_URI parameter not specified in ini file. Exit");
		if ((Config.TO.DEST_TYPE = ini.get("TO", "DEST_TYPE")) == null)
			throw new IllegalArgumentException("TO->DEST_TYPE parameter not specified in ini file. Exit");
		if ((Config.TO.DEST_NAME = ini.get("TO", "DEST_NAME")) == null)
			throw new IllegalArgumentException("TO->DEST_NAME parameter not specified in ini file. Exit");

		Config.TO.USERNAME = ini.get("TO", "USERNAME");
		Config.TO.PASSWORD = ini.get("TO", "PASSWORD");
		Config.TO.CLIENTID = ini.get("TO", "CLIENTID");
		Config.TO.SUBSCRIPTION_NAME = ini.get("TO", "SUBSCRIPTION_NAME");
	}
}
