package jms2jms;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;

import javax.jms.Message;

public class JMSThread implements Runnable {
	JMSConsumer consumer;
	JMSProducer producer;
	BlockingQueue<Message> exchangeData = new ArrayBlockingQueue<>(1);
	Exchanger<Boolean> exchangeResult = new Exchanger<>();

	boolean isClosed = false;

	public JMSThread() {
		producer = new JMSProducer(Config.TO.DEST_NAME, exchangeData, exchangeResult);
		producer.start();

		consumer = new JMSConsumer(Config.FROM.DEST_NAME, exchangeData, exchangeResult);
		consumer.start();
	}

	@Override
	public void run() {
		while (!isClosed) {
			if (!consumer.isConnected) {
				consumer.connect();
			}
			if (!producer.isConnected) {
				producer.connect();
			}
			try {
				Thread.sleep(Config.COMMON.TIMEOUT);
			} catch (InterruptedException e) {
			}
		}
	}

	public void stop() {
		consumer.stopReceive();
	}

	public void start() {
		consumer.startReceive();
	}

	public void close() {
		isClosed = true;
		consumer.disconnect();
		producer.disconnect();

	}
}
