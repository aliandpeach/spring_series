package com.yk.demo.activemq.service;

import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

/**
 * 消息发布客户端
 */
public class MessageClient {

    private TopicConnectionFactory factory;

    private TopicConnection connection;

    private TopicSession session;

    private Topic topic;

    private TopicPublisher publisher;

    private TopicSubscriber subscriber;
}
