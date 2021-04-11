package org.combat.reactive.streams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhangwei
 * @Description SimplePublisher
 * @Date: 2021/4/11 11:20
 */
public class SimplePublisher<T> implements Publisher<T> {

    private List<Subscriber> subscribers = new LinkedList<>();

    @Override
    public void subscribe(Subscriber<? super T> s) {
        SubscriptionAdapter subscriptionAdapter = new SubscriptionAdapter(s);
        s.onSubscribe(subscriptionAdapter);

        subscribers.add(subscriptionAdapter.getSubscriber());
    }

    public void publish(T data) {
        subscribers.forEach(subscriber -> {
            subscriber.onNext(data);
        });
    }

    public static void main(String[] args) {
        SimplePublisher publisher = new SimplePublisher();

        publisher.subscribe(new BusinessSubscriber(3));

        for (int i = 0; i < 5; i++) {
            publisher.publish(i);
        }
    }

}
