package jms2jms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JMSProducer extends Thread implements ExceptionListener {
	private static final Logger log = LogManager.getLogger();

	Connection connection;
	Session session;
	Destination destination;
	MessageProducer producer;
	String destTo;
	boolean isConnected = false;
	BlockingQueue<Message> queue;
	Exchanger<Boolean> exchange;

	public JMSProducer(String destTo, BlockingQueue<Message> queue, Exchanger<Boolean> exchange) {
		this.destTo = destTo;
		this.queue = queue;
		this.exchange = exchange;
	}

	public void connect() {
		log.info("producer -> [" + destTo + "] connect");
		try {
			connection = JMSConnectionFactory.getAWSActiveMQFactory(Config.TO.BROKER_URI, Config.TO.USERNAME, Config.TO.PASSWORD).createConnection();
			if (!Config.TO.CLIENTID.isEmpty())
				connection.setClientID(Config.TO.CLIENTID);
			connection.setExceptionListener(this);
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			if (Config.TO.DEST_TYPE.equals("queue"))
				destination = session.createQueue(Config.TO.DEST_NAME);
			else
				destination = session.createTopic(Config.TO.DEST_NAME);
			producer = session.createProducer(destination);

			isConnected = true;
		} catch (JMSException e) {
			log.error("producer -> [" + destTo + "] connect exception: " + e.getMessage());
		}
	}

	public void disconnect() {
		log.info("producer -> [" + destTo + "] disconnect");
		try {
			if (producer != null) {
				producer.close();
				producer = null;
			}
			if (session != null) {
				session.close();
				session = null;
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (JMSException e) {
			log.error("producer -> [" + destTo + "] disconnect exception: " + e.getMessage());
		}
	}

	public void send(String path, String file) throws JMSException, IOException {
		producer.send(session.createTextMessage(new String(Files.readAllBytes(Paths.get(path, file)))));
	}

	@Override
	public void onException(JMSException e) {
		log.error("producer -> [" + destTo + "] onException: " + e.getMessage());

		disconnect();
		isConnected = false;
		Thread.currentThread().interrupt();
	}

	@Override
	public void run() {
		Thread.currentThread().setName("jms producer");
		Message message = null;
		while (true) {
			try {
				if (this.isConnected == true) {
					if ((message = queue.take()) != null) {
						log.debug("producer -> exchange get " + message.getJMSMessageID());
						message.setJMSCorrelationID(message.getJMSMessageID());
						producer.send(message);
						log.debug("producer -> [" + destTo + "] put: " + message.getJMSCorrelationID());
						session.commit();
						log.info("producer -> [" + destTo + "] commit: " + message.getJMSCorrelationID());
						exchange.exchange(true);
					}
				} else {
					try {
						Thread.sleep(Config.COMMON.TIMEOUT);
					} catch (InterruptedException e) {
					}
				}
			} catch (JMSException | InterruptedException e) {
				try {
					queue.clear();
					exchange.exchange(false);
					session.rollback();
					log.error("producer -> [" + destTo + "] rollback");
					Thread.sleep(Config.COMMON.TIMEOUT);
				} catch (JMSException | InterruptedException ex) {
				}
			}
		}
	}
}
