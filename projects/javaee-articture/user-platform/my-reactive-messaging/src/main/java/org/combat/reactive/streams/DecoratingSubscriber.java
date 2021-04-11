package org.combat.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.logging.Logger;

/**
 * @author zhangwei
 * @Description DecoratingSubscribber
 * @Date: 2021/4/11 11:24
 */
public class DecoratingSubscriber<T> implements Subscriber<T> {

    private final Subscriber<T> source;

    private final Logger logger;

    private long maxRequest = -1;

    private boolean canceled = false;

    private boolean completed = false;

    private long requestCount = 0;

    public DecoratingSubscriber(Subscriber<T> source) {
        this.source = source;
        this.logger = Logger.getLogger(source.getClass().getName());
    }

    @Override
    public void onSubscribe(Subscription s) {
        source.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        assertRequest();

        if (isCompleted()) {
            logger.severe("The data subscription was completed, This method should not be invoked again!");
            return;
        }

        if (isCanceled()) {
            logger.warning(String.format("The Subscriber has canceled the data subscription, " +
                    " current data[%s] will be ignored!", t));
            return;
        }

        if (requestCount == maxRequest && maxRequest < Long.MAX_VALUE) {
            onComplete();
            logger.warning(String.format("The number of requests is up to the max threshold[%d]," +
                    " the data subscription is completed!", maxRequest));
            return;
        }

        next(t);
    }

    @Override
    public void onError(Throwable t) {
        source.onError(t);
    }

    @Override
    public void onComplete() {
        System.out.println("收到数据已完成");
        source.onComplete();
        completed = true;
    }

    private void assertRequest() {
        if (maxRequest < 1) {
            throw new IllegalStateException("the number of request must be initialized before " +
                    "Subscriber#onNext(Object) method, please set the positive number to " +
                    "Subscription#request(int) method on Publisher#subscribe(Subscriber) phase.");
        }
    }

    private void next(T t) {
        try {
            source.onNext(t);
        } catch (Throwable e) {
            onError(e);
        } finally {
            requestCount++;
        }
    }

    public void setMaxRequest(long maxRequest) {
        this.maxRequest = maxRequest;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void cancel() {
        this.canceled = true;
    }

    public Subscriber<T> getSource() {
        return source;
    }
}
