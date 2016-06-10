package au.gov.ga.geodesy.port.adapter.sns;

import com.amazonaws.services.sns.AmazonSNSClient;

import au.gov.ga.geodesy.domain.model.event.Event;
import au.gov.ga.geodesy.port.NotificationPort;

public class SnsNotificationAdapter implements NotificationPort {

    @Override
    public void sendNotification(Event e) {
        new AmazonSNSClient().publish("arn:aws:sns:ap-southeast-2:094928090547:egeodesy-domain-events", "Test", "test");
    }
}
