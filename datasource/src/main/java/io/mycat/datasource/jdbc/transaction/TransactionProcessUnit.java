package io.mycat.datasource.jdbc.transaction;

import io.mycat.logTip.MycatLogger;
import io.mycat.logTip.MycatLoggerFactory;
import io.mycat.proxy.reactor.SessionThread;
import java.util.concurrent.LinkedTransferQueue;

public final class TransactionProcessUnit extends SessionThread {

  private static final MycatLogger LOGGER = MycatLoggerFactory
      .getLogger(TransactionProcessUnit.class);
  private volatile long startTime;
  private final LinkedTransferQueue<Runnable> blockingDeque = new LinkedTransferQueue<>();


  public void run(Runnable runnale) {
    blockingDeque.offer(runnale);
  }

  @Override
  public void run() {
    try {
      while (!isInterrupted()) {
        Runnable poll = null;
        try {
          poll = blockingDeque.take();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        if (poll != null) {
          this.startTime = System.currentTimeMillis();
          try {
            poll.run();
          } catch (Exception e) {
            LOGGER.error("", e);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("---------------------------------------------");
  }

  public long getStartTime() {
    return startTime;
  }

  public void close() {
    super.close();
//    interrupt();
//    blockingDeque.add(END);
  }
}