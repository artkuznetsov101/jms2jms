package jms2jms;

import java.io.File;
import java.text.SimpleDateFormat;

import org.ini4j.Wini;

public class Main {

	private static SimpleDateFormat formatterWithTimezone = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:Z");

	public static void main(String[] args) throws Exception {

		Wini ini = new Wini(new File(Config.NAME));
		Config.setConfig(ini);
		System.out.println(ini);

		JMSThread jms = new JMSThread();
		Thread jmsThread = new Thread(jms, "control thread");
		jmsThread.start();
		jmsThread.join();
	}

	public static String getTimestampWithTimezone() {
		return formatterWithTimezone.format(System.currentTimeMillis());
	}

}
