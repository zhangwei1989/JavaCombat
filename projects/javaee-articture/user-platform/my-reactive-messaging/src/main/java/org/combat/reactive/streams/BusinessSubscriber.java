package org.combat.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @author zhangwei
 * @Description BusinessSubsriber
 * @Date: 2021/4/11 11:46
 */
public class BusinessSubscriber<T> implements Subscriber<T> {

    private Subscription subscription;

    private int count = 0;

    private final long maxRequest;

    public BusinessSubscriber(long maxRequest) {
        this.maxRequest = maxRequest;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        this.subscription.request(maxRequest);
    }

    @Override
    public void onNext(T t) {
        if (count++ > 2) {
            subscription.cancel();
            return;
        }

        System.out.println("收到数据： " + t);
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("遇到异常： " + t);
    }

    @Override
    public void onComplete() {
        System.out.println("收到数据完成");
    }


}
