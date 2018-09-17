package jms2jms;

import java.io.File;

import org.ini4j.Wini;

public class Main {

	public static void main(String[] args) throws Exception {

		Wini ini = new Wini(new File(Config.NAME));
		Config.setConfig(ini);
		System.out.println(ini);

		JMSThread jms = new JMSThread();
		Thread jmsThread = new Thread(jms, "control thread");
		jmsThread.start();
		jmsThread.join();
	}
}
